package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.utility.UIStore;

public class ListenerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ListenerTable lt = new ListenerTable(this);

	public JButton btnRemove;

	public ListenerPanel() {
		setLayout(new BorderLayout(0, 0));
		add(lt);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(panel_3, BorderLayout.NORTH);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (UIStore.addDialog == null) {
					UIStore.addDialog = new AddDialog();
					UIStore.addDialog.setLocationRelativeTo(null);
					UIStore.addDialog.setVisible(true);
				} else {
					UIStore.addDialog.toFront();
				}

			}
		});
		btnNew.setFont(new Font("Dialog", Font.BOLD, 11));
		btnNew.setMargin(new Insets(0, 4, 0, 4));
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 0;
		panel_3.add(btnNew, gbc_btnNewButton_1);

		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						ViewerCommands.removeListener(new StringBuffer(), lt.getSelected().getId());
					}
				}).start();
			}
		});
		btnRemove.setEnabled(false);
		btnRemove.setFont(new Font("Dialog", Font.BOLD, 11));
		btnRemove.setMargin(new Insets(0, 4, 0, 4));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 0;
		panel_3.add(btnRemove, gbc_btnNewButton);
	}

}
