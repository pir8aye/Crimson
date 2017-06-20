package com.subterranean_security.charcoal.ui.components.details;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public abstract class TableDetail extends JPanel implements Detail {
	private static final long serialVersionUID = 1L;
	protected JTable table;

	public TableDetail() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);
	}

}
