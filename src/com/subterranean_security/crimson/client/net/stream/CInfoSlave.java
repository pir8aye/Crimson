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
package com.subterranean_security.crimson.client.net.stream;

import com.subterranean_security.crimson.core.net.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.proto.Delta.EV_ProfileDelta;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.store.LcvidStore;

public class CInfoSlave extends InfoSlave {

	public CInfoSlave(Param p) {
		super(p, p.getVID());
	}

	@Override
	public void send() {
		EV_ProfileDelta pd = gather();
		if (pd.getGroupCount() != 0) {
			write(Message.newBuilder().setSid(LcvidStore.cvid).setRid(param().getVID()).setEvProfileDelta(pd));
		}
	}

}
