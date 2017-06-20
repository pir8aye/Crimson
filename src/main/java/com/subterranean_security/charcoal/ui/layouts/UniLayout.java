package com.subterranean_security.charcoal.ui.layouts;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class UniLayout extends JPanel {

	private static final long serialVersionUID = 1L;

	public UniLayout(JPanel pane) {
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
	}

}
