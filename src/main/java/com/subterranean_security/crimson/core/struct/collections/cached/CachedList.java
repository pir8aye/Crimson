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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.subterranean_security.crimson.core.storage.BasicStorageFacility;

/**
 * A CachedList indexes values which are stored in an underlying database
 *
 * @param <T>
 *            The type of values stored in the database
 */
public class CachedList<T> extends CachedCollection implements List<T> {

	private static final long serialVersionUID = 1L;

	private List<Integer> index;

	public CachedList() {
		index = new ArrayList<Integer>();
	}

	public CachedList(BasicStorageFacility d) {
		this();
		setDatabase(d);
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
		return new Iterator<T>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return current < index.size();
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return get(current++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int arg0) {
		return new ListIterator<T>() {

			private int current = arg0;

			@Override
			public boolean hasNext() {
				return current < index.size();
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();

				return get(current++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void add(T arg0) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean hasPrevious() {
				return current > 0;
			}

			@Override
			public int nextIndex() {
				throw new UnsupportedOperationException();
			}

			@Override
			public T previous() {
				if (!hasPrevious())
					throw new NoSuchElementException();

				return get(current--);
			}

			@Override
			public int previousIndex() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(T arg0) {
				throw new UnsupportedOperationException();
			}
		};
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
	public T set(int i, T value) {
		if (i < 0 || i >= index.size())
			throw new IndexOutOfBoundsException();

		// remember previous element
		T t = get(i);

		// delete
		database.delete(index.get(i));

		// replace
		index.add(i, database.store(value));

		return t;
	}

	@Override
	public List<T> subList(int i1, int i2) {
		CachedList<T> sub = new CachedList<>(database);
		for (int i = i1; i < i2; i++) {
			sub.index.add(index.get(i));
		}

		return sub;
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
