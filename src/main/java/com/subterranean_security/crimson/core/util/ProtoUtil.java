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
package com.subterranean_security.crimson.core.util;

import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.AttributeGroupContainer;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ServerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ViewerProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;

public final class ProtoUtil {
	public static InfoParam.Builder getInfoParam(AttributeKey... keys) {
		InfoParam.Builder param = InfoParam.newBuilder();
		for (AttributeKey key : keys) {
			param.addKey(key.getFullID());
		}
		return param;
	}

	public static AttributeGroupContainer.Builder getNewGeneralGroup() {
		return AttributeGroupContainer.newBuilder().setGroupType(AttributeKey.Type.GENERAL.ordinal());
	}

	public static AttributeGroupContainer getGeneralGroup(EV_ProfileDelta pd) {
		for (AttributeGroupContainer container : pd.getGroupList()) {
			if (container.getGroupType() == AttributeKey.Type.GENERAL.ordinal()) {
				return container;
			}
		}
		return null;
	}

	public static AttributeGroupContainer getGeneralGroup(EV_ServerProfileDelta pd) {
		return getGeneralGroup(pd.getPd());
	}

	public static AttributeGroupContainer getGeneralGroup(EV_ViewerProfileDelta pd) {
		return getGeneralGroup(pd.getPd());
	}

}
