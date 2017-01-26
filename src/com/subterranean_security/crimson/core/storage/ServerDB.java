/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.core.storage;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.MemList;
import com.subterranean_security.crimson.core.misc.MemMap;
import com.subterranean_security.crimson.core.proto.Listener.ListenerConfig;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.server.ServerStore;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.JarUtil;

public class ServerDB extends Database {
	private static final Logger log = LoggerFactory.getLogger(ServerDB.class);

	public ServerDB(File dfile) throws Exception {
		if (!dfile.exists()) {
			// copy the template
			log.debug("Copying database template to: " + dfile.getAbsolutePath());
			JarUtil.extract("com/subterranean_security/crimson/core/storage/server-template.db",
					dfile.getAbsolutePath());
		}
		init(dfile);
		if (isFirstRun()) {
			this.hardReset();
		}

	}

	public boolean userExists(String user) {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE `Username`=?");
			stmt.setString(1, user);

			ResultSet rs = stmt.executeQuery();
			return rs.next();

		} catch (SQLException e) {
			log.error("Error during user query");
			return true;
		}
	}

	public String getSalt(String user) {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE `Username`=?");
			stmt.setString(1, user);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				return rs.getString("Salt");
			}
		} catch (SQLException e) {
			log.error("Error during salt query");

		}
		return null;
	}

	public boolean validLogin(String user, String password) {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE `Username`=?");
			stmt.setString(1, user);

			ResultSet rs = stmt.executeQuery();
			String UID = "";
			String hash = "";

			if (rs.next()) {
				UID = rs.getString("UID");
				hash = rs.getString("Hash");
			} else {
				log.debug("Could not get UID and Salt");
				throw new Exception();
			}

			if (!ServerStore.Databases.loaded_viewers.containsKey(UID)) {
				ServerStore.Databases.loaded_viewers.put(UID,
						new ClientDB(new File(Common.Directories.var + File.separator + UID + ".db")));
			}

			return hash.equals(password);

		} catch (Exception e) {
			log.error("Error during login query");

		}
		return false;
	}

	public boolean changePassword(String user, String password) {
		String salt = CryptoUtil.genSalt();
		String hash = CryptoUtil.hashCrimsonPassword(password, salt);

		try {
			PreparedStatement stmt = db.prepareStatement("UPDATE users SET Salt=?,Hash=? WHERE Username=?;");
			stmt.setString(1, salt);
			stmt.setString(2, hash);
			stmt.setString(3, user);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String getUID(String username) {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE `Username`=?");
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("UID");
			} else {
				log.debug("Could not get UID");
				throw new Exception();
			}

		} catch (Exception e) {
			log.error("Error during login query");

		}
		return null;
	}

	public boolean addLocalUser(String user, String password, ViewerPermissions permissions) {
		if (userExists(user)) {
			log.info("This user already exists: " + user);
			return false;
		}

		String salt = CryptoUtil.genSalt();
		String hash = CryptoUtil.hashCrimsonPassword(password, salt);
		String UID = RandomUtil.randString(4);

		try {
			PreparedStatement stmt = db
					.prepareStatement("INSERT INTO users (Username, Salt, UID, Hash ) VALUES (?,?,?,?);");
			stmt.setString(1, user);
			stmt.setString(2, salt);
			stmt.setString(3, UID);
			stmt.setString(4, hash);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		try {
			ClientDB udb = new ClientDB(new File(dfile.getParent() + File.separator + UID + ".db"));
			udb.master = CryptoUtil.hashCrimsonPassword(password.toCharArray(), salt);
			udb.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		// create ViewerProfile
		ViewerProfile vp = new ViewerProfile(IDGen.cvid());
		vp.setUser(user);
		vp.setPermissions(permissions);

		try {
			// TODO use serverstore and fix cinstaller
			MemMap<Integer, ViewerProfile> map = (MemMap<Integer, ViewerProfile>) getObject("profiles.viewers");
			map.setDatabase(this);
			map.put(vp.getCvid(), vp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void softReset() {

		this.storeObject("auth.methods", new MemList<AuthMethod>());
		this.storeObject("listeners", new ArrayList<ListenerConfig>());
		this.storeObject("profiles.clients", new MemMap<Integer, ClientProfile>());
		this.storeObject("profiles.viewers", new MemMap<Integer, ViewerProfile>());
		this.storeObject("profiles.idcount", 0);
		super.softReset();
	}

	@Override
	public void hardReset() {
		this.softReset();
		super.hardReset();

	}

}