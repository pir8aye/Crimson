package com.subterranean_security.charcoal.ui.layouts;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class TriLayout extends JPanel {

	private static final long serialVersionUID = 1L;

	public TriLayout(JPanel panel_left, JPanel panel_mid, JPanel panel_right) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(panel_left);
		add(panel_mid);
		add(panel_right);

	}

	public void maximize() {

	}

	public void minimize() {

	}

}
