package com.subterranean_security.crimson.server.exe;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.info.InfoSlave;
import com.subterranean_security.crimson.core.stream.subscriber.SubscriberSlave;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.Param;

/**
 * @author cilki
 * @since 5.0.0
 */
public class StreamExe extends Exelet implements ExeI {

	public StreamExe(Connector connector) {
		super(connector);
	}

	public void mi_stream_start(Message m) {
		Param p = m.getMiStreamStart().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(new InfoSlave(p));
		}
		if (p.hasSubscriberParam()) {
			StreamStore.addStream(new SubscriberSlave(p));
		}
	}

	public void mi_stream_stop(Message m) {
		StreamStore.removeStreamBySID(m.getMiStreamStop().getStreamID());
	}
}
