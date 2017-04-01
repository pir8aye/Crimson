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
package com.subterranean_security.crimson.client.stream;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;

public class CInfoSlave extends InfoSlave {

	public CInfoSlave(Param p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void send() {
		EV_ProfileDelta pd = gather();
		if (pd.getGroupCount() != 0) {
			ConnectionStore
					.route(Message.newBuilder().setSid(Common.cvid).setRid(param.getVID()).setEvProfileDelta(pd));
		}
	}

}
