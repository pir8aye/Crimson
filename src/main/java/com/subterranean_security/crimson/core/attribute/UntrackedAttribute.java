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

/**
 * @author cilki
 * @since 4.0.0
 */
public class UntrackedAttribute<E> extends Attribute<E> {

	private static final long serialVersionUID = 1L;

	public UntrackedAttribute() {

	}

	public UntrackedAttribute(E value) {
		set(value);
	}

	public UntrackedAttribute(E value, long time) {
		set(value, time);
	}

}
