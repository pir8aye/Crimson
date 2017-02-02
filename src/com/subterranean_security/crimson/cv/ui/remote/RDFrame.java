package com.subterranean_security.crimson.cv.ui.remote;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.core.profile.SimpleAttribute;
import com.subterranean_security.crimson.viewer.store.ProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class RDFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RDPanel rdp;

	public RDFrame(RDPanel.Type type, int cvid) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(450, 300));
		setSize(new Dimension(450, 300));
		setIconImages(UIUtil.getIconList());
		setTitle("Remote Desktop (" + ProfileStore.getClient(cvid).getAttr(SimpleAttribute.USER_NAME) + "@"
				+ ProfileStore.getClient(cvid).getAttr(SimpleAttribute.NET_EXTERNALIP) + ")");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		rdp = new RDPanel(type, cvid, true);
		contentPane.add(rdp, BorderLayout.CENTER);
	}

	@Override
	public void dispose() {
		if (rdp.isRunning()) {
			rdp.stop();
		}
		super.dispose();
	}

}
