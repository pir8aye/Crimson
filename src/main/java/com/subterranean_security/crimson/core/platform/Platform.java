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

import java.awt.GraphicsEnvironment;

import com.subterranean_security.crimson.client.store.ConfigStore;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.platform.info.CPU;
import com.subterranean_security.crimson.core.platform.info.DISP;
import com.subterranean_security.crimson.core.platform.info.JAVA;
import com.subterranean_security.crimson.core.platform.info.NIC;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.universal.Universal;

public final class Platform {

	private Platform() {
	}

	public static final ARCH javaArch = JAVA.getARCH();
	public static final OSFAMILY osFamily = OSFAMILY.get();

	public enum ARCH {
		X86, X64, SPARC, UNSUPPORTED;
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * First Info Gather
	 */
	public static EV_ProfileDelta fig() {

		EV_ProfileDelta.Builder info = EV_ProfileDelta.newBuilder();

		try {
			info.setCvid(LcvidStore.cvid);
		} catch (Exception e1) {
			// TODO handle
			info.setCvid(0);
		}

		info.setFig(true);

		AttributeGroupContainer.Builder general = AttributeGroupContainer.newBuilder();
		for (SingularKey sa : SingularKey.keys) {

			if (!sa.isCompatible(osFamily, Universal.instance)) {
				continue;
			}
			if (sa == AK_NET.EXTERNAL_IPV4 && !ConfigStore.getConfig().getAllowMiscConnections()) {
				continue;
			}
			String value = queryAttribute(sa);
			if (value != null) {
				general.putAttribute(sa.ordinal(), value);
			}

		}
		info.addGroup(general.build());
		info.addAllGroup(CPU.getAttributes());
		info.addAllGroup(NIC.getAttributes());

		if (!GraphicsEnvironment.isHeadless()) {
			info.addAllGroup(DISP.getAttributes());
		}

		return info.build();
	}

}
