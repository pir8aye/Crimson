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
package com.subterranean_security.crimson.core.net;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkAdded;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkRemoved;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.NodeAdded;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.NodeRemoved;
import com.subterranean_security.crimson.sv.profile.Profile;

public class NetworkNode extends Observable {
	private static final Logger log = LoggerFactory.getLogger(NetworkNode.class);

	private int cvid;
	private List<NetworkNode> adjacent;

	public NetworkNode(int cvid) {
		adjacent = new ArrayList<NetworkNode>();
		this.cvid = cvid;
	}

	public NetworkNode(NetworkNode parent, int cvid) {
		this(cvid);
	}

	public void addNode(NetworkNode node) {
		adjacent.add(node);
		node.adjacent.add(this);
	}

	public void removeNode(NetworkNode node) {
		adjacent.remove(node);
		node.adjacent.remove(this);
	}

	/**
	 * @return The cvid associated with this node
	 */
	public int getCvid() {
		return cvid;
	}

	public Profile getProfile() {
		return null;
	}

	public List<NetworkNode> getAdjacent() {
		return adjacent;
	}

	public NetworkNode findNode(int cvid) {
		return findNode(cvid, new LinkedList<>());
	}

	private NetworkNode findNode(int cvid, LinkedList<NetworkNode> visited) {
		if (this.cvid == cvid)
			return this;

		for (NetworkNode node : adjacent) {
			if (!visited.contains(node)) {
				visited.add(node);
				NetworkNode test = node.findNode(cvid);
				if (test != null)
					return test;
			}

		}

		return null;
	}

	/**
	 * Update the network graph with the specified delta
	 * 
	 * @param nd
	 */
	public void update(EV_NetworkDelta nd) {
		if (nd.getNodeAdded() != null) {
			NodeAdded na = nd.getNodeAdded();
			NetworkNode parent = findNode(na.getParent());
			if (parent != null) {
				parent.addNode(new NetworkNode(na.getCvid()));
				setChanged();
			} else {
				log.debug("[Node {}] Cannot find parent: {}", cvid, na.getParent());
			}
		}

		if (nd.getNodeRemoved() != null) {
			NodeRemoved nr = nd.getNodeRemoved();
			NetworkNode removal = findNode(nr.getCvid());
			if (removal != null) {
				for (NetworkNode node : removal.getAdjacent()) {
					node.removeNode(removal);
					setChanged();
				}
			} else {
				log.debug("[Node {}] Cannot find node to remove: {}", cvid, nr.getCvid());
			}
		}

		if (nd.getLinkAdded() != null) {
			LinkAdded la = nd.getLinkAdded();
			NetworkNode node1 = findNode(la.getCvid1());
			NetworkNode node2 = findNode(la.getCvid2());

			if (node1 != null && node2 != null) {
				node1.addNode(node2);
				setChanged();
			} else {
				log.debug("[Node {}] Cannot link nodes: {} and {}", cvid, la.getCvid1(), la.getCvid2());
			}
		}

		if (nd.getLinkRemoved() != null) {
			LinkRemoved lr = nd.getLinkRemoved();
			NetworkNode node1 = findNode(lr.getCvid1());
			NetworkNode node2 = findNode(lr.getCvid2());

			if (node1 != null && node2 != null) {
				node1.removeNode(node2);
				setChanged();
			} else {
				log.debug("[Node {}] Cannot unlink nodes: {} and {}", cvid, lr.getCvid1(), lr.getCvid2());
			}
		}

		notifyObservers(nd);
	}

}
