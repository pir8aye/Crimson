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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.server.ServerStore;

public class ServerDB extends Database {
	private static final Logger log = LoggerFactory.getLogger(ServerDB.class);

	public ServerDB(File dfile) throws Exception {
		if (!dfile.exists()) {
			// copy the template
			log.debug("Copying database template to: " + dfile.getAbsolutePath());
			CUtil.Files.extract("com/subterranean_security/crimson/core/storage/server-template.db",
					dfile.getAbsolutePath());
		}
		init(dfile);
		if (isFirstRun()) {
			Defaults.hardReset(this);
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
			String salt = "";
			String UID = "";

			if (rs.next()) {
				UID = rs.getString("UID");
				salt = rs.getString("Salt");
			} else {
				log.debug("Could not get UID and Salt");
				throw new Exception();
			}

			if (!ServerStore.Databases.loaded_viewers.containsKey(UID)) {
				ServerStore.Databases.loaded_viewers.put(UID,
						new ViewerDB(new File(Common.var + File.separator + UID + ".db")));
			}

			return ServerStore.Databases.loaded_viewers.get(UID).getString("MAGIC").equals("subterranean");

		} catch (Exception e) {
			log.error("Error during login query");

		}
		return false;
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

	public boolean addUser(String user, String password) {
		if (userExists(user)) {
			log.info("This user already exists: " + user);
			return false;
		}

		String salt = Crypto.genSalt();
		String UID = CUtil.Misc.nameGen(4);

		try {
			PreparedStatement stmt = db.prepareStatement("INSERT INTO users (Username, Salt, UID ) VALUES (?,?,?);");
			stmt.setString(1, user);
			stmt.setString(2, salt);
			stmt.setString(3, UID);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		try {
			ViewerDB udb = new ViewerDB(new File(dfile.getParent() + File.separator + UID + ".db"));
			udb.master = Crypto.hashPass(password.toCharArray(), salt);
			udb.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		return true;
	}

}