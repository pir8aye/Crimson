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

import com.subterranean_security.crimson.core.storage.StorageFacility;

public class MemList<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Integer> index = new ArrayList<Integer>();
	private transient StorageFacility database;

	public MemList() {

	}

	public MemList(StorageFacility d) {
		setDatabase(d);

	}

	public void setDatabase(StorageFacility d) {
		this.database = d;
	}

	public void add(T ob) {
		index.add(database.store(ob));

	}

	public T get(Integer id) {
		try {
			return (T) database.getObject(index.get(id));
		} catch (Exception e) {
			return null;
		}

	}

	public T remove(Integer i) {
		T t = get(i);
		database.delete(index.get(i));
		return t;
	}

	public int size() {
		return index.size();
	}

}
