package com.subterranean_security.crimson.cv.ui.remote.ep;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.subterranean_security.crimson.core.proto.Misc.GraphicsDisplay;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam;
import com.subterranean_security.crimson.core.proto.Stream.RemoteParam.RMethod;
import com.subterranean_security.crimson.core.stream.StreamStore;
import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class Settings extends JPanel {

	private static final long serialVersionUID = 1L;

	private GraphicsDisplay[] displays;
	private String[] displayStrings;

	private String[] methodStrings = new String[] { "Simple Poll (slowest)", "Delta Poll (medium)",
			"Native (fastest)" };

	private RDPanel parent;

	private JComboBox monitorBox;
	private JComboBox methodBox;
	private JLabel lblCapture;

	public Settings(GraphicsDisplay[] displays, RDPanel parent) {
		this.displays = displays;
		this.parent = parent;
		loadSettings();
		init();
	}

	private void loadSettings() {
		displayStrings = new String[displays.length];
		for (int i = 0; i < displays.length; i++) {
			displayStrings[i] = "Monitor " + (i + 1) + " (" + displays[i].getWidth() + " x " + displays[i].getHeight()
					+ ")";
		}

		if (parent.stream != null) {
			switch (parent.stream.param.getRemoteParam().getRmethod()) {
			case NATIVE:
				methodBox.setSelectedIndex(2);
				break;
			case POLL:
				methodBox.setSelectedIndex(0);
				break;
			case POLL_DELTA:
				methodBox.setSelectedIndex(1);
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
			return RMethod.POLL_DELTA;
		} else if (m.equals(methodStrings[2])) {
			return RMethod.NATIVE;
		} else {
			return null;
		}
	}

	public String getMonitor() {
		return getDisplay().getId();
	}

	public GraphicsDisplay getDisplay() {
		return displays[monitorBox.getSelectedIndex()];
	}

	private void resetStream() {
		if (parent.stream == null) {
			return;
		}
		StreamStore.removeStream(parent.stream.getStreamID());

		parent.stream = new RemoteMaster(
				RemoteParam.newBuilder().setRmethod(getMethod()).setMonitor(getMonitor()).build(), parent.cvid,
				parent.rdArea);
		StreamStore.addStream(parent.stream);
		parent.stream.start();
		parent.rdArea.setStream(parent.stream);
	}

	private void init() {
		setLayout(null);

		monitorBox = new JComboBox();
		monitorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		monitorBox.setModel(new DefaultComboBoxModel(displayStrings));
		monitorBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					methodBox.setEnabled(false);
					monitorBox.setEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							methodBox.setEnabled(true);
							monitorBox.setEnabled(true);
						};

					}.execute();

				}
			}
		});

		monitorBox.setBounds(130, 7, 164, 20);
		add(monitorBox);

		methodBox = new JComboBox();
		methodBox.setModel(new DefaultComboBoxModel(methodStrings));
		methodBox.setFont(new Font("Dialog", Font.BOLD, 10));
		methodBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					methodBox.setEnabled(false);
					monitorBox.setEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							resetStream();
							return null;
						}

						protected void done() {
							methodBox.setEnabled(true);
							monitorBox.setEnabled(true);
						};

					}.execute();

				}
			}
		});
		methodBox.setBounds(130, 31, 164, 20);
		add(methodBox);

		JLabel lblCaptureDevice = new JLabel("Capture Device:");
		lblCaptureDevice.setIcon(UIUtil.getIcon("icons16/general/viewer.png"));
		lblCaptureDevice.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCaptureDevice.setBounds(12, 10, 117, 15);
		add(lblCaptureDevice);

		lblCapture = new JLabel("Capture Mode:");
		lblCapture.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblCapture.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCapture.setBounds(12, 34, 117, 15);
		add(lblCapture);

	}
}
