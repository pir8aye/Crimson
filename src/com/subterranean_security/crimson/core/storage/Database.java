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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.rowset.serial.SerialException;

import com.subterranean_security.crimson.core.Logger;
import com.subterranean_security.crimson.core.utility.CUtil;
import com.subterranean_security.crimson.core.utility.ObjectTransfer;

public abstract class Database extends Thread implements AutoCloseable {

	private HashMap<String, Object> map = new HashMap<String, Object>();
	private HashMap<Integer, Object> heap = new HashMap<Integer, Object>();

	protected Connection db;
	public File dfile;

	public void init(File dfile) throws Exception {
		this.dfile = dfile;

		try {
			// load the driver if unloaded
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			Logger.error("Missing Dependency: sqlite");
			throw e;
		}

		try {
			db = DriverManager.getConnection("jdbc:sqlite:" + dfile.getAbsolutePath());
		} catch (SQLException e) {
			Logger.error("Could not connect to database: " + dfile.getAbsolutePath());
			throw new Exception("Could not create database: " + dfile.getAbsolutePath());
		}

		Logger.debug("Initialized database: " + dfile.getAbsolutePath());

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
			Logger.error("Query for: " + id + " failed");
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
	public Object get(int id) throws Exception {
		if (heap.containsKey(id)) {
			return heap.get(id);
		} else {

			return ObjectTransfer.Default.deserialize(query(id));

		}

	}

	/**
	 * Query the map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private Object query(String key) throws Exception {

		// get the object from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			Logger.error("Could not query key: " + key);
			throw new Exception();
		} else {
			return ObjectTransfer.Default.deserialize(rs.getBytes("Data"));
		}

	}

	/**
	 * Get String from map
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getString(String key) throws Exception {

		if (map.containsKey(key)) {
			return (String) map.get(key);
		}

		// get the string from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			Logger.error("Could not query key: " + key);
			throw new Exception();
		} else {
			Logger.debug("Returning: " + rs.getString("Data") + " for key: " + key);
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
	public Long getLong(String key) throws Exception {

		if (map.containsKey(key)) {
			return (long) map.get(key);
		}

		// get the string from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			Logger.error("Could not query key: " + key);
			throw new Exception();
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
	public Integer getInteger(String key) throws Exception {

		if (map.containsKey(key)) {
			return (int) map.get(key);
		}

		// get the integer from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			Logger.error("Could not query key: " + key);
			throw new Exception();
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
	public Boolean getBoolean(String key) throws Exception {

		if (map.containsKey(key)) {
			return (boolean) map.get(key);
		}

		// get the boolean from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM map WHERE `Name`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (!rs.next()) {
			Logger.error("Could not query key: " + key);
			throw new Exception();
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
	public Object getObject(String key) throws Exception {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		return query(key);

	}

	/**
	 * Store serialized object in map
	 * 
	 * @param s
	 * @param o
	 */
	public void storeObject(String s, Object o) {
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

	public boolean isFirstRun() {

		try {
			return (getInteger("runs") == 0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}

	}

	private void flushMap() throws SerialException, SQLException {
		for (String key : map.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT INTO map(Name, Data) VALUES (?, ?)");
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

	private void flushHeap() throws SQLException {
		for (Integer id : heap.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT INTO heap(Id, Data) VALUES (?, ?)");
			stmt.setInt(1, id);
			stmt.setBytes(2, ObjectTransfer.Default.serialize(heap.get(id)));
			stmt.executeUpdate();
		}
	}

	private int reserveRow() {
		try {
			String placeholder = CUtil.Misc.nameGen(64);
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

	protected void execute(String s) {
		try {
			Statement statement = db.createStatement();
			statement.setQueryTimeout(4);
			statement.executeUpdate(s);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
