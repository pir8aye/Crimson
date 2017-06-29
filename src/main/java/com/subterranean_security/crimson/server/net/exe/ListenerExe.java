package com.subterranean_security.crimson.server.net.exe;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.store.ListenerStore;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;

/**
 * @author cilki
 * @since 5.0.0
 */
public class ListenerExe extends Exelet implements ExeI {

	public ListenerExe(Connector connector) {
		super(connector);
	}

	public void rq_add_listener(Message m) {
		// check permissions
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.network.create_listener)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsOutcome(Outcome.newBuilder().setResult(false).setComment("Insufficient permissions"))
					.build());
			return;
		}

		connector.write(
				Message.newBuilder().setId(m.getId()).setRsOutcome(Outcome.newBuilder().setResult(true)).build());
		ListenerStore.add(m.getRqAddListener().getConfig());
		// TODO UPDATE
		// Message update = Message.newBuilder().setEvServerProfileDelta(
		// EV_ServerProfileDelta.newBuilder().addListener(m.getRqAddListener().getConfig())).build();
		// NetworkStore.broadcastTo(Universal.Instance.VIEWER, update);

	}

	public void rq_remove_listener(Message m) {

	}
}
