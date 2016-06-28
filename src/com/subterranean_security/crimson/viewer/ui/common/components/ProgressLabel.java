package com.subterranean_security.crimson.viewer.ui.common.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressLabel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private JLabel label;

	public ProgressLabel() {
		init();
	}

	public ProgressLabel(String s) {
		init();
		setText(s);
	}

	public void init() {

		setLayout(new BorderLayout(0, 0));

		label = new JLabel();
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		add(label, BorderLayout.CENTER);

		progressBar = ProgressBarFactory.get();
		progressBar.setPreferredSize(new Dimension(148, 4));
		progressBar.setVisible(false);
		add(progressBar, BorderLayout.SOUTH);

	}

	public void setText(String text) {
		label.setText(text);
	}

	public void startLoading() {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void stopLoading() {
		progressBar.setVisible(false);
		progressBar.setIndeterminate(false);
	}

}
