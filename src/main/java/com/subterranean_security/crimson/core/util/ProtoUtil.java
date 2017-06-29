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
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.InfoParam;

public final class ProtoUtil {
	public static InfoParam.Builder getInfoParam(AttributeKey... keys) {
		InfoParam.Builder param = InfoParam.newBuilder();
		for (AttributeKey key : keys) {
			param.addKey(key.getWireID());
		}
		return param;
	}

	/**
	 * This factory makes creating an EV_ProfileDelta easier.
	 * 
	 * @author cilki
	 * @since 5.0.0
	 */
	public static class PDFactory {
		private EV_ProfileDelta.Builder pd;

		public PDFactory() {
			pd = EV_ProfileDelta.newBuilder();
		}

		public PDFactory(int cvid) {
			this();
			setCvid(cvid);
		}

		public PDFactory setCvid(int cvid) {
			pd.setCvid(cvid);
			return this;
		}

		public PDFactory add(AttributeKey key, Object value) {
			if (value instanceof Integer) {
				pd.putIntAttr(key.getWireID(), (int) value);
			} else if (value instanceof String) {
				pd.putStrAttr(key.getWireID(), (String) value);
			} else if (value instanceof Long) {
				pd.putLongAttr(key.getWireID(), (Long) value);
			} else if (value instanceof Boolean) {
				pd.putBooleanAttr(key.getWireID(), (Boolean) value);
			} else {
				throw new IllegalArgumentException("Invalid value: " + value.getClass().getName());
			}

			return this;
		}

		public Message buildMsg() {
			return Message.newBuilder().setEvProfileDelta(pd).build();
		}

		public EV_ProfileDelta buildPd() {
			return pd.build();
		}
	}

}
