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
import java.util.Date;

public abstract class Attribute implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String current;
	private Date timestamp = new Date(0);

	public String get() {
		return current;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void set(String s) {
		current = s;
		timestamp = new Date();
	}

	@Override
	public String toString() {
		return current;
	}

}
