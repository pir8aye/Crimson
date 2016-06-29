package com.subterranean_security.crimson.viewer.ui.screen.generator;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class AuthComboBoxRenderer extends JLabel implements ListCellRenderer {

	public AuthComboBoxRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		ImageIcon icon = (ImageIcon) value;
		setIcon(icon);
		setText(icon.getDescription());

		return this;
	}

}
