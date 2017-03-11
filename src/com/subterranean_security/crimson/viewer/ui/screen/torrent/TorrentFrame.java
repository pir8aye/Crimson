package com.subterranean_security.crimson.viewer.ui.screen.torrent;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TorrentFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public TorrentFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		TorrentPanel tp = new TorrentPanel();
		contentPane.add(tp, BorderLayout.CENTER);
	}

}
