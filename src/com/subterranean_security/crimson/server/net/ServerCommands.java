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
package com.subterranean_security.crimson.server.net;

import com.subterranean_security.crimson.core.proto.CVID.MI_AssignCVID;
import com.subterranean_security.crimson.core.proto.MSG.Message;

public final class ServerCommands {

	private ServerCommands() {
	}

	public static void setCvid(Receptor r, int cvid) {
		r.setCvid(cvid);
		r.handle.write(Message.newBuilder().setMiAssignCvid(MI_AssignCVID.newBuilder().setId(cvid)).build());
	}

}
