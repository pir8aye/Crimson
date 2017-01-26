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

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;

public interface StorageFacility extends AutoCloseable {

	public void initialize() throws IOException, ClassNotFoundException, SQLException;

	@Override
	public void close() throws IOException;

	public void store(String key, Object object);

	public int store(Object object);

	public void delete(String key);

	public void delete(int id);

	/*
	 * String values
	 */

	public String getString(String key) throws SQLException, NoSuchElementException;

	public String getString(int id) throws IOException;

	/*
	 * Integer values
	 */

	public int getInteger(String key) throws SQLException, NoSuchElementException;

	public int getInteger(int id) throws IOException;

	/*
	 * Long values
	 */

	public long getLong(String key) throws SQLException, NoSuchElementException;

	public long getLong(int id) throws IOException;

	/*
	 * Boolean values
	 */

	public boolean getBoolean(String key) throws SQLException, NoSuchElementException;

	public boolean getBoolean(int id) throws IOException;

	/*
	 * Object values
	 */

	public Object getObject(String key) throws SQLException, NoSuchElementException;

	public Object getObject(int id) throws SQLException, NoSuchElementException;

	/*
	 * Server exclusive methods
	 */

	public boolean userExists(String user);

	public String getSalt(String user);

	public boolean validLogin(String user, String password);

	public boolean changePassword(String user, String password);

	public boolean addLocalUser(String user, String password, ViewerPermissions permissions);

}
