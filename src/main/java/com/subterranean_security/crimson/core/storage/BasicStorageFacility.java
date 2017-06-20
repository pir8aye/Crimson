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

import com.subterranean_security.crimson.core.struct.collections.cached.CachedCollection;

public interface BasicStorageFacility extends AutoCloseable {

	public void initialize() throws IOException, ClassNotFoundException, SQLException;

	public void close();

	public void store(String key, Object object);

	public int store(Object object);

	public void delete(String key);

	public void delete(int id);

	/* String */
	public String getString(String key);

	public String getString(int id);

	/* Integer */
	public int getInteger(String key);

	public int getInteger(int id);

	/* Long */
	public long getLong(String key);

	public long getLong(int id);

	/* Boolean */
	public boolean getBoolean(String key);

	public boolean getBoolean(int id);

	/* Object */
	public Object getObject(String key);

	public Object getObject(int id);

	/* CachedCollection */
	public CachedCollection getCachedCollection(String key);
}
