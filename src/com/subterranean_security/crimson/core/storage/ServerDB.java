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

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.utility.CUtil;
import com.subterranean_security.crimson.core.utility.Crypto;
import com.subterranean_security.crimson.server.ServerStore;

public class ServerDB extends Database {

	public ServerDB(File dfile) throws Exception {
		super(dfile);
		execute("CREATE TABLE IF NOT EXISTS users (Id INTEGER PRIMARY KEY AUTOINCREMENT, Username VARCHAR(16), Salt VARCHAR(128), UID VARCHAR(8) );");
		if (isEmpty()) {
			Defaults.System.set(this, true);
		}
		try {
			storeObject("runs", getInteger("runs") + 1);
		} catch (Exception e) {
			Logger.error("Could not update run count");
			e.printStackTrace();
		}
	}

	public boolean userExists(String user) {
		try {
			PreparedStatement stmt = db.prepareStatement("SELECT * FROM users WHERE `Username`=?");
			stmt.setString(1, user);

			ResultSet rs = stmt.executeQuery();
			return rs.next();

		} catch (SQLException e) {
			Logger.rerror("Error during user query");
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
			Logger.rerror("Error during salt query");

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
				Logger.debug("Could not get UID and Salt");
				throw new Exception();
			}

			if (!ServerStore.Databases.loaded_users.containsKey(UID)) {
				ServerStore.Databases.loaded_users.put(UID,
						new LocalClientDB(new File(Common.var + File.separator + UID + ".db")));
			}

			return ServerStore.Databases.loaded_users.get(UID).getString("MAGIC").equals("subterranean");

		} catch (Exception e) {
			Logger.rerror("Error during login query");

		}
		return false;
	}

	public boolean addUser(String user, String password) {
		if (userExists(user)) {
			Logger.info("This user already exists: " + user);
			return false;
		}

		String salt = Crypto.genSalt();
		String UID = CUtil.Misc.nameGen(8);

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
			LocalClientDB udb = new LocalClientDB(new File(dfile.getParent() + File.separator + UID + ".db"));
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