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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDatabase implements AutoCloseable {

	protected Connection db;

	// for sqlite databases
	protected File sqlite;

	// for mysql databases
	protected String url;
	protected String user;
	protected String pass;

	public void initialize() throws ClassNotFoundException, IOException, SQLException {
		if (sqlite != null)
			initialize(sqlite);
		else
			initialize(url, user, pass);
	}

	public void initialize(File sqliteDB) throws ClassNotFoundException, IOException, SQLException {
		if (db != null)
			throw new IllegalStateException("The database is already initialized");

		// create database if needed
		sqlite.getParentFile().mkdirs();
		sqlite.createNewFile();

		// load database driver
		Class.forName("org.sqlite.JDBC");

		// initialize connection
		db = DriverManager.getConnection("jdbc:sqlite:" + sqlite.getAbsolutePath());
	}

	public void initialize(String url, String user, String pass)
			throws ClassNotFoundException, IOException, SQLException {
		if (db != null)
			throw new IllegalStateException("The database is already initialized");

		// load database driver
		Class.forName("com.mysql.jdbc.Driver");

		// initialize connection
		db = DriverManager.getConnection(url, user, pass);
	}

	public void close() throws SQLException {
		try {
			db.close();
		} finally {
			db = null;
			sqlite = null;
		}
	}

	/**
	 * Execute a SQL command and ignore output
	 * 
	 * @param command
	 * @throws SQLException
	 */
	public void execute(String command) throws SQLException {
		db.createStatement().executeUpdate(command);
	}

	/**
	 * Get the database file
	 * 
	 * @return The SQLite database file or null if this is a MySQL database.
	 */
	public File getFile() {
		return sqlite;
	}

}
