package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StatsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public StatsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		add(panel);
				panel.setLayout(new GridLayout(0, 2, 0, 0));
		
				JLabel lblListeners = new JLabel("Listeners:");
				panel.add(lblListeners);
						
								JLabel label = new JLabel("0");
								label.setHorizontalAlignment(SwingConstants.TRAILING);
								panel.add(label);
				
						JLabel lblInternalIp = new JLabel("Internal IP:");
						panel.add(lblInternalIp);
				
						JLabel label_1 = new JLabel("0.0.0.0");
						label_1.setHorizontalAlignment(SwingConstants.TRAILING);
						panel.add(label_1);
		
				JLabel lblExternalIp = new JLabel("External IP:");
				panel.add(lblExternalIp);
		
				JLabel label_2 = new JLabel("0.0.0.0");
				label_2.setHorizontalAlignment(SwingConstants.TRAILING);
				panel.add(label_2);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));

	}

}
