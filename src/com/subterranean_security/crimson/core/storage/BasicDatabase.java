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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.misc.MemList;
import com.subterranean_security.crimson.core.misc.ObjectTransfer;
import com.subterranean_security.crimson.core.proto.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.core.proto.Report.MI_Report;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

public class BasicDatabase implements StorageFacility {
	private static final Logger log = LoggerFactory.getLogger(BasicDatabase.class);

	private HashMap<String, Object> map = new HashMap<String, Object>();
	private HashMap<Integer, Object> heap = new HashMap<Integer, Object>();

	protected Connection db;
	public File sqlite;

	public BasicDatabase(String pref, File sqlite) {
		this.sqlite = sqlite;
	}

	@Override
	public void initialize() throws IOException, ClassNotFoundException, SQLException {
		initSql();
	}

	public void initSql() throws IOException, ClassNotFoundException, SQLException {

		// load driver
		Class.forName("org.sqlite.JDBC");

		// create database if needed
		sqlite.createNewFile();

		// create connection
		db = DriverManager.getConnection("jdbc:sqlite:" + sqlite.getAbsolutePath());

		// construct database if needed
		if (!isTableConstructed()) {
			construct();
		}

		// increase run count
		// try {
		// store("runs", getInteger("runs") + 1);
		// } catch (Exception e) {
		// log.error("Could not update run count");
		// e.printStackTrace();
		// }
	}

	/**
	 * Construct a basic database
	 * 
	 * @throws SQLException
	 */
	private void construct() throws SQLException {
		execute("CREATE TABLE `map` (`Name` TEXT UNIQUE, `Data` BLOB);");
		execute("INSERT INTO `map` VALUES ('runs','0');");
		execute("CREATE TABLE `heap` (`Id` INTEGER PRIMARY KEY AUTOINCREMENT, `Data`  BLOB);");
	}

