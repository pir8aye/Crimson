package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Font;

public class KeyLogParagraph extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextPane txtpnTest;

	public KeyLogParagraph(String title) {
		setBackground(Color.WHITE);
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		txtpnTest = new JTextPane();
		txtpnTest.setFont(new Font("Dialog", Font.PLAIN, 11));
		txtpnTest.setEditable(false);
		txtpnTest.setContentType("");
		add(txtpnTest, BorderLayout.NORTH);
	}

	public void append(String event) {
		txtpnTest.setText(txtpnTest.getText() + event);

	}

}
