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
package com.subterranean_security.crimson.core.attribute.keys.plural;

import com.subterranean_security.crimson.core.attribute.Attribute;
import com.subterranean_security.crimson.core.attribute.UntrackedAttribute;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.PluralKey;
import com.subterranean_security.crimson.core.platform.info.OS.OSFAMILY;
import com.subterranean_security.crimson.universal.Universal.Instance;

public enum AK_NIC implements PluralKey {
	NIC_NAME, NIC_DESC, NIC_IP, NIC_MAC, NIC_MASK, NIC_RX_SPEED, NIC_TX_SPEED, NIC_RX_BYTES, NIC_TX_BYTES, NIC_RX_PACKETS, NIC_TX_PACKETS;

	@Override
	public int getGroupType() {
		return AttributeKey.Type.NIC.ordinal();
	}

	@Override
	public int getOrdinal() {
		return this.ordinal();
	}

	@Override
	public Attribute getNewAttribute() {
		return new UntrackedAttribute();
	}

	@Override
	public boolean isCompatible(OSFAMILY os, Instance instance) {
		return true;
	}

	@Override
	public boolean isHeaderable() {
		return true;
	}
}
