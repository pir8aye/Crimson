package com.subterranean_security.charcoal.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import com.subterranean_security.charcoal.ui.components.IPanel.State;
import com.subterranean_security.crimson.universal.Universal.Instance;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;

public class StatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private State state;
	private Instance instance;

	private Timer updater;

	private JLabel lbl_instance;

	private JPanel cards;

	public StatPanel(Instance instance) {
		this.instance = instance;
		init();
		initInstance();
	}

	private void init() {
		setLayout(new BorderLayout(0, 0));

		cards = new JPanel();
		add(cards);
		cards.setLayout(new CardLayout(0, 0));

		JPanel panel_1 = new JPanel();
		cards.add(panel_1, "UNCONNECTED");
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		cards.add(panel, "CONNECTED");

		JLabel lblMem = new JLabel("Mem:");
		panel.add(lblMem);

		JLabel lblCpu = new JLabel("Cpu:");
		panel.add(lblCpu);

	}

	private void initInstance() {
		lbl_instance = new JLabel(instance.toString());
		add(lbl_instance, BorderLayout.WEST);
		setInstanceColor(Color.GRAY);
	}

	private void setInstanceColor(Color color) {
		lbl_instance.setForeground(color);
		lbl_instance.setBorder(new LineBorder(color, 2, true));
	}

	public void startUpdater() {
		if (updater != null)
			updater.stop();

		updater = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		updater.setRepeats(true);
		updater.start();
	}

	public void setState(State state) {
		if (this.state == state)
			return;

		((CardLayout) cards.getLayout()).show(cards, state.toString());
		switch (state) {
		case CONNECTED:
			setInstanceColor(StatusLabel.good);
			break;
		case UNCONNECTED:
			setInstanceColor(Color.GRAY);
			updater.stop();
			break;
		default:
			break;

		}
	}

}
