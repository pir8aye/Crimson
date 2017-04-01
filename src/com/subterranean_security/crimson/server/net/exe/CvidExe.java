package com.subterranean_security.crimson.server.net.exe;

import com.subterranean_security.crimson.core.LCVID;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.proto.CVID.RS_Cvid;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.util.IDGen;

public final class CvidExe {
	private CvidExe() {
	}

	/**
	 * Respond to a request for a cvid. If the server is not familiar with one
	 * of the supplied long-cvids, respond with a new lcvid-cvid pair.
	 * 
	 * @param c
	 * @param m
	 */
	public static void rq_cvid(Connector c, Message m) {
		for (String lcvid : m.getRqCvid().getLcvidList()) {
			if (LCVID.contains(lcvid)) {
				int cvid = LCVID.get(lcvid);
				ConnectionStore.changeCvid(c.getCvid(), cvid);

				c.setCvid(cvid);
				c.write(Message.newBuilder().setId(m.getId()).setRsCvid(RS_Cvid.newBuilder().setCvid(cvid)).build());
				return;
			}
		}

		// create a new cvid and lcvid
		int cvid = IDGen.cvid();
		String lcvid = IDGen.lcvid();
		LCVID.addLcvid(lcvid, cvid);

		ConnectionStore.changeCvid(c.getCvid(), cvid);

		c.setCvid(cvid);
		c.write(Message.newBuilder().setId(m.getId()).setRsCvid(RS_Cvid.newBuilder().setCvid(cvid).setLcvid(lcvid))
				.build());
	}
}
