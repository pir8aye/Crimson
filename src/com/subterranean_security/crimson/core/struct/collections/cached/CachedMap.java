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
package com.subterranean_security.crimson.core.struct.collections.cached;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.subterranean_security.crimson.core.storage.BasicStorageFacility;

/**
 * 
 *
 * @param <K>
 * @param <V>
 */
public class CachedMap<K, V> extends CachedCollection implements Map<K, V> {

	private static final long serialVersionUID = 1L;

	private HashMap<K, Integer> index;

	public CachedMap(BasicStorageFacility d) {
		this();
		setDatabase(d);
	}

	public CachedMap() {
		index = new HashMap<K, Integer>();
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

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (index.get(key) == null)
			return null;
		return (V) database.getObject(index.get(key));
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
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	// TODO return a CachedList
	public Collection<V> values() {
		Collection<V> values = new ArrayList<>();
		for (K key : keySet()) {
			values.add(get(key));
		}
		return values;
	}

}
