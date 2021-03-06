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
package com.subterranean_security.crimson.server.storage;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.singular.AKeySimple;
import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.storage.ServerStorageFacility;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedList;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedMap;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.net.sequences.Listener.ListenerConfig;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;

public class ServerDatabase extends BasicDatabase implements ServerStorageFacility {
	private static final Logger log = LoggerFactory.getLogger(ServerDatabase.class);

	public ServerDatabase(File sqlite) {
		super(sqlite);
	}

	@Override
	public void initialize() throws IOException, ClassNotFoundException, SQLException {
		super.initialize();
		if (!isConstructed()) {
			construct();
		}
	}

	private void construct() throws SQLException {
		execute("CREATE TABLE `users` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT, `Username` TEXT, `Salt` TEXT, `Hash` TEXT);");
	}

	private boolean isConstructed() {
		try {
			return db.getMetaData().getTables(null, null, "users", null).next();
		} catch (SQLException e) {
			return false;
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
			String hash = "";

			if (rs.next()) {
				hash = rs.getString("Hash");
			} else {
				log.debug("Could not get Salt");
				throw new Exception();
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

	public boolean addLocalUser(String user, String password, ViewerPermissions permissions) {
		if (userExists(user)) {
			log.info("This user already exists: " + user);
			return false;
		}

		String salt = CryptoUtil.genSalt();
		String hash = CryptoUtil.hashCrimsonPassword(password, salt);

		try {
			PreparedStatement stmt = db.prepareStatement("INSERT INTO users (Username, Salt, Hash ) VALUES (?,?,?);");
			stmt.setString(1, user);
			stmt.setString(2, salt);
			stmt.setString(3, hash);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// create ViewerProfile
		ViewerProfile vp = new ViewerProfile();
		vp.set(AKeySimple.VIEWER_USER, user);
		vp.setPermissions(permissions);

		try {
			CachedMap<Integer, ViewerProfile> map = (CachedMap<Integer, ViewerProfile>) getCachedCollection(
					"profiles.viewers");
			map.put(vp.getCvid(), vp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void resetServer() {
		resetBasic();

		store("auth.methods", new CachedList<AuthMethod>());
		store("listeners", new CachedList<ListenerConfig>());
		store("profiles.clients", new CachedMap<Integer, ClientProfile>());
		store("profiles.viewers", new CachedMap<Integer, ViewerProfile>());
		store("profiles.idcount", 0);
	}

}