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
package com.subterranean_security.crimson.core.util;

import java.util.ArrayList;

public class LimitedQueue<K> extends ArrayList<K> {

	private static final long serialVersionUID = 1L;

	private int size;

	public LimitedQueue(int size) {
		this.size = size;
	}

	public boolean add(K k) {
		boolean r = super.add(k);
		if (size() > size) {
			removeRange(0, size() - size - 1);
		}
		return r;
	}

}