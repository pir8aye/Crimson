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

import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.subterranean_security.crimson.core.attribute.keys.AKeySimple;
import com.subterranean_security.crimson.core.attribute.keys.AttributeKey;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.sv.profile.ViewerProfile;
import com.subterranean_security.crimson.universal.util.JarUtil;
import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.screen.main.ContextMenuFactory;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class HostGraph extends JPanel implements MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private AttributeKey textType = AKeySimple.NET_HOSTNAME;

	private final int vertexWidth = 80;
	private final int vertexHeight = 30;

	public mxGraph graph;
	public Object parent;

	public Object serverVertex;
	private mxGraphComponent graphComponent;

	public HashMap<Object, Integer> vertices = new HashMap<Object, Integer>();

	public HostGraph() {
		init();
		if (ViewerState.isOnline()) {
			addServer();
			addInitialClients();
		}

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

	public void select(ClientProfile cp) {
		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == cp.getCid()) {
				graph.setSelectionCell(entry.getKey());
				return;
			}
		}

	}

	private void addServer() {
		// insert server
		try {
			graph.getModel().beginUpdate();

			mxStylesheet stylesheet = graph.getStylesheet();
			Hashtable<String, Object> style = new Hashtable<String, Object>();
			style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
			stylesheet.putCellStyle("style", style);

			// TODO change behavior if not connected
			serverVertex = graph.insertVertex(parent, null, "\n\n\nServer", 260, 135, vertexWidth, vertexHeight,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons32/general/server.png");

		} finally {
			graph.getModel().endUpdate();
		}
	}

	private void addViewer(ViewerProfile vp) {

	}

	private void addInitialClients() {
		for (int i = 0; i < ProfileStore.clients.size(); i++) {
			addClient(ProfileStore.clients.get(i));
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
		if (vertices.values().contains(p.getCid())) {
			// this client is already present
			return;
		}

		// find a spot for the new vertex
		Point point = findSpot();

		graph.getModel().beginUpdate();

		try {
			// TODO move to util
			String iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
					+ p.get(AKeySimple.OS_NAME).replaceAll(" ", "_").toLowerCase() + ".png";

			if (JarUtil.getResourceSize(iconLocation) == 0) {
				iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
						+ p.get(AKeySimple.OS_FAMILY) + ".png";
			}

			Object v = graph.insertVertex(parent, null, "\n\n\n" + getTextFor(p), point.x, point.y, vertexWidth,
					vertexHeight, "shape=image;image=" + iconLocation);
			vertices.put(v, p.getCid());

			graph.insertEdge(parent, null, "", serverVertex, v,
					"startArrow=oval;endArrow=oval;sourcePerimeterSpacing=4;startFill=0;endFill=0;");

		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void removeClient(ClientProfile p) {
		if (p == null) {
			return;
		}
		graph.getModel().beginUpdate();

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == p.getCid()) {
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

	public void mouseWheelMoved(MouseWheelEvent e) {

		int notches = e.getWheelRotation();

	}

	private String getTextFor(ClientProfile cp) {
		if (textType instanceof AKeySimple) {
			AKeySimple sa = (AKeySimple) textType;
			return cp.get(sa);
		} else {
			// TODO complex attribute
			return "";
		}

	}

}
