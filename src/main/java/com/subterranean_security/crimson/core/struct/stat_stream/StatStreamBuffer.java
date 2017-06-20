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
package com.subterranean_security.crimson.core.struct.stat_stream;

/**
 * The underlying storage container of a StatStream which is essentially a basic
 * RingBuffer.
 *
 * @param <E>
 */
public class StatStreamBuffer<E> {

	private final E[] ring;

	private int write = 0;

	private int size = 0;

	@SuppressWarnings("unchecked")
	public StatStreamBuffer(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException("Invalid capacity: " + capacity);

		ring = (E[]) new Object[capacity];
	}

	/**
	 * Add an element to the buffer
	 * 
	 * @param e
	 */
	public void add(E e) {
		// add element, possibly overwriting the oldest element
		ring[write] = e;

		// increase write pointer
		write = (write + 1) % ring.length;

		// increase size if the ring is not yet full
		if (size < ring.length)
			size++;

	}

	/**
	 * Get the given element. The element at index 0 is the most recently added.
	 * 
	 * @param index
	 * @return
	 */
	public E get(int index) {
		if (index >= size || index < 0)
			throw new IllegalArgumentException("Invalid index: " + index);
		int pos = write - 1 - index;
		if (pos < 0) {
			pos = size + pos;
		}

		return ring[pos];
	}

	/**
	 * @return The number of elements currently stored
	 */
	public int size() {
		return size;
	}

	/**
	 * @return The maximum number of elements which can be stored
	 */
	public int capacity() {
		return ring.length;
	}

}