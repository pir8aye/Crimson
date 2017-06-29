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

import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.platform.collect.singular.JVM;
import com.subterranean_security.crimson.core.platform.collect.singular.OS.OSFAMILY;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.universal.Universal;

public final class Platform {

	private Platform() {
	}

	public static final ARCH javaArch = JVM.getARCH();
	public static final OSFAMILY osFamily = OSFAMILY.get();

	public enum ARCH {
		X86, X64, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public static EV_ProfileDelta fig() {
		PDFactory pd = new PDFactory(LcvidStore.cvid);

		for (SingularKey key : SingularKey.keys) {

			if (!key.isCompatible(osFamily, Universal.instance)) {
				continue;
			}
			if (key == AK_NET.EXTERNAL_IPV4 && !ConfigStore.getConfig().getAllowMiscConnections()) {
				continue;
			}
			Object value = key.query();
			if (value != null) {
				pd.add(key, value);
			}

		}

		// TODO PLURAL ATTRIBUTES

		return pd.buildPd();
	}

}
