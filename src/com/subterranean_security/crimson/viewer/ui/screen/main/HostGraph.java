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
package com.subterranean_security.crimson.viewer.ui.screen.main;

import java.awt.BorderLayout;
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
import com.subterranean_security.crimson.core.profile.AbstractAttribute;
import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.universal.JarUtil;
import com.subterranean_security.crimson.viewer.store.ProfileStore;

public class HostGraph extends JPanel implements MouseWheelListener {

	private static final long serialVersionUID = 1L;
	public mxGraph graph = new mxGraph();
	public Object parent = graph.getDefaultParent();

	public Object serverVertex;
	private mxGraphComponent graphComponent;

	public HashMap<Object, Integer> vertices = new HashMap<Object, Integer>();

	public HostGraph() {
		init();
		addInitialClients();
	}

	public void init() {

		// insert server
		try {
			graph.getModel().beginUpdate();

			mxStylesheet stylesheet = graph.getStylesheet();
			Hashtable<String, Object> style = new Hashtable<String, Object>();
			style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
			stylesheet.putCellStyle("style", style);

			graph.setCellsEditable(false);
			graph.setCellsResizable(false);
			graph.setAllowDanglingEdges(false);
			graph.setConnectableEdges(false);
			graph.setAllowNegativeCoordinates(false);
			// TODO change behavior if not connected
			serverVertex = graph.insertVertex(parent, null, "\n\n\nServer", 260, 135, 80, 30,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons32/general/server.png");

		} finally {
			graph.getModel().endUpdate();
		}
		setLayout(new BorderLayout(0, 0));

		graphComponent = new mxGraphComponent(graph);
		add(graphComponent);

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {

				Object cell = graphComponent.getCellAt(e.getX(), e.getY());

				if (cell != null && cell != serverVertex) {

					// get profile
					ClientProfile selected = ProfileStore.getClient(vertices.get(cell));

					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {

						// select the cell
						graph.setSelectionCell(cell);

						ContextMenu.getMenu(selected, "graph").show(graphComponent, e.getX(), e.getY());

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

	private void addInitialClients() {
		for (int i = 0; i < ProfileStore.clients.size(); i++) {
			addClient(ProfileStore.clients.get(i));
		}

	}

	public void addClient(ClientProfile p) {
		for (Object o : vertices.keySet()) {
			if (vertices.get(o) == p.getCid()) {
				// TODO
				return;
			}
		}

		// generate coordinates for the new vertex
		int xMin = 0;
		int xMax = this.getWidth();
		int yMin = 0;
		int yMax = this.getHeight();
		int x = 0;
		int y = 0;

		// TODO timeout if screen full
		while (true) {
			x = xMin + (int) (Math.random() * ((xMax - xMin) + 1));
			y = yMin + (int) (Math.random() * ((yMax - yMin) + 1));

			Object corner1 = graphComponent.getCellAt(x, y);
			Object corner2 = graphComponent.getCellAt(x + 80, y);
			Object corner3 = graphComponent.getCellAt(x, y + 30);
			Object corner4 = graphComponent.getCellAt(x + 80, y + 30);

			if (corner1 == null && corner2 == null && corner3 == null && corner4 == null) {

				break;
			}

		}
		graph.getModel().beginUpdate();
		String text = "";

		try {
			String iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
					+ p.getAttr(SimpleAttribute.OS_NAME).replaceAll(" ", "_").toLowerCase() + ".png";

			if (JarUtil.getResourceSize(iconLocation) == 0) {
				iconLocation = "/com/subterranean_security/crimson/viewer/ui/res/image/icons32/platform/viewer-"
						+ p.getAttr(SimpleAttribute.OS_FAMILY) + ".png";
			}

			Object v = graph.insertVertex(parent, null, "\n\n\n" + text, x, y, 80, 30,
					"shape=image;image=" + iconLocation);
			vertices.put(v, p.getCid());

			graph.insertEdge(parent, null, "", serverVertex, v);

		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void removeClient(ClientProfile p) {
		if (p == null) {
			return;
		}
		graph.getModel().beginUpdate();

		Object target = null;

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == p.getCid()) {
				// found the connection to remove
				target = entry.getKey();

				break;
			}

		}

		vertices.remove(target);

		try {
			Object[] ob = new Object[1];
			ob[0] = target;
			graph.removeCells(ob, true);
		} finally {
			graph.getModel().endUpdate();
		}

	}

	public void mouseWheelMoved(MouseWheelEvent e) {

		int notches = e.getWheelRotation();

	}

	private AbstractAttribute textType = SimpleAttribute.CLIENT_CID;

	// TODO updates should trigger this method
	public String getTextAt(ClientProfile cp) {
		if (textType instanceof SimpleAttribute) {
			SimpleAttribute sa = (SimpleAttribute) textType;
			return cp.getAttr(sa);

		} else {
			// TODO complex attribute
			return "";
		}

	}

}
