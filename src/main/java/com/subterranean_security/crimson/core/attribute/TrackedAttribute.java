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
package com.subterranean_security.crimson.core.attribute;

import java.util.LinkedList;
import java.util.List;

/**
 * A {@code TrackedAttribute} stores the timestamped history of its value.
 * 
 * @author cilki
 * @since 4.0.0
 */
public class TrackedAttribute<E> extends Attribute<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * The {@code Attribute} history
	 */
	private List<UntrackedAttribute<E>> values;

	public TrackedAttribute() {
		values = new LinkedList<>();
	}

	@Override
	public void set(E value) {
		set(value, System.currentTimeMillis());
	}

	@Override
	public void set(E value, long time) {
		values.add(new UntrackedAttribute<E>(current, timestamp));
		super.set(value, time);
	}

	/**
	 * @return The number of entries in the attribute history
	 */
	public int size() {
		return values.size();
	}

	/**
	 * @param index
	 * @return
	 */
	public E getValue(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException();

		return values.get(index).get();
	}

	/**
	 * 
	 * 
	 * @param index
	 *            The index into the history where 0 is the most recent,
	 *            non-current entry
	 * @return The specific timestamp in the history
	 */
	public long getTime(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException();

		return values.get(index).getTimestamp();
	}

}
