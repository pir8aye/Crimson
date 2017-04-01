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
import java.util.List;
import java.util.Observable;

import com.subterranean_security.crimson.core.proto.Delta.EV_NetworkDelta;

public class NetworkNode extends Observable {
	private int cvid;
	private NetworkNode parent;
	private List<NetworkNode> children;

	public NetworkNode(int cvid) {
		children = new ArrayList<NetworkNode>();
		this.cvid = cvid;
	}

	public NetworkNode(NetworkNode parent, int cvid) {
		this(cvid);
		this.parent = parent;
	}

	public void addNode(NetworkNode node) {
		children.add(node);
	}

	public NetworkNode getParent() {
		return parent;
	}

	public int getCvid() {
		return cvid;
	}

	/**
	 * Return the cvid of the next hop which leads to the target cvid
	 * 
	 * @param target
	 * @return
	 */
	public int getNextHop(int target) {

		return 0;
	}

	public List<NetworkNode> getAdjacent() {
		List<NetworkNode> nodeList = new ArrayList<NetworkNode>();
		if (parent != null)
			nodeList.add(parent);
		nodeList.addAll(children);
		return nodeList;
	}

	public NetworkNode findChildNode(int cvid) {
		if (this.cvid == cvid)
			return this;

		for (NetworkNode node : children) {
			NetworkNode test = node.findChildNode(cvid);
			if (test != null)
				return test;
		}

		return null;
	}

	/**
	 * Update the network tree with the specified delta
	 * 
	 * @param nd
	 */
	public void update(EV_NetworkDelta nd) {
		NetworkNode peer1 = findChildNode(nd.getPeer1());
		NetworkNode peer2 = findChildNode(nd.getPeer2());

		if (nd.getAdded()) {
			if (peer1 != null && peer2 == null) {
				peer1.addNode(new NetworkNode(nd.getPeer2()));
			} else if (peer1 == null && peer2 != null) {
				peer2.addNode(new NetworkNode(nd.getPeer1()));
			}
		} else {
			// TODO
		}

		setChanged();
		notifyObservers(nd);
	}

}
