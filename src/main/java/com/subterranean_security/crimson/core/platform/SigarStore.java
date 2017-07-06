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
package com.subterranean_security.crimson.core.platform;

import java.io.File;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.platform.collect.singular.CRIMSON;
import com.subterranean_security.crimson.core.platform.collect.singular.NET;
import com.subterranean_security.crimson.core.platform.collect.singular.OS;
import com.subterranean_security.crimson.core.platform.collect.singular.RAM;

public final class SigarStore {
	private static final Logger log = LoggerFactory.getLogger(SigarStore.class);

	private SigarStore() {
	}

	private static Sigar sigar;

	public static void loadSigar() {
		System.setProperty("java.library.path",
				new File(Environment.base.getAbsolutePath() + "/lib/jni/" + Platform.osFamily.toString())
						.getAbsolutePath());

		try {
			sigar = new Sigar();
		} catch (Exception e) {
			log.error("Sigar failed to autoload!");
			return;
		}

		initialize();
	}

	public static void initialize() {
		// CPU.initialize();
		CRIMSON.initialize();
		NET.initialize();
		// NIC.initialize();
		OS.initialize();
		RAM.initialize();
	}

	public static Sigar getSigar() {
		return sigar;
	}

}