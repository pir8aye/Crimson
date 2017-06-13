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
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.proto.Report.MI_Report;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedCollection;
import com.subterranean_security.crimson.core.struct.collections.cached.CachedList;
import com.subterranean_security.crimson.core.util.SerialUtil;
import com.subterranean_security.crimson.universal.Universal;

public class BasicDatabase extends DoubleCacheDatabase implements BasicStorageFacility {
	private static final Logger log = LoggerFactory.getLogger(BasicDatabase.class);

	/**
	 * Initialize a new mysql database
	 * 
	 * @param url
	 *            The location of the database
	 * @param user
	 *            The database username
	 * @param pass
	 *            The user password
	 */
	public BasicDatabase(String url, String user, String pass) {
		this();
		if (url == null)
			throw new IllegalArgumentException();
		if (user == null)
			throw new IllegalArgumentException();
		if (pass == null)
			throw new IllegalArgumentException();

		this.url = url;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * Initialize a new sqlite database
	 * 
	 * @param sqlite
	 *            The file for the database
	 */
	public BasicDatabase(File sqlite) {
		this();
		if (sqlite == null)
			throw new IllegalArgumentException("File is null");

		this.sqlite = sqlite;
	}

	private BasicDatabase() {
		map = new HashMap<String, Object>();
		heap = new HashMap<Integer, Object>();
	}

	@Override
	public void initialize() throws IOException, ClassNotFoundException, SQLException {
		super.initialize();

		// construct database if needed
		if (!isConstructed()) {
			construct();
		}
	}

	/**
	 * Construct the tables which consist of:<br>
	 * <ul>
	 * <li>A "map" which associates Strings and stored objects.</li>
	 * <li>A "heap" which associates Integers and stored objects.</li>
	 * </ul>
	 * 
	 * @throws SQLException
	 */
	private void construct() throws SQLException {
		execute("CREATE TABLE `MAP` (`Id` TEXT UNIQUE, `Data` BLOB);");
		execute("CREATE TABLE `HEAP` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT, `Data`  BLOB);");
	}

	/**
	 * @return True if this database has been constructed
	 */
	private boolean isConstructed() {
		try {
			return db.getMetaData().getTables(null, null, "MAP", null).next()
					&& db.getMetaData().getTables(null, null, "HEAP", null).next();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get a Object from the map.
	 * 
	 * @param id
	 * @return The Object associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public Object getObject(int id) {
		if (!heap.containsKey(id)) {
			try {
				heap.put(id, SerialUtil.deserialize(query(id)));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		}
		return heap.get(id);

	}

	/**
	 * Get a Object from the heap.
	 * 
	 * @param key
	 * @return The Object associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public Object getObject(String key) {
		if (!map.containsKey(key)) {
			try {
				map.put(key, SerialUtil.deserialize(query(key)));
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return map.get(key);

	}

	/**
	 * Get a String from the map.
	 * 
	 * @param key
	 * @return The String associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public String getString(String key) {
		if (!map.containsKey(key)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM MAP WHERE `Id`=?")) {
				stmt.setString(1, key);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					map.put(key, rs.getString("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (String) map.get(key);
	}

	/**
	 * Get a Long from the map.
	 * 
	 * @param key
	 * @return The Long associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public long getLong(String key) {
		if (!map.containsKey(key)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM MAP WHERE `Id`=?")) {
				stmt.setString(1, key);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					map.put(key, rs.getLong("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (long) map.get(key);
	}

	/**
	 * Get a Integer from the map.
	 * 
	 * @param key
	 * @return The Integer associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public int getInteger(String key) {
		if (!map.containsKey(key)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM MAP WHERE `Id`=?")) {
				stmt.setString(1, key);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					map.put(key, rs.getInt("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (int) map.get(key);
	}

	/**
	 * Get a Boolean from the map.
	 * 
	 * @param key
	 * @return The Boolean associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public boolean getBoolean(String key) {
		if (!map.containsKey(key)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM MAP WHERE `Id`=?")) {
				stmt.setString(1, key);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					map.put(key, rs.getBoolean("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (boolean) map.get(key);

	}

	/**
	 * Get a String from the heap.
	 * 
	 * @param id
	 * @return The String associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public String getString(int id) {
		if (!heap.containsKey(id)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM HEAP WHERE `Id`=?")) {
				stmt.setInt(1, id);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					heap.put(id, rs.getString("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (String) heap.get(id);
	}

	/**
	 * Get a Integer from the heap.
	 * 
	 * @param id
	 * @return The Integer associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public int getInteger(int id) {
		if (!heap.containsKey(id)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM HEAP WHERE `Id`=?")) {
				stmt.setInt(1, id);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					heap.put(id, rs.getInt("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (int) heap.get(id);
	}

	/**
	 * Get a Long from the heap.
	 * 
	 * @param id
	 * @return The Long associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public long getLong(int id) {
		if (!heap.containsKey(id)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM HEAP WHERE `Id`=?")) {
				stmt.setInt(1, id);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					heap.put(id, rs.getLong("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (long) heap.get(id);
	}

	/**
	 * Get a Boolean from the heap.
	 * 
	 * @param id
	 * @return The Boolean associated with the specified key or null
	 * @throws NoSuchElementException
	 */
	public boolean getBoolean(int id) {
		if (!heap.containsKey(id)) {
			try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM HEAP WHERE `Id`=?")) {
				stmt.setInt(1, id);

				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					heap.put(id, rs.getBoolean("Data"));
				} else
					throw new NoSuchElementException();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return (boolean) heap.get(id);
	}

	/**
	 * Get a CachedCollection from the map.
	 * 
	 * @param key
	 * @return The collection associated with the specified key or null
	 */
	public CachedCollection getCachedCollection(String key) {
		CachedCollection cc = (CachedCollection) getObject(key);
		cc.setDatabase(this);
		return cc;
	}

	public void resetViewer() {
		resetBasic();
	}

	public void resetClient() {
		resetBasic();

		store("install.timestamp", new Date().getTime());
		store("login-times", new ArrayList<Long>());
		store("login-ips", new ArrayList<String>());

		store("keylogger.buffer", new CachedList<EV_KEvent>());
	}

	public void resetBasic() {
		store("cvid", 0);
		store("lcvid", new HashMap<String, Integer>());
		store("reports.buffer", new ArrayList<MI_Report>());
		store("crimson.version", Universal.version);
		store("crimson.build_number", Universal.build);

		store("error_reporting", true);
		store("reports.sent", 0);
		store("language", "en");
	}

}
