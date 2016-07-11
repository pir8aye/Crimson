package com.subterranean_security.crimson.cv.ui.remote.ep;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	private RDPanel parent;

	private JComboBox monitorBox;
	private JComboBox methodBox;
	private JLabel lblCapture;

	public Settings(GraphicsDisplay[] displays, RDPanel parent) {
		this.displays = displays;
		this.parent = parent;
		loadDisplayStrings();
		init();
	}

	private void loadDisplayStrings() {
		displayStrings = new String[displays.length];
		for (int i = 0; i < displays.length; i++) {
			displayStrings[i] = "Monitor " + (i + 1) + " (" + displays[i].getWidth() + " x " + displays[i].getHeight()
					+ ")";
		}
	}

	private void init() {
		setLayout(null);

		monitorBox = new JComboBox();
		monitorBox.setFont(new Font("Dialog", Font.BOLD, 10));
		monitorBox.setModel(new DefaultComboBoxModel(displayStrings));
		monitorBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			}
		});
		monitorBox.setBounds(155, 7, 164, 20);
		add(monitorBox);

		methodBox = new JComboBox();
		methodBox.setFont(new Font("Dialog", Font.BOLD, 10));
		methodBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					methodBox.setEnabled(false);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							StreamStore.removeStream(parent.stream.getStreamID());
							parent.stream = new RemoteMaster(
									RemoteParam.newBuilder().setRmethod(RMethod.POLL_DELTA)// TODO
											.setMonitor(displays[methodBox.getSelectedIndex()].getId()).build(),
									parent.cvid, parent.rdArea);
							StreamStore.addStream(parent.stream);
							parent.stream.start();
							parent.rdArea.setStream(parent.stream);
							return null;
						}

						protected void done() {
							methodBox.setEnabled(true);
						};

					}.execute();

				}
			}
		});
		methodBox.setBounds(155, 35, 164, 20);
		add(methodBox);

		JLabel lblCaptureDevice = new JLabel("Capture Device:");
		lblCaptureDevice.setIcon(UIUtil.getIcon("icons16/general/viewer.png"));
		lblCaptureDevice.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCaptureDevice.setBounds(12, 10, 125, 15);
		add(lblCaptureDevice);

		lblCapture = new JLabel("Capture Mode:");
		lblCapture.setIcon(UIUtil.getIcon("icons16/general/processor.png"));
		lblCapture.setFont(new Font("Dialog", Font.BOLD, 10));
		lblCapture.setBounds(12, 37, 125, 15);
		add(lblCapture);

	}
}
