/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.NoSuchElementException;

import com.subterranean_security.crimson.core.util.SerialUtil;

public abstract class DoubleCacheDatabase extends AbstractDatabase {

	protected Map<String, Object> map;
	protected Map<Integer, Object> heap;

	/**
	 * Query the heap
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	protected byte[] query(int id) throws SQLException {

		PreparedStatement stmt = db.prepareStatement("SELECT * FROM HEAP WHERE `Id`=?");
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getBytes("Data");
		} else {
			throw new NoSuchElementException();
		}

	}

	/**
	 * Query the map
	 * 
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	protected byte[] query(String key) throws SQLException {

		// get the object from the database
		PreparedStatement stmt = db.prepareStatement("SELECT * FROM MAP WHERE `Id`=?");
		stmt.setString(1, key);

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getBytes("Data");
		} else {
			throw new NoSuchElementException();
		}
	}

	public void delete(String key) {
		synchronized (map) {
			map.remove(key);
			try {
				PreparedStatement stmt = db.prepareStatement("DELETE FROM MAP WHERE `Id`=?");
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
			heap.remove(id);
			try {
				PreparedStatement stmt = db.prepareStatement("DELETE FROM HEAP WHERE `Id`=?");
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void flushMap() throws SQLException {
		for (String key : map.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT OR REPLACE INTO MAP(Id, Data) VALUES (?, ?)");
			stmt.setString(1, (String) key);

			Object value = map.get(key);
			if (value instanceof String) {
				stmt.setString(2, (String) value);
			} else if (value instanceof Integer) {
				stmt.setInt(2, (int) value);
			} else if (value instanceof Long) {
				stmt.setLong(2, (long) value);
			} else if (value instanceof Boolean) {
				stmt.setBoolean(2, (boolean) value);
			} else {
				stmt.setBytes(2, SerialUtil.serialize(value));
			}

			stmt.executeUpdate();
		}
	}

	public void flushHeap() throws SQLException {
		for (Integer key : heap.keySet()) {
			PreparedStatement stmt = db.prepareStatement("INSERT OR REPLACE INTO HEAP(Id, Data) VALUES (?, ?)");
			stmt.setInt(1, (Integer) key);

			Object value = heap.get(key);
			if (value instanceof String) {
				stmt.setString(2, (String) value);
			} else if (value instanceof Integer) {
				stmt.setInt(2, (int) value);
			} else if (value instanceof Long) {
				stmt.setLong(2, (long) value);
			} else if (value instanceof Boolean) {
				stmt.setBoolean(2, (boolean) value);
			} else {
				stmt.setBytes(2, SerialUtil.serialize(value));
			}

			stmt.executeUpdate();
		}
	}

	/**
	 * Store an object in the String map
	 * 
	 * @param key
	 * @param object
	 *            The object to be stored
	 */
	public void store(String key, Object object) {
		synchronized (map) {
			map.put(key, object);
		}
	}

	/**
	 * Store an object in the Integer heap
	 * 
	 * @param object
	 *            The object to be stored
	 * @return The id in the database where the object was stored
	 */
	public int store(Object object) {
		int id = reserveRow();
		heap.put(id, object);
		return id;
	}

	public void close() {
		try {
			flushMap();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			flushHeap();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			super.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected int reserveRow() {
		try {
			PreparedStatement stmt1 = db.prepareStatement("INSERT INTO HEAP(Data) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt1.setBytes(1, new byte[0]);
			stmt1.executeUpdate();

			try (ResultSet generatedKeys = stmt1.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException();
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
