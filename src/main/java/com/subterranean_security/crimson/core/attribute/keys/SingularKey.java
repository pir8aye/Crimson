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
package com.subterranean_security.crimson.core.attribute.keys;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_CLIENT;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_JVM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_KEYLOGGER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LIN;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_META;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_MOBO;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OSX;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_RAM;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_USER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_WIN;

/**
 * @author cilki
 * @since 5.0.0
 */
public interface SingularKey extends AttributeKey {

	@SuppressWarnings("unchecked")
	public static final Class<SingularKey>[] keyTypes = new Class[] { AK_CLIENT.class, AK_JVM.class, AK_KEYLOGGER.class,
			AK_LIN.class, AK_LOC.class, AK_META.class, AK_MOBO.class, AK_NET.class, AK_OS.class, AK_OSX.class,
			AK_RAM.class, AK_SERVER.class, AK_USER.class, AK_VIEWER.class, AK_WIN.class };

	public static List<SingularKey> keys = getAllKeys();

	public static List<SingularKey> getAllKeys() {
		List<SingularKey> l = new LinkedList<>();
		for (Class<SingularKey> k : keyTypes) {
			l.addAll(Arrays.asList(k.getEnumConstants()));
		}
		return l;
	}

	public Object query();

	@Override
	default int getGroupID() {
		// singular keys are always in group 0
		return 0;
	}
}
