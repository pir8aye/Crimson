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
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.subterranean_security.crimson.core.storage.Headers;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

public class HostGraph extends JPanel implements MouseWheelListener {

	private static final long serialVersionUID = 1L;
	public mxGraph graph = new mxGraph();
	public Object parent = graph.getDefaultParent();

	public Object serverVertex;
	private mxGraphComponent graphComponent;

	public HashMap<Object, Integer> vertices = new HashMap<Object, Integer>();

	public HostGraph() {

		// insert server
		try {
			graph.getModel().beginUpdate();
			graph.setCellsEditable(false);
			graph.setCellsResizable(false);
			graph.setAllowDanglingEdges(false);
			graph.setConnectableEdges(false);
			graph.setAllowNegativeCoordinates(false);
			serverVertex = graph.insertVertex(parent, null, "\n\n\nServer", 260, 135, 80, 30,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons16/server.png");

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

					// get client id
					int id = vertices.get(cell);
					ClientProfile selected = null;

					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {

						JPopupMenu popup = new JPopupMenu();
						JMenuItem control = new JMenuItem();
						control.setText("Control Panel");
						control.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {

								new Thread() {
									public void run() {

										// open cp
									}
								}.start();
							}

						});
						// select the cell
						graph.setSelectionCell(cell);

						popup.add(control);
						popup.show(graphComponent, e.getX(), e.getY());

					} else {
						// left click
						MainFrame.main.dp.showDetail(selected);
					}

				} else {
					MainFrame.main.dp.closeDetail();
				}
			}

		});

	}

	public void addClient(ClientProfile p) {
		// generate coordinates for the new vertex
		int xMin = 0;
		int xMax = 600;
		int yMin = 0;
		int yMax = 300;
		int x = 0;
		int y = 0;

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
		switch (Headers.HOSTNAME) {
		case ACTIVE_WINDOW:
			break;
		case COUNTRY:
			break;
		case CPU_MODEL:
			break;
		case CPU_TEMP:
			break;
		case CPU_USAGE:
			break;
		case CRIMSON_VERSION:
			break;
		case EXTERNAL_IP:
			break;
		case HOSTNAME:
			text = p.getHostname();
			break;
		case INTERNAL_IP:
			break;
		case JAVA_VERSION:
			break;
		case LANGUAGE:
			break;
		case MESSAGE_PING:
			break;
		case MONITOR_COUNT:
			break;
		case OS_ARCH:
			break;
		case OS_FAMILY:
			break;
		case RAM_CAPACITY:
			break;
		case RAM_USAGE:
			break;
		case SCREEN_PREVIEW:
			break;
		case TIMEZONE:
			break;
		case USERNAME:
			break;
		case USER_STATUS:
			break;
		case VIRTUALIZATION:
			break;
		default:
			break;

		}

		try {

			Object v = graph.insertVertex(parent, null, "\n\n\n" + text, x, y, 80, 30,
					"shape=image;image=/com/subterranean_security/crimson/viewer/ui/res/image/icons16/viewer.png");
			vertices.put(v, p.getCvid());

			graph.insertEdge(parent, null, "", serverVertex, v);

		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void removeConnection(ClientProfile p) {
		if (p == null) {
			return;
		}
		graph.getModel().beginUpdate();

		Object target = null;

		for (Entry<Object, Integer> entry : vertices.entrySet()) {
			if (entry.getValue() == p.getCvid()) {
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

}
