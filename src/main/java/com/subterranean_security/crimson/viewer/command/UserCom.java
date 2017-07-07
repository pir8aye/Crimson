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
package com.subterranean_security.crimson.viewer.command;

import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.net.exception.MessageFlowException;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RQ_AddUser;
import com.subterranean_security.crimson.proto.core.net.sequences.Users.RQ_EditUser;
import com.subterranean_security.crimson.sv.permissions.ViewerPermissions;

public final class UserCom {
	private UserCom() {
	}

	public static Outcome addUser(String user, String pass, ViewerPermissions vp) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		RQ_AddUser.Builder add = RQ_AddUser.newBuilder().setUser(user).setPassword(pass)
				.addAllPermissions(vp.getFlags());

		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setRqAddUser(add),
					TimeoutConstants.DEFAULT);
			if (m.getRsOutcome() == null)
				throw new MessageFlowException(RQ_AddUser.class, m);

			outcome = outcome.mergeFrom(m.getRsOutcome());
		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			outcome.setResult(false).setComment("Request timeout");
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}

	public static Outcome editUser(String user, String oldpass, String newpass, ViewerPermissions vp) {
		long t1 = System.currentTimeMillis();
		Outcome.Builder outcome = Outcome.newBuilder().setResult(false);

		RQ_AddUser.Builder rqau = RQ_AddUser.newBuilder().setUser(user).addAllPermissions(vp.getFlags());

		RQ_EditUser.Builder rqeu = RQ_EditUser.newBuilder();
		if (oldpass != null && newpass != null) {
			rqau.setPassword(newpass);
			rqeu.setOldPassword(oldpass);
		}

		try {
			Message m = NetworkStore.route(Message.newBuilder().setId(IDGen.msg()).setRqEditUser(rqeu.setUser(rqau)),
					TimeoutConstants.DEFAULT);
			if (m.getRsOutcome() == null)
				throw new MessageFlowException(RQ_EditUser.class, m);

			outcome = outcome.mergeFrom(m.getRsOutcome());

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (MessageTimeout e) {
			outcome.setResult(false).setComment("Request timeout");
		}

		return outcome.setTime(System.currentTimeMillis() - t1).build();
	}
}
