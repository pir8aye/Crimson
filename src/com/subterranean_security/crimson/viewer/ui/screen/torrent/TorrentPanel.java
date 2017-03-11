package com.subterranean_security.crimson.viewer.ui.screen.torrent;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JMenuBar;

public class TorrentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public TorrentPanel() {
		setLayout(new BorderLayout());
		TorrentList list = new TorrentList();
		add(list, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
	}

}
