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
package com.subterranean_security.crimson.viewer.ui.screen.netman.auth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.attribute.keys.plural.AK_AUTH;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_VIEWER;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.IDGen;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.AuthType;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;
import com.subterranean_security.crimson.sv.store.ProfileStore;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.common.components.EntropyHarvester;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

public class CreateGroup extends JPanel {

	private static final long serialVersionUID = 1L;

	private Timer timer = new Timer();
	private GroupTimer gt = new GroupTimer();

	private EPanel ep;

	private JLabel key_prefix;
	private JTextField textField;

	private StatusLabel sl;

	public CreateGroup(EPanel ep) {
		this.ep = ep;
		init();
		timer.schedule(gt, 0, 750);
	}

	public void init() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		EntropyHarvester eh = new EntropyHarvester();
		eh.setPreferredSize(new Dimension(500, 100));
		eh.hpanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				gt.mix(e.getPoint());
			}
		});

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setMargin(new Insets(2, 4, 2, 4));
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIStore.ECreateGroup = null;
				ep.drop();
			}
		});
		panel.add(btnCancel);

		JButton btnCreate = new JButton("Create");
		btnCreate.setMargin(new Insets(2, 4, 2, 4));
		btnCreate.setFont(new Font("Dialog", Font.BOLD, 10));
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// TODO swingworker
				new Thread(new Runnable() {
					public void run() {
						sl.setInfo("Creating group");
						timer.cancel();
						Outcome outcome = ViewerCommands.createAuthMethod(
								AuthMethod.newBuilder().setId(IDGen.auth()).setCreation(new Date().getTime())
										.addOwner(ProfileStore.getLocalViewer().get(AK_VIEWER.USER))
										.setType(AuthType.GROUP).setName(textField.getText())
										.setGroupSeedPrefix(key_prefix.getText() + RandomUtil.randString(32)).build());
						if (outcome.getResult()) {
							sl.setGood("Group created successfully");
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
							}
							UIStore.ECreateGroup = null;
							ep.drop();
						} else {
							sl.setBad(outcome.getComment().isEmpty() ? "Creation failed!"
									: "Failed: " + outcome.getComment());
							timer = new Timer();
							gt = new GroupTimer();
							timer.schedule(gt, 0, 750);
						}

					}
				}).start();

			}
		});
		panel.add(btnCreate);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(500, 50));
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.add(panel_3);
		panel_3.setLayout(null);

		JLabel lblGroupName = new JLabel("Group Name:");
		lblGroupName.setBounds(12, 6, 102, 14);
		lblGroupName.setFont(new Font("Dialog", Font.BOLD, 11));
		panel_3.add(lblGroupName);

		textField = new JTextField();
		textField.setBounds(119, 3, 136, 19);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				btnCreate.setEnabled(testValues());
			}
		});
		panel_3.add(textField);
		textField.setColumns(10);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Collect entropy from clients");
		chckbxNewCheckBox.setEnabled(false);
		chckbxNewCheckBox.setBounds(285, 3, 201, 21);
		chckbxNewCheckBox.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_3.add(chckbxNewCheckBox);

		JCheckBox chckbxCollectEntropyFrom = new JCheckBox("Collect entropy from user");
		chckbxCollectEntropyFrom.setSelected(true);
		chckbxCollectEntropyFrom.setEnabled(false);
		chckbxCollectEntropyFrom.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxCollectEntropyFrom.setBounds(285, 23, 201, 21);
		panel_3.add(chckbxCollectEntropyFrom);

		JPanel panel_4 = new JPanel();
		panel_4.add(eh);
		panel_2.add(panel_4);

		JPanel panel_5 = new JPanel();
		add(panel_5, BorderLayout.NORTH);
		panel_5.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_5.add(panel_1, BorderLayout.CENTER);
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Key Prefix:");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		panel_1.add(lblNewLabel, BorderLayout.WEST);

		key_prefix = new JLabel("");
		key_prefix.setFont(new Font("Dialog", Font.BOLD, 11));
		key_prefix.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(key_prefix, BorderLayout.CENTER);

		JLabel lbl_invisible = new JLabel("Key Prefix:");
		lbl_invisible.setForeground(UIManager.getColor("Panel.background"));
		lbl_invisible.setFont(new Font("Dialog", Font.BOLD, 11));
		lbl_invisible.setFocusable(false);
		panel_1.add(lbl_invisible, BorderLayout.EAST);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_5.add(panel_6, BorderLayout.NORTH);
		panel_6.setLayout(new BorderLayout(0, 0));

		sl = new StatusLabel("Enter details to create a new authentication group");
		panel_6.add(sl);

	}

	private boolean testValues() {
		if (!ValidationUtil.group(textField.getText())) {
			sl.setBad("Invalid group name");
			return false;
		}
		for (AttributeGroup authMethod : ProfileStore.getServer().getAuthMethods()) {
			if (authMethod.getStr(AK_AUTH.NAME).equals(textField.getText())) {
				sl.setBad("Group name in use");
				return false;
			}
		}
		return true;
	}

	class GroupTimer extends TimerTask {

		private Random rand = new Random();
		private String last = "" + new Date().getTime();

		private int upper = 20;
		private int lower = 10;

		@Override
		public void run() {
			hash();
			display(50);
		}

		private void hash() {
			last = CryptoUtil.hashSign(last, RandomUtil.randString(rand.nextInt(upper - lower + 1) + lower))
					.replaceAll("\\+|/", RandomUtil.randString(1));
		}

		private void display(int delay) {
			String data = last;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 16; i++) {
				sb.append(' ');
				sb.append(data.charAt(i));
			}
			key_prefix.setText("");
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			key_prefix.setText(sb.toString().substring(1).toUpperCase());
		}

		public void mix(Point p) {
			last += p.x + p.y;
			hash();
			display(0);
		}

	}
}
