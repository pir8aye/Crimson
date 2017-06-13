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
package com.subterranean_security.crimson.viewer.ui.screen.generator.tabs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.util.CryptoUtil;
import com.subterranean_security.crimson.core.util.RandomUtil;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.EntropyHarvester;

public class ATab extends JPanel {

	private static final long serialVersionUID = 1L;

	private String group_text = "Group authentication is the most secure mechanism. A \"group key\" is embedded in the client and only servers that posses this key may authenticate with the client and vice versa.";
	private String pass_text = "A simple password is used to authenticate the client. Choose a strong password because the client will be able to authenticate with any server with the password installed.";
	private String none_text = "The client will request to skip authentication entirely.  SSL/TLS will be used, but the identity of the client will be unverifiable.";

	public JComboBox<ImageIcon> authType;
	public JLabel key_prefix;
	public JTextField fld_group_name;

	private Timer timer = new Timer();
	private GroupTimer gt = new GroupTimer();

	public JComboBox<String> groupSelectionBox;

	private JPanel cards;

	private JTextArea txtAuthDescription;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	public JTextField fld_password_name;

	public JCheckBox chckbxDontInstallPassword;

	public ATab() {
		init();
		loadGroups();
		timer.schedule(gt, 0, 750);
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));
		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new TitledBorder(UICommon.basic, "Authentication Type", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(panel_8, BorderLayout.NORTH);
		panel_8.setLayout(new BorderLayout(0, 0));

		txtAuthDescription = new JTextArea();
		txtAuthDescription.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtAuthDescription.setFont(new Font("Dialog", Font.PLAIN, 10));
		txtAuthDescription.setOpaque(false);
		txtAuthDescription.setWrapStyleWord(true);
		txtAuthDescription.setLineWrap(true);
		txtAuthDescription.setText(group_text);
		panel_8.add(txtAuthDescription, BorderLayout.CENTER);

		JPanel panel_10 = new JPanel();
		panel_8.add(panel_10, BorderLayout.WEST);

		cards = new JPanel();
		add(cards, BorderLayout.CENTER);
		cards.setLayout(new CardLayout(0, 0));

		ImageIcon group = UIUtil.getIcon("icons16/general/group.png");
		group.setDescription("Group");
		ImageIcon password = UIUtil.getIcon("icons16/general/textfield_password.png");
		password.setDescription("Password");
		ImageIcon na = UIUtil.getIcon("icons16/general/radioactivity.png");
		na.setDescription("None");
		panel_10.setLayout(new BorderLayout(0, 0));
		panel_10.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		panel_10.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		panel_10.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		authType = new JComboBox<ImageIcon>();
		panel.add(authType, BorderLayout.NORTH);
		authType.setFont(new Font("Dialog", Font.BOLD, 10));
		authType.setRenderer(new AuthComboBoxRenderer());
		authType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshGroupSelection();
			}
		});
		authType.setModel(new DefaultComboBoxModel(new ImageIcon[] { group, password, na }));

		groupSelectionBox = new JComboBox<String>();
		panel.add(groupSelectionBox, BorderLayout.SOUTH);
		groupSelectionBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshGroupSelection();
			}
		});

		JPanel blank = new JPanel();
		cards.add(blank, "blank");

		JPanel authpanel_group = new JPanel();
		cards.add(authpanel_group, "group");
		authpanel_group.setLayout(new BorderLayout(0, 0));

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		authpanel_group.add(panel_11, BorderLayout.NORTH);
		panel_11.setLayout(new BorderLayout(0, 0));

		JLabel lblGroupKeyPrefix = new JLabel("Group Key Prefix:");
		lblGroupKeyPrefix.setFont(new Font("Dialog", Font.BOLD, 11));
		panel_11.add(lblGroupKeyPrefix, BorderLayout.WEST);

		key_prefix = new JLabel("4 5 9 2 0 1 5 3 8 3 1 3 4 2 5 5");
		key_prefix.setFont(new Font("Dialog", Font.BOLD, 11));
		key_prefix.setHorizontalAlignment(SwingConstants.CENTER);
		panel_11.add(key_prefix, BorderLayout.CENTER);

		JPanel panel_12 = new JPanel();
		authpanel_group.add(panel_12);
		panel_12.setLayout(new BorderLayout(0, 0));

		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Create new group",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_12.add(panel_13, BorderLayout.NORTH);
		panel_13.setLayout(new BorderLayout(0, 0));

		JLabel lblGroupName = new JLabel("  Group Name:");
		lblGroupName.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_13.add(lblGroupName, BorderLayout.WEST);

		JPanel panel_14 = new JPanel();
		panel_13.add(panel_14, BorderLayout.EAST);

		fld_group_name = new JTextField();
		fld_group_name.setBounds(130, 20, 117, 19);
		panel_14.add(fld_group_name);
		fld_group_name.setColumns(10);

		JButton btnRandom_1 = new JButton("Randomize");
		btnRandom_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fld_group_name.setText("Grp-" + RandomUtil.randString(5));
			}
		});
		btnRandom_1.setMargin(new Insets(2, 2, 2, 2));
		btnRandom_1.setFont(new Font("Dialog", Font.BOLD, 9));
		btnRandom_1.setBounds(259, 20, 70, 19);
		panel_14.add(btnRandom_1);

		EntropyHarvester eh = new EntropyHarvester();
		eh.hpanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				gt.mix(e.getPoint());
			}
		});
		panel_12.add(eh, BorderLayout.CENTER);

		JPanel authpanel_password = new JPanel();
		authpanel_password.setBorder(new TitledBorder(UICommon.basic, "Password Authentication", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		cards.add(authpanel_password, "Password");
		authpanel_password.setLayout(null);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPassword.setBounds(220, 20, 66, 18);
		authpanel_password.add(lblPassword);

		JLabel lblRetype = new JLabel("Retype:");
		lblRetype.setFont(new Font("Dialog", Font.BOLD, 10));
		lblRetype.setBounds(220, 42, 66, 18);
		authpanel_password.add(lblRetype);

		passwordField = new JPasswordField();
		passwordField.setBounds(314, 20, 126, 18);
		authpanel_password.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(314, 42, 126, 18);
		authpanel_password.add(passwordField_1);

		fld_password_name = new JTextField();
		fld_password_name.setBounds(79, 20, 108, 18);
		authpanel_password.add(fld_password_name);
		fld_password_name.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Dialog", Font.BOLD, 10));
		lblName.setBounds(12, 20, 66, 18);
		authpanel_password.add(lblName);

		chckbxDontInstallPassword = new JCheckBox("Don't install password on server");
		chckbxDontInstallPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxDontInstallPassword.setBounds(12, 80, 258, 23);
		authpanel_password.add(chckbxDontInstallPassword);

		JPanel authpanel_none = new JPanel();
		cards.add(authpanel_none, "None");

		JLabel lblNewLabel = new JLabel("Warning: Authentication mode is set to NONE");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		authpanel_none.add(lblNewLabel);

		JLabel lblThisClientWill = new JLabel("This client will be able to authenticate with any server!");
		lblThisClientWill.setFont(new Font("Dialog", Font.BOLD, 10));
		authpanel_none.add(lblThisClientWill);
	}

	private void loadGroups() {
		ArrayList<AuthMethod> groups = new ArrayList<AuthMethod>();
		for (AuthMethod am : ViewerProfileStore.getServer().authMethods) {
			if (am.getType() == AuthType.GROUP) {
				groups.add(am);
			}
		}
		String[] g = new String[groups.size() + 1];
		g[g.length - 1] = "Create Group";
		for (int i = 0; i < g.length - 1; i++) {
			g[i] = groups.get(i).getName();
		}
		groupSelectionBox.setModel(new DefaultComboBoxModel<String>(g));
		refreshGroupSelection();
	}

	private void refreshGroupSelection() {
		switch (((ImageIcon) authType.getSelectedItem()).getDescription()) {
		case "Group": {
			txtAuthDescription.setText(group_text);
			if (((String) groupSelectionBox.getSelectedItem()).equals("Create Group")) {
				((CardLayout) cards.getLayout()).show(cards, "group");
			} else {
				((CardLayout) cards.getLayout()).show(cards, "blank");
			}
			groupSelectionBox.setVisible(true);
			break;
		}
		case "Password": {
			txtAuthDescription.setText(pass_text);
			((CardLayout) cards.getLayout()).show(cards, "Password");
			groupSelectionBox.setVisible(false);
			break;
		}
		case "None": {
			txtAuthDescription.setText(none_text);
			((CardLayout) cards.getLayout()).show(cards, "None");
			groupSelectionBox.setVisible(false);
			break;
		}
		}

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

	public void cancelTimer() {
		timer.cancel();
		timer = null;

	}

	public String getGroupPrefix() {
		try {
			return CryptoUtil.hash("SHA-256", key_prefix.getText().toCharArray());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getPassword() {
		return UIUtil.getPassword(passwordField);
	}

	public class AuthComboBoxRenderer extends JLabel implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			ImageIcon icon = (ImageIcon) value;
			setIcon(icon);
			setText(icon.getDescription());

			return this;
		}

	}
}
