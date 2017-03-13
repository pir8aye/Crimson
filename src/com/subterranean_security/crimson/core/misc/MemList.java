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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.subterranean_security.crimson.core.storage.StorageFacility;

/**
 * A MemList indexes values which are stored in an underlying database
 *
 * @param <T>
 */
public class MemList<T> implements Serializable, List<T> {

	private static final long serialVersionUID = 1L;

	private transient StorageFacility database;

	private List<Integer> index;

	public MemList() {
		index = new ArrayList<Integer>();
	}

	public MemList(StorageFacility d) {
		this();
		setDatabase(d);
	}

	/**
	 * Use the specified storage facility for list elements
	 * 
	 * @param d
	 */
	public void setDatabase(StorageFacility d) {
		this.database = d;
	}

	@Override
	public boolean add(T t) {
		return index.add(database.store(t));
	}

	@Override
	public void add(int pos, T t) {
		index.add(pos, database.store(t));
	}

	@Override
	public T get(int pos) {
		try {
			return (T) database.getObject(index.get(pos));
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public T remove(int pos) {
		T t = get(pos);
		database.delete(index.remove(pos));
		return t;
	}

	@Override
	public int size() {
		return index.size();
	}

	@Override
	public void clear() {
		for (int i = 0; i < index.size(); i++) {
			remove(i);
		}
	}

	@Override
	public boolean isEmpty() {
		return index.size() == 0;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int arg0, T arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}

}
