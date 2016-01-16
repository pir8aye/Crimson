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

import java.io.Serializable;
import java.util.HashMap;

public class MemMap<K, V> implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private HashMap<K, Integer>	map					= new HashMap<K, Integer>();
	private transient Database	database;

	public MemMap(Database d) {
		setDatabase(d);
	}

	public MemMap() {

	}

	public void setDatabase(Database d) {
		this.database = d;
	}

	public void put(K key, V value) {
		map.put(key, database.store(value));
	}

	@SuppressWarnings("unchecked")
	public V get(K key) throws Exception {
		return (V) database.get(map.get(key));
	}

}
