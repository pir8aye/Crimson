package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.viewer.ui.utility.UIStore;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class NetMan extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public ListenerPanel lp;

	public NetMan() {
		setIconImages(UUtil.getIconList());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(573, 364));
		setTitle("Network Manager");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		StatsPanel sp = new StatsPanel();
		sp.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(sp);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		lp = new ListenerPanel();
		tabbedPane.add(lp);
		tabbedPane.setTitleAt(0, "Listeners");

		AuthPanel ap = new AuthPanel();
		tabbedPane.add(ap);
		tabbedPane.setTitleAt(1, "Authentication");
	}

	@Override
	public void dispose() {
		super.dispose();
		UIStore.netMan = null;
	}

}
