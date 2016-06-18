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
package com.subterranean_security.crimson.client.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.client.ClientStore;
import com.subterranean_security.crimson.client.stream.CInfoSlave;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.core.fm.LocalFilesystem;
import com.subterranean_security.crimson.core.net.BasicExecutor;
import com.subterranean_security.crimson.core.net.ConnectionState;
import com.subterranean_security.crimson.core.proto.ClientAuth.MI_GroupChallengeResult;
import com.subterranean_security.crimson.core.proto.ClientAuth.RQ_GroupChallenge;
import com.subterranean_security.crimson.core.proto.ClientAuth.RS_GroupChallenge;
import com.subterranean_security.crimson.core.proto.FileManager.RQ_FileListing;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileHandle;
import com.subterranean_security.crimson.core.proto.FileManager.RS_FileListing;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.proto.Misc.Group;
import com.subterranean_security.crimson.core.proto.Stream.Param;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.Crypto;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.Native;

import io.netty.util.ReferenceCountUtil;

public class ClientExecutor extends BasicExecutor {
	private static final Logger log = LoggerFactory.getLogger(ClientExecutor.class);

	private ClientConnector connector;

	public ClientExecutor(ClientConnector vc) {
		connector = vc;

		ubt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.uq.take();
					} catch (InterruptedException e) {
						return;
					}

					ReferenceCountUtil.release(m);
				}
			}
		});
		ubt.start();

		nbt = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Message m;
					try {
						m = connector.nq.take();
					} catch (InterruptedException e) {
						return;
					}
					if (m.hasRqGroupChallenge()) {
						challenge_rq(m);
					} else if (m.hasMiChallengeresult()) {
						challengeResult_1w(m);
					} else if (m.hasRqFileListing()) {
						file_listing_rq(m);
					} else if (m.hasMiAssignCvid()) {
						assign_1w(m);
					} else if (m.hasMiStreamStart()) {
						stream_start_ev(m);
					} else if (m.hasMiStreamStop()) {
						stream_stop_ev(m);
					} else if (m.hasRqChangeClientState()) {
						rq_change_client_state(m);
					} else if (m.hasRqFileHandle()) {
						rq_file_handle(m);
					} else if (m.hasRqAdvancedFileInfo()) {
						rq_advanced_file_info(m);
					} else {
						connector.cq.put(m.getId(), m);
					}

					ReferenceCountUtil.release(m);

				}
			}
		});
		nbt.start();
	}

	private void rq_change_client_state(Message m) {
		// TODO reply
		log.debug("Received state change request: {}", m.getRqChangeClientState().getNewState().toString());
		switch (m.getRqChangeClientState().getNewState()) {
		case FUNCTIONING_OFF:
			break;
		case FUNCTIONING_ON:
			break;
		case RESTART:
			Native.restart();
			break;
		case SHUTDOWN:
			Native.poweroff();
			break;
		case UNINSTALL:
			break;
		case HIBERNATE:
			Native.hibernate();
			break;
		case STANDBY:
			Native.standby();
			break;
		default:
			break;

		}

	}

	private void challenge_rq(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}

		String result = Crypto.sign(m.getRqGroupChallenge().getMagic(), Client.ic.getGroup().getKey());
		RS_GroupChallenge rs = RS_GroupChallenge.newBuilder().setResult(result).build();
		connector.handle.write(Message.newBuilder().setId(m.getId()).setRsGroupChallenge(rs).build());
	}

	private void challengeResult_1w(Message m) {
		if (connector.getState() != ConnectionState.AUTH_STAGE1) {
			return;
		}
		if (!m.getMiChallengeresult().getResult()) {
			log.debug("Authentication with server failed");
			connector.setState(ConnectionState.CONNECTED);
			return;
		} else {
			connector.setState(ConnectionState.AUTH_STAGE2);
		}

		Group group = Client.ic.getGroup();
		final String key = group.getKey();

		// Send authentication challenge
		final int id = IDGen.get();

		final String magic = CUtil.Misc.randString(64);
		RQ_GroupChallenge rq = RQ_GroupChallenge.newBuilder().setGroupName(group.getName()).setMagic(magic).build();
		connector.handle.write(Message.newBuilder().setId(id).setRqGroupChallenge(rq).build());

		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				try {
					Message rs = connector.cq.take(id, 7, TimeUnit.SECONDS);
					if (rs != null) {
						if (!rs.getRsGroupChallenge().getResult().equals(Crypto.sign(magic, key))) {
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
				}

				MI_GroupChallengeResult.Builder oneway = MI_GroupChallengeResult.newBuilder().setResult(flag);

				if (flag) {
					connector.setState(ConnectionState.AUTHENTICATED);

					oneway.setPd(Platform.Advanced.getFullProfile());
				} else {
					// TODO handle more
					connector.setState(ConnectionState.CONNECTED);
				}
				connector.handle.write(Message.newBuilder().setId(id).setMiChallengeresult(oneway.build()).build());

			}
		}).start();

	}

	private void file_listing_rq(Message m) {

		RQ_FileListing rq = m.getRqFileListing();
		log.debug("file_listing_rq. fmid: " + rq.getFmid());
		LocalFilesystem lf = ClientStore.LocalFilesystems.get(rq.getFmid());
		if (rq.hasUp() && rq.getUp()) {
			lf.up();
		} else if (rq.hasDown()) {
			if (rq.hasFromRoot() && rq.getFromRoot()) {
				lf.setPath(rq.getDown());
			} else {
				lf.down(rq.getDown());
			}

		}
		try {
			ClientStore.Connections.route(Message.newBuilder().setId(m.getId())
					.setRsFileListing(RS_FileListing.newBuilder().setPath(lf.pwd()).addAllListing(lf.list()))
					.setSid(m.getRid()).setRid(m.getSid()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rq_file_handle(Message m) {
		log.debug("rq_file_handle");
		ClientStore.Connections.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsFileHandle(RS_FileHandle.newBuilder()
						.setFmid(ClientStore.LocalFilesystems.add(new LocalFilesystem(true, true)))));
	}

	private void rq_advanced_file_info(Message m) {
		log.debug("rq_advance_file_info");
		ClientStore.Connections.route(Message.newBuilder().setId(m.getId()).setRid(m.getSid()).setSid(m.getRid())
				.setRsAdvancedFileInfo(LocalFilesystem.getInfo(m.getRqAdvancedFileInfo().getFile())));
	}

	private void assign_1w(Message m) {
		Common.cvid = m.getMiAssignCvid().getId();
		Client.clientDB.storeObject("cvid", Common.cvid);
	}

	private void stream_start_ev(Message m) {
		log.debug("stream_start_ev");
		Param p = m.getMiStreamStart().getParam();
		if (p.hasInfoParam()) {
			StreamStore.addStream(new CInfoSlave(p));
		}
	}

	private void stream_stop_ev(Message m) {
		log.debug("stream_stop_ev");
		StreamStore.removeStream(m.getMiStreamStop().getStreamID());

	}

}
