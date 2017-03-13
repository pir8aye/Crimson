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
package com.subterranean_security.crimson.core.misc;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.subterranean_security.crimson.core.storage.StorageFacility;

public class MemMap<K, V> implements Serializable, Map<K, V> {

	private static final long serialVersionUID = 1L;

	private transient StorageFacility database;

	private HashMap<K, Integer> index;

	public MemMap(StorageFacility d) {
		this();
		setDatabase(d);
	}

	public MemMap() {
		index = new HashMap<K, Integer>();
	}

	public void setDatabase(StorageFacility d) {
		this.database = d;
	}

	@Override
	public V put(K key, V value) {
		index.put(key, database.store(value));
		return null;
	}

	@Override
	public V remove(Object key) {
		V value = get(key);
		index.remove(key);
		return value;
	}

	@Override
	public V get(Object key) {
		try {
			return (V) database.getObject(index.get(key));
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean containsKey(Object arg0) {
		return index.containsKey(arg0);
	}

	@Override
	public boolean isEmpty() {
		return index.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return index.keySet();
	}

	@Override
	public int size() {
		return index.size();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

}
