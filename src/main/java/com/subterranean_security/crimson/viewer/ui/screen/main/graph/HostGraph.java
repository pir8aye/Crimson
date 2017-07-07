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
package com.subterranean_security.crimson.viewer.ui.screen.main.graph;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.core.attribute.keys.SingularKey;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_OS;
import com.subterranean_security.crimson.core.net.NetworkNode;
import com.subterranean_security.crimson.core.store.NetworkStore;
import com.subterranean_security.crimson.core.store.ProfileStore;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkAdded;
import com.subterranean_security.crimson.proto.core.net.sequences.Delta.EV_NetworkDelta.LinkRemoved;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.Profile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.util.JarUtil;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ui.screen.main.ContextMenuFactory;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class HostGraph extends JPanel implements MouseWheelListener, Observer {

	private static final Logger log = LoggerFactory.getLogger(HostGraph.class);

	private static final long serialVersionUID = 1L;

	private AttributeKey textType = AK_NET.HOSTNAME;

	private final int vertexWidth = 80;
	private final int vertexHeight = 30;

	public mxGraph graph;
	public Object parent;

	public Object serverVertex;
	private mxGraphComponent graphComponent;

	public HashMap<Object, Integer> vertices = new HashMap<Object, Integer>();

	public HostGraph() {
		init();
		initStyles();

		if (ViewerState.isOnline()) {
			addServer();
			addInitialClients();
		}

		addViewer(ProfileStore.getLocalViewer());

		NetworkStore.getNetworkTree().addObserver(this);

	}

	public void init() {

		setLayout(new BorderLayout(0, 0));

		graph = new mxGraph() {
			@Override
			public boolean isCellMovable(Object arg0) {
				if (model.isEdge(arg0)) {
					return false;
				}
				return super.isCellMovable(arg0);
			}

			@Override
			public boolean isCellSelectable(Object arg0) {
				if (model.isEdge(arg0)) {
					return false;
				}
				return super.isCellSelectable(arg0);
			}

		};
		graph.setCellsEditable(false);
		graph.setCellsResizable(false);
		graph.setAllowDanglingEdges(false);
		graph.setConnectableEdges(false);
		graph.setAllowNegativeCoordinates(false);

		parent = graph.getDefaultParent();

		graphComponent = new mxGraphComponent(graph);
		add(graphComponent);

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {

				Object cell = graphComponent.getCellAt(e.getX(), e.getY());

				if (cell != null && cell != serverVertex && vertices.containsKey(cell)) {

					// get profile
					ClientProfile selected = ProfileStore.getClient(vertices.get(cell));

					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {

						// select the cell
						graph.setSelectionCell(cell);

						ContextMenuFactory.getMenu(selected, "graph").show(graphComponent, e.getX(), e.getY());

					} else if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
						// left click
						MainFrame.main.dp.showDetail(selected);
					}

				} else {
					MainFrame.main.dp.closeDetail();
				}
			}

		});

	}

	private void initStyles() {
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
		stylesheet.putCellStyle("style", style);
	}

	public void select(ClientProfile cp) {
		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == cp.getCvid()) {
				graph.setSelectionCell(entry.getKey());
				return;
			}
		}

	}

	/**
	 * Add the server to the graph
	 */
	private void addServer() {
		try {
			graph.getModel().beginUpdate();

			// TODO change behavior if not connected
			serverVertex = graph.insertVertex(parent, null, "\n\n\nServer", 260, 135, vertexWidth, vertexHeight,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons32/general/server.png");

		} finally {
			graph.getModel().endUpdate();
		}
	}

	/**
	 * Add a viewer to the graph
	 * 
	 * @param vp
	 */
	private void addViewer(ViewerProfile vp) {
		try {
			graph.getModel().beginUpdate();

			Point point = findSpot();

			serverVertex = graph.insertVertex(parent, null, "\n\n\nViewer", point.x, point.y, vertexWidth, vertexHeight,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons32/general/viewer.png");

		} finally {
			graph.getModel().endUpdate();
		}
	}

	private void addInitialClients() {
		for (ClientProfile client : ProfileStore.getClients()) {
			addClient(client);
		}
	}

	private Point findSpot(int xMin, int yMin, int xMax, int yMax) {
		int x = 0;
		int y = 0;

		int iteration = 0;
		do {
			iteration++;
			x = xMin + (int) (Math.random() * ((xMax - xMin) + 1));
			y = yMin + (int) (Math.random() * ((yMax - yMin) + 1));

		} while (!suitablePoint(x, y) && iteration < 1000);

		return new Point(x, y);
	}

	private boolean suitablePoint(int x, int y) {
		if (graphComponent.getCellAt(x, y) != null || graphComponent.getCellAt(x + vertexWidth, y) != null
				|| graphComponent.getCellAt(x, y + vertexHeight) != null
				|| graphComponent.getCellAt(x + vertexWidth, y + vertexHeight) != null) {
			return false;
		}
		return true;
	}

	private Point findSpot() {
		return findSpot(15, 15, 520 - vertexWidth - 15, 270 - vertexHeight - 15);
	}

	public void addClient(ClientProfile p) {
		if (vertices.values().contains(p.getCvid())) {
			// this client is already present
			return;
		}

		// find a spot for the new vertex
		Point point = findSpot();

		graph.getModel().beginUpdate();

		try {
			// TODO move to util
			String iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
					+ p.get(AK_OS.NAME).replaceAll(" ", "_").toLowerCase() + ".png";

			if (!JarUtil.containsResource(iconLocation)) {
				iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
						+ p.get(AK_OS.FAMILY) + ".png";
			}

			Object v = graph.insertVertex(parent, null, "\n\n\n" + getTextFor(p), point.x, point.y, vertexWidth,
					vertexHeight, "shape=image;image=" + iconLocation);
			vertices.put(v, p.getCvid());

		} finally {
			graph.getModel().endUpdate();
		}

		for (NetworkNode node : NetworkStore.getNetworkTree().getAdjacent()) {
			addConnection(p.getCvid(), node.getCvid());
		}

	}

	public void addConnection(int peer1, int peer2) {
		Object o1 = null;
		Object o2 = null;

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (o1 != null && o2 != null)
				break;
			if (entry.getValue() == peer1)
				o1 = entry.getKey();
			else if (entry.getValue() == peer2)
				o2 = entry.getKey();
		}

		graph.getModel().beginUpdate();
		try {
			graph.insertEdge(parent, null, "", o1, o2,
					"startArrow=oval;endArrow=oval;sourcePerimeterSpacing=4;startFill=0;endFill=0;");
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void removeConnection(int peer1, int peer2) {
		Object o1 = null;
		Object o2 = null;

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (o1 != null && o2 != null)
				break;
			if (entry.getValue() == peer1)
				o1 = entry.getKey();
			else if (entry.getValue() == peer2)
				o2 = entry.getKey();
		}

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getEdgesBetween(o1, o2));
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void remove(int cvid) {
		graph.getModel().beginUpdate();

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == cvid) {
				// found the vertex to remove
				vertices.remove(entry.getKey());

				try {
					graph.removeCells(new Object[] { entry.getKey() }, true);
				} finally {
					graph.getModel().endUpdate();
				}

				return;
			}

		}

	}

	private String getTextFor(ClientProfile cp) {
		if (textType instanceof SingularKey) {
			SingularKey sk = (SingularKey) textType;
			return cp.get(sk);
		} else {
			// TODO complex attribute
			return "";
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		NetworkNode root = (NetworkNode) arg0;
		EV_NetworkDelta nd = (EV_NetworkDelta) arg1;

		if (nd.getNodeAdded() != null) {
			Profile profile = ProfileStore.getProfile(nd.getNodeAdded().getCvid());
			if (profile instanceof ClientProfile) {
				addClient((ClientProfile) profile);
			} else if (profile instanceof ViewerProfile) {
				addViewer((ViewerProfile) profile);
			}
		}

		if (nd.getNodeRemoved() != null) {
			remove(nd.getNodeRemoved().getCvid());
		}

		if (nd.getLinkAdded() != null) {
			LinkAdded la = nd.getLinkAdded();
			addConnection(la.getCvid1(), la.getCvid2());
		}

		if (nd.getLinkRemoved() != null) {
			LinkRemoved lr = nd.getLinkRemoved();
			removeConnection(lr.getCvid1(), lr.getCvid2());
		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub

	}

}
