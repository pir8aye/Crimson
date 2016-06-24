package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.logs;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.subterranean_security.crimson.core.proto.Log.LogType;

public class LogPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private LogType type;

	public LogPane(LogType name, String log) {
		this.type = name;
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		textArea.setText(log);

	}

	public void setLog(String log) {
		textArea.setText(log);
	}

	public LogType getLogType() {
		return type;
	}

}
