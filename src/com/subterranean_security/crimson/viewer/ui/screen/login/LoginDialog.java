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
package com.subterranean_security.crimson.viewer.ui.screen.login;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.viewer.ViewerState;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.FadeLabel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.cpanel.CPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HPanel;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.HiddenMenu;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.hpanel.NormalMenu;
import com.subterranean_security.crimson.viewer.ui.screen.main.MainFrame;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// special container panels
	private HPanel hp;
	private CPanel cp;

	// visible panels
	private ServerSelectionPanel serverSelectionPanel;
	private UserSelectionPanel userSelectionPanel;

	// menu buttons
	private JButton back;
	private JButton next;

	// upper image panel
	private FadeLabel logo;

	public LoginDialog(boolean localServer) {
		initButtons();
		init(localServer);
	}

	public void init(boolean localServer) {
		setTitle("Crimson - Login");
		setSize(UICommon.dim_login);
		setPreferredSize(UICommon.dim_login);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		serverSelectionPanel = new ServerSelectionPanel(this, localServer);
		userSelectionPanel = new UserSelectionPanel(this);

		cp = new CPanel(3f, serverSelectionPanel, userSelectionPanel);

		JPanel panel_header = new JPanel();
		panel_header.setBackground(UICommon.bg);
		panel_header.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel_header.setLayout(null);

		logo = new FadeLabel(UIUtil.getIcon("crimson_logo-login.png"), 0, 0.05f, 50);
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setBounds(0, 0, 405, 150);
		panel_header.add(logo);

		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(panel_header);
		container.add(cp);

		hp = new HPanel(container);
		hp.init(initVisibleMenu(), initHiddenMenu());
		hp.setHMenuHeight(76);
		getContentPane().add(hp);

	}

	private void initButtons() {
		back = new JButton();
		back.setMargin(new Insets(0, 3, 0, 3));
		back.setFont(new Font("Dialog", Font.BOLD, 10));
		back.setPreferredSize(new Dimension(64, 25));

		next = new JButton();
		next.setMargin(new Insets(0, 1, 0, 1));
		next.setFont(new Font("Dialog", Font.BOLD, 10));
		next.setPreferredSize(new Dimension(64, 25));
	}

	private NormalMenu initVisibleMenu() {

		NormalMenu nmenu = new NormalMenu();
		nmenu.setButtons(back, Box.createHorizontalGlue(), hp.getUpBtn(), Box.createHorizontalGlue(), next);
		return nmenu;
	}

	private HiddenMenu initHiddenMenu() {
		JButton login = new JButton("Offline Login");
		login.setToolTipText("Continue without logging into a server");
		login.setFont(new Font("Dialog", Font.BOLD, 9));
		login.setMargin(new Insets(0, 3, 0, 3));
		login.setPreferredSize(new Dimension(85, 20));
		login.addActionListener(e -> {
			userSelectionPanel.result = true;
			ViewerState.online = false;
			MainFrame.main = new MainFrame();

			synchronized (this) {
				this.notifyAll();
			}
			dispose();
		});

		JButton help = new JButton("Help");
		help.setToolTipText("Show interface help");
		help.setFont(new Font("Dialog", Font.BOLD, 9));
		help.setMargin(new Insets(0, 3, 0, 3));
		help.setPreferredSize(new Dimension(55, 20));
		help.addActionListener(e -> {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.subterranean_security.com"));
				} catch (URISyntaxException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JButton website = new JButton("Website");
		website.setToolTipText("Open the website");
		website.setFont(new Font("Dialog", Font.BOLD, 9));
		website.setMargin(new Insets(0, 3, 0, 3));
		website.setPreferredSize(new Dimension(64, 20));
		website.addActionListener(e -> {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.subterranean_security.com"));
				} catch (URISyntaxException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		return new HiddenMenu(true, login, help, website);
	}

	public void showUserSelectionPanel() {
		// TODO drop hpanel if raised
		cp.moveRight();
	}

	public void showServerSelectionPanel() {
		// TODO drop hpanel if raised
		cp.moveLeft();
	}

	public JButton getBack() {
		return back;
	}

	public JButton getNext() {
		return next;
	}

	public void resetButtons() {
		for (ActionListener a : back.getActionListeners())
			back.removeActionListener(a);
		for (ActionListener a : next.getActionListeners())
			next.removeActionListener(a);
	}

	@Override
	public void dispose() {
		if (userSelectionPanel.result) {
			hp.hmenu.nowClosed();
			super.dispose();
		} else {
			System.exit(0);
		}
	}

	public UserSelectionPanel getUserSelectionPanel() {
		return userSelectionPanel;
	}

	public ServerSelectionPanel getServerSelectionPanel() {
		return serverSelectionPanel;
	}

	public FadeLabel getLogo() {
		return logo;
	}

	public void resetLogo() {
		logo.fadeImage(UIUtil.getIcon("crimson_logo-login.png"));
	}

}
