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
package com.subterranean_security.crimson.cv.ui.remote.ep;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.attribute.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_DISP;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel;
import com.subterranean_security.crimson.proto.core.net.sequences.Stream.RemoteParam.RMethod;

public class Settings extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<AttributeGroup> displays;
	private String[] displayStrings;

	private String[] methodStrings = new String[] { "Simple Poll (slowest)", "Native Hook (fastest)" };
	private String[] colorStrings = new String[] { "ARGB", "RGB", "555 RGB", "Grayscale" };
	private String[] compStrings = new String[] { "None", "Low", "Medium", "High" };

	private RDPanel parent;

	private JComboBox monitorBox;
	private JComboBox methodBox;
	private JComboBox colorBox;
	private JComboBox compBox;

	private JPanel settings;

	private boolean full;

	public Settings(List<AttributeGroup> displays, RDPanel parent, boolean full) {
		this.displays = displays;
		this.parent = parent;
		this.full = full;
		if (full) {
			FullSettings fs = new FullSettings();
			monitorBox = fs.monitorBox;
			methodBox = fs.methodBox;
			colorBox = fs.colorBox;
			compBox = fs.compBox;
			settings = fs;
		} else {
			ReducedSettings rs = new ReducedSettings();
			monitorBox = rs.monitorBox;
			colorBox = rs.colorBox;

			// not used, but initialize anyway
			methodBox = new JComboBox();
			compBox = new JComboBox();
			settings = rs;
		}
		loadSettings();
		init();
	}

	private void loadSettings() {
		displayStrings = new String[displays.size()];
		for (int i = 0; i < displays.size(); i++) {
			displayStrings[i] = (full ? "Monitor " : "M") + (i + 1) + " (" + displays.get(i).get(AK_DISP.WIDTH)
					+ " x " + displays.get(i).get(AK_DISP.HEIGHT) + ")";
		}

		if (parent.stream != null) {
			switch (parent.stream.param().getRemoteParam().getRmethod()) {
			case NATIVE:
				methodBox.setSelectedIndex(1);
				break;
			case POLL:
				methodBox.setSelectedIndex(0);
				break;
			default:
				break;

			}
		}
	}

	public RMethod getMethod() {
		String m = (String) methodBox.getSelectedItem();
		if (m.equals(methodStrings[0])) {
			return RMethod.POLL;
		} else if (m.equals(methodStrings[1])) {
			return RMethod.NATIVE;
		} else {
			return null;
		}
	}

	public String getMonitor() {
		return getDisplay().get(AK_DISP.ID);
	}

	public AttributeGroup getDisplay() {
		return displays.get(monitorBox.getSelectedIndex());
	}

	public int getColorType() {
		String m = (String) colorBox.getSelectedItem();
		if (m.equals(colorStrings[1])) {
			return BufferedImage.TYPE_INT_RGB;
		} else if (m.equals(colorStrings[2])) {
			return BufferedImage.TYPE_USHORT_555_RGB;
		} else if (m.equals(colorStrings[3])) {
			return BufferedImage.TYPE_BYTE_GRAY;
		} else {
			return BufferedImage.TYPE_INT_ARGB;
		}
	}

	public float getCompType() {
		String m = (String) colorBox.getSelectedItem();
		if (m.equals(compStrings[1])) {
			return 1.0f;
		} else if (m.equals(compStrings[2])) {
			return 0.5f;
		} else if (m.equals(compStrings[3])) {
			return 0.0f;
		} else {
			return -1f;
		}
	}

	private void resetStream() {
		if (parent.stream == null || !parent.stream.isRunning()) {
			return;
		}

		setAllEnabled(false);
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				StreamStore.removeStreamBySID(parent.stream.getStreamID());
				// dont use parent.stop() to avoid gui changes
				parent.running = false;
				parent.start();
				return null;
			}

			protected void done() {
				setAllEnabled(true);
			};

		}.execute();

	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		add(settings);

		monitorBox.setModel(new DefaultComboBoxModel(displayStrings));
		monitorBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					resetStream();
				}
			}
		});

		methodBox.setModel(new DefaultComboBoxModel(methodStrings));
		methodBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					resetStream();
				}
			}
		});

		colorBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					resetStream();
				}
			}
		});
		colorBox.setModel(new DefaultComboBoxModel(colorStrings));

		compBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					resetStream();
				}
			}
		});
		compBox.setModel(new DefaultComboBoxModel(compStrings));

	}

	private void setAllEnabled(boolean b) {
		methodBox.setEnabled(b);
		monitorBox.setEnabled(b);
		colorBox.setEnabled(b);
		compBox.setEnabled(b);
	}
}
