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
package com.subterranean_security.crimson.core.struct.collections.cached;

import java.io.Serializable;

import com.subterranean_security.crimson.core.storage.BasicStorageFacility;

/**
 * A cached collection mimics its standard Java counterpart, but may not be
 * entirely loaded in memory. A database is used as underlying storage. This
 * database must be associated with the collection (using setDatabase()) before
 * the collection can be used.
 *
 */
public abstract class CachedCollection implements Serializable {

	private static final long serialVersionUID = 1L;

	protected transient BasicStorageFacility database;

	/**
	 * Set the underlying storage
	 * 
	 * @param database
	 *            The underlying storage for this Collection
	 */
	public void setDatabase(BasicStorageFacility database) {
		this.database = database;
	}

	/**
	 * Get the underlying storage
	 * 
	 * @return The underlying storage for this Collection
	 */
	public BasicStorageFacility getDatabase() {
		return database;
	}

}
