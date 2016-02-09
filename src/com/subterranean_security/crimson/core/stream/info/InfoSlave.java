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
package com.subterranean_security.crimson.core.stream.info;

import com.subterranean_security.crimson.core.proto.net.MSG;
import com.subterranean_security.crimson.core.proto.net.stream.Stream.InfoData;
import com.subterranean_security.crimson.core.stream.Stream;
import com.subterranean_security.crimson.core.util.CUtil;

public class InfoSlave extends Stream {

	@Override
	public void received(MSG m) {

	}

	@Override
	public void send() {
		InfoData.Builder id = InfoData.newBuilder();
		for (String s : param.getInfoParam().getPropertyList()) {
			switch (s) {
			case "CPU_USAGE": {
				id.addData(CUtil.Misc.randString(5));
				break;
			}
			}
		}

	}

	@Override
	public void start() {
		timer.schedule(sendTask, 0, 1000);

	}

	@Override
	public void stop() {
		timer.cancel();

	}

}
