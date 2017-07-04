package com.subterranean_security.crimson.client.exe;

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.Connector.ConnectionState;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.net.auth.KeyAuthGroup;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.exe.DeltaExe;

public class AuthExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(AuthExe.class);

	public AuthExe(Connector connector) {
		super(connector);
	}

	public void rq_group_challenge(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		KeyAuthGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		String result = CryptoUtil.hashSign(m.getRqGroupChallenge().getMagic(), groupKey);
		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder().setResult(result).build();
		connector.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
	}

	public void challengeResult_1w(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getMiChallengeResult().getResult()) {
			log.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		KeyAuthGroup group = Client.getGroup();
		final byte[] groupKey = group.getGroupKey();
		try {
			group.destroy();
		} catch (DestroyFailedException e1) {
		}

		// Send authentication challenge
		final int id = IDGen.msg();

		final String magic = RandomUtil.randString(64);
		RQ_GroupChallenge rq = RQ_GroupChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.write(Message.newBuilder().setId(id).setRqGroupChallenge(rq).build());

		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.getResponse(id).get(7000);

					if (rs != null) {
						if (!CryptoUtil.verifyGroupChallenge(magic, groupKey, rs.getRsGroupChallenge().getResult())) {
							log.info("Server challenge failed");
							flag = false;
						}

					} else {
						log.debug("No response to challenge");
						flag = false;
					}
				} catch (InterruptedException e) {
					log.debug("No response to challenge");
					flag = false;
				} catch (MessageTimeout e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				MI_GroupChallengeResult.Builder oneway = MI_GroupChallengeResult.newBuilder().setResult(flag);

				if (flag) {
					connector.setState(ConnectionState.AUTHENTICATED);

					oneway.setPd(Platform.fig());
				} else {
					// TODO handle more
					connector.setState(ConnectionState.CONNECTED);
				}
				connector.write(Message.newBuilder().setId(id).setMiChallengeResult(oneway.build()).build());

			}
		}).start();

	}

}
