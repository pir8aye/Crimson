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

import java.util.ArrayList;
import java.util.Date;

public class TrackedAttribute extends Attribute {

	private static final long serialVersionUID = 1L;

	private ArrayList<String> values = new ArrayList<String>();
	private ArrayList<Date> timestamps = new ArrayList<Date>();

	@Override
	public void set(String s) {
		set(s, new Date());
	}

	public void set(String s, Date d) {
		timestamps.add(d);
		values.add(s);
		super.set(s);
	}

	public int size() {
		return values.size();
	}

	public String getValue(int i) {
		try {
			return values.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Date getTime(int i) {
		try {
			return timestamps.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

}
