package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.logs;

import javax.swing.JPanel;

import com.subterranean_security.crimson.sv.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.panel.Console;
import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class Logs extends JPanel implements CPPanel {

	private static final long serialVersionUID = 1L;

	public Logs(ClientProfile profile, Console console) {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
	}

}
