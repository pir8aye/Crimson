package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.exception.InvalidObjectException;
import com.subterranean_security.crimson.core.proto.FileManager.RS_AdvancedFileInfo;
import com.subterranean_security.crimson.core.util.B64;
import com.subterranean_security.crimson.core.util.CUtil;
import com.subterranean_security.crimson.core.util.ObjectTransfer;
import com.subterranean_security.crimson.viewer.ui.common.components.DataViewer;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import javax.swing.SwingConstants;

public class AdvancedFileInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	public AdvancedFileInfo(RS_AdvancedFileInfo rs, EPanel ep) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		DataViewer dv = new DataViewer();
		dv.addRow(new String[] { "Path", rs.hasPath() ? rs.getPath() : "" });
		dv.addRow(new String[] { "Size",
				CUtil.Misc.familiarize(rs.getSize(), CUtil.Misc.BYTES) + " (" + rs.getSize() + " bytes)" });
		dv.addRow(new String[] { "Last Modification", new Date(rs.getMtime()).toString() });
		panel.add(dv, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		panel_1.add(Box.createHorizontalStrut(5), BorderLayout.EAST);
		panel_1.add(Box.createVerticalStrut(5), BorderLayout.NORTH);

		JLabel lblIcon = new JLabel();
		lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblIcon);
		try {
			lblIcon.setIcon((Icon) ObjectTransfer.Default.deserialize(B64.decode(rs.getLocalIcon())));
		} catch (InvalidObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblIcon.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Closing info");
				ep.drop();
			}
		});
		btnClose.setMargin(new Insets(2, 4, 2, 4));
		btnClose.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_2.add(btnClose);
	}

}
