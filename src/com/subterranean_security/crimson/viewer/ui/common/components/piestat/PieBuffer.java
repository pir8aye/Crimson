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
package com.subterranean_security.crimson.viewer.ui.common.components.piestat;

import java.awt.Point;
import java.util.ArrayList;

/**
 * A modified RingBuffer for backing history in a pie stat chart
 */
public final class PieBuffer {
	private Point[] elements = null;

	private int offset = 0;

	public PieBuffer(int size) {
		elements = new Point[size];
	}

	public synchronized void add(Point e) {

		// write the next element, potentially overwriting an old value
		elements[offset] = e;
		offset = (offset + 1) % elements.length;
	}

	public synchronized Point[] getElements() {
		ArrayList<Point> val = new ArrayList<Point>();

		for (int i = 0; i < elements.length; i++) {
			if (elements[(offset + i) % elements.length] != null) {
				val.add(elements[(offset + i) % elements.length]);
			}
		}

		Point[] values = new Point[val.size()];

		for (int i = 0; i < values.length; i++) {
			values[i] = val.get(i);
		}

		return values;
	}

	public int getSize() {
		return elements.length;
	}

}
