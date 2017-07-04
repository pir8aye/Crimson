package com.subterranean_security.crimson.client.exe;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;

/**
 * @author cilki
 * @since 5.0.0
 */
public class TorrentExe extends Exelet implements ExeI {

	public TorrentExe(Connector connector) {
		super(connector);
	}

	public void rq_add_torrent(Message m) {

	}

}
