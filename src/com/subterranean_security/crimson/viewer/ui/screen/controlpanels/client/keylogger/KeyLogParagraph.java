package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.keylogger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

public class KeyLogParagraph extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextPane txtpnTest;

	private String title;

	public KeyLogParagraph(String title) {
		this.title = title;
		setBackground(Color.WHITE);
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		txtpnTest = new JTextPane();
		txtpnTest.setFont(new Font("Dialog", Font.PLAIN, 11));
		txtpnTest.setEditable(false);
		txtpnTest.setContentType("");
		add(txtpnTest, BorderLayout.NORTH);
	}

	public String getTitle() {
		return title;
	}

	public void append(String event) {
		txtpnTest.setText(txtpnTest.getText() + event);

	}

}
