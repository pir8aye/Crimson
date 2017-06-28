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
package com.subterranean_security.crimson.core.attribute;

import java.io.Serializable;

/**
 * An {@code Attribute} is the primary constituent of a {@code Profile}. It
 * contains a datum such as CPU speed, Java version, Username, etc...
 * 
 * @author cilki
 * @since 4.0.0
 */
public abstract class Attribute<E> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected E current;
	protected long timestamp;

	/**
	 * @return The current value of this {@code Attribute}
	 */
	public E get() {
		return current;
	}

	/**
	 * @return The timestamp of the current value
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set the current value of this {@code Attribute}.
	 * {@code System.currentTimeMillis()} will be used as the timestamp.
	 * 
	 * @param value
	 */
	public void set(E value) {
		set(value, System.currentTimeMillis());
	}

	/**
	 * Set the current value of this {@code Attribute}.
	 * 
	 * @param value
	 * @param time
	 */
	public void set(E value, long time) {
		current = value;
		timestamp = time;
	}

	public int toInt() {
		return (int) current;
	}

	@Override
	public String toString() {
		return (String) current;
	}

}