	private boolean isTableConstructed() {
		try {
			return db.getMetaData().getTables(null, null, "map", null).next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Query the heap
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private byte[] query(int id) throws Exception {

		PreparedStatement stmt = db.prepareStatement("SELECT * FROM heap WHERE `Id`=?");
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			log.error("Query for: " + id + " failed");
			throw new Exception();
		} else {
			return rs.getBytes("Data");
		}

	}

	/**
	 * Get serialized object from heap
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Object getObject(int id) throws SQLException, NoSuchElementException {
		if (!heap.containsKey(id)) {
			try {
				heap.put(id, ObjectTransfer.Default.deserialize(query(id)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return heap.get(id);

	}

	/**
	 * Query the map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private Object query(String key) throws SQLException, NoSuchElementException {

		// get the object from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			throw new NoSuchElementException();
		} else {
			try {
				return ObjectTransfer.Default.deserialize(rs.getBytes("Data"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * Get String from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getString(String key) throws SQLException, NoSuchElementException {

		if (map.containsKey(key)) {
			return (String) map.get(key);
		}

		// get the string from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			throw new NoSuchElementException();
		} else {
			return rs.getString("Data");
		}

	}

	/**
	 * Get Long from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public long getLong(String key) throws SQLException, NoSuchElementException {

		if (map.containsKey(key)) {
			return (long) map.get(key);
		}

		// get the string from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			throw new NoSuchElementException();
		} else {
			return rs.getLong("Data");
		}

	}

	/**
	 * Get Integer from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public int getInteger(String key) throws SQLException, NoSuchElementException {

		if (map.containsKey(key)) {
			return (int) map.get(key);
		}

		// get the integer from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			throw new NoSuchElementException();
		} else {
			return rs.getInt("Data");
		}

	}

	/**
	 * Get Boolean from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean getBoolean(String key) throws SQLException, NoSuchElementException {

		if (map.containsKey(key)) {
			return (boolean) map.get(key);
		}

		// get the boolean from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			throw new NoSuchElementException();
		} else {
			return rs.getBoolean("Data");
		}

	}

	/**
	 * Get serialized object from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object getObject(String key) throws SQLException, NoSuchElementException {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		map.put(key, query(key));
		return map.get(key);

	}

	/**
	 * Store serialized object in map
	 * 
	 * @param s
	 * @param o
	 */
	@Override
	public void store(String s, Object o) {
		synchronized (map) {
			map.put(s, o);
		}

	}

	/**
	 * Store serialized object in heap
	 * 
	 * @param o
	 * @return
	 */
	public int store(Object o) {
		int id = reserveRow();
		heap.put(id, o);
		return id;

	}

	private int reserveRow() {
		try {
			String placeholder = RandomUtil.randString(64);
			PreparedStatement stmt = db.prepareStatement("INSERT INTO heap(Data) VALUES (?)");
			stmt.setBytes(1, placeholder.getBytes());
			stmt.executeUpdate();

			PreparedStatement stmt2 = db.prepareStatement("SELECT * FROM heap WHERE `Data`=?");
			stmt2.setBytes(1, placeholder.getBytes());

			ResultSet rs = stmt2.executeQuery();
			if (!rs.next()) {

				throw new Exception();
			} else {
				return rs.getInt("Id");
			}
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public void close() {
		try {
			flushMap();
			flushHeap();
		} catch (SerialException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void flushMap() throws SerialException, SQLException {
		for (String key : map.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT OR REPLACE INTO map(Name, Data) VALUES (?, ?)");
			stmt.setString(1, key);
			if (map.get(key) instanceof String) {
				stmt.setString(2, (String) map.get(key));
			} else if (map.get(key) instanceof Integer) {
				stmt.setInt(2, (int) map.get(key));
			} else if (map.get(key) instanceof Long) {
				stmt.setLong(2, (long) map.get(key));
			} else if (map.get(key) instanceof Boolean) {
				stmt.setBoolean(2, (boolean) map.get(key));
			} else {
				stmt.setBytes(2, ObjectTransfer.Default.serialize(map.get(key)));
			}

			stmt.executeUpdate();
		}

	}

	public void flushHeap() throws SQLException {
		for (Integer id : heap.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT OR REPLACE INTO heap(Id, Data) VALUES (?, ?)");
			stmt.setInt(1, id);
			stmt.setBytes(2, ObjectTransfer.Default.serialize(heap.get(id)));
			stmt.executeUpdate();
		}
	}

	public void delete(String key) {
		synchronized (map) {
			map.remove(key);
			try {
				PreparedStatement stmt = db.prepareStatement("DELETE FROM map WHERE `Name`=?");
				stmt.setString(1, key);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void delete(int id) {
		synchronized (heap) {
			map.remove(id);
			try {
				PreparedStatement stmt = db.prepareStatement("DELETE FROM heap WHERE `Id`=?");
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void execute(String s) throws SQLException {
		db.createStatement().executeUpdate(s);
	}

	@Override
	public String getString(int id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInteger(int id) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(int id) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getBoolean(int id) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetViewer() {
		resetBasic();

		store("show_detail", true);
		store("login.recents", new ArrayList<String>());
		store("profiles.clients", new MemList<ClientProfile>());
	}

	public void resetClient() {
		resetBasic();

		store("install.timestamp", new Date().getTime());
		store("login-times", new ArrayList<Long>());
		store("login-ips", new ArrayList<String>());

		store("keylogger.buffer", new MemList<EV_KEvent>());
	}

	public void resetBasic() {
		store("cvid", 0);
		store("lcvid", new HashMap<String, Integer>());
		store("reports.buffer", new ArrayList<MI_Report>());
		store("crimson.version", Common.version);
		store("crimson.build_number", Common.build);

		store("error_reporting", true);
		store("reports.sent", 0);
		store("language", "en");
	}

	@Override
	// SERVER ONLY
	public boolean userExists(String user) {
		return false;
	}

	@Override
	// SERVER ONLY
	public String getSalt(String user) {
		return null;
	}

	@Override
	// SERVER ONLY
	public boolean validLogin(String user, String password) {
		return false;
	}

	@Override
	// SERVER ONLY
	public boolean changePassword(String user, String password) {
		return false;
	}

	@Override
	// SERVER ONLY
	public boolean addLocalUser(String user, String password, ViewerPermissions permissions) {
		return false;
	}

}
