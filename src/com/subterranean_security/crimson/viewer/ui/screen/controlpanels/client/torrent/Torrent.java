package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.torrent;

import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;

public class Torrent extends JPanel implements CPPanel {
	private static final long serialVersionUID = 1L;

	public Torrent() {
		setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
	}

	@Override
	public void clientOffline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverOffline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabClosed() {
		// TODO Auto-generated method stub

	}

}
