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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.net.TimeoutConstants;
import com.subterranean_security.crimson.core.net.MessageFuture.MessageTimeout;
import com.subterranean_security.crimson.core.store.LcvidStore;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.proto.core.net.sequences.Torrent.RQ_AddTorrent;

public final class TorrentCom {
	private TorrentCom() {
	}

	public static Outcome addTorrent(int cid, File torrent, String destination) {
		Outcome.Builder outcome = Outcome.newBuilder();

		try {
			Message m = NetworkStore.route(
					Message.newBuilder().setId(IDGen.msg()).setRid(cid).setSid(LcvidStore.cvid)
							.setRqAddTorrent(RQ_AddTorrent.newBuilder().setDestintation(destination)
									.setTorrentFile(ByteString.readFrom(new FileInputStream(torrent)))),
					TimeoutConstants.DEFAULT);

			if (m == null) {
				outcome.setResult(false).setComment("Request timed out");
			} else {
				outcome.setResult(m.getRsAddTorrent().getOutcome().getResult());
			}

		} catch (InterruptedException e) {
			outcome.setResult(false).setComment("Interrupted");
		} catch (IOException e1) {
			outcome.setResult(false).setComment("Torrent file not found");
		} catch (MessageTimeout e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outcome.build();
	}

}
