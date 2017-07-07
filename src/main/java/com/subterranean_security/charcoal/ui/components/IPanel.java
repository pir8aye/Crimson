package com.subterranean_security.charcoal.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class IPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public enum State {
		UNCONNECTED, CONNECTED;
	}

	private ColorPane console;
	private JButton btnKill;
	private JButton btnDetach;

	private Instance instance;
	private StatPanel stat_panel;
	private DetailPanel detail_panel;

	public IPanel() {
		init();
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		console = new ColorPane();
		console.setFont(new Font("Dialog", Font.PLAIN, 10));
		// console.setContentType("text/html");
		console.setBackground(Color.DARK_GRAY);

		JScrollPane jsp = new JScrollPane(console);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(jsp, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar, BorderLayout.NORTH);

		btnKill = new JButton("");
		btnKill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		btnKill.setToolTipText("Kill process");
		btnKill.setEnabled(false);
		btnKill.setMargin(new Insets(2, 4, 2, 4));
		btnKill.setIcon(UIUtil.getIcon("icons16/general/delete.png"));
		menuBar.add(btnKill);

		btnDetach = new JButton("");
		btnDetach.setEnabled(false);
		btnDetach.setMargin(new Insets(2, 4, 2, 4));
		btnDetach.setIcon(UIUtil.getIcon("icons16/general/door.png"));
		menuBar.add(btnDetach);

	}

	private void initStatPanel() {
		stat_panel = new StatPanel(instance);
		add(stat_panel, BorderLayout.NORTH);
	}

	private void initDetailPanel() {
		detail_panel = new DetailPanel();
		add(detail_panel, BorderLayout.SOUTH);
	}

	public IPanel(Instance instance) {
		init();
		listen(instance);
		initStatPanel();
		initDetailPanel();
	}

	public void listen(Instance instance) {
		this.instance = instance;
	}

	public void addLine(String line) {
		console.appendANSI(line);
	}

	public Instance getInstance() {
		return instance;
	}

	public void setState(State state) {
		stat_panel.setState(state);
		setActive(state == State.CONNECTED);
	}

	public void setActive(boolean active) {
		btnKill.setEnabled(active);
		btnDetach.setEnabled(active);
	}

}
