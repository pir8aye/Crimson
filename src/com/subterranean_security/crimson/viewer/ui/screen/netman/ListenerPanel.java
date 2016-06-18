package com.subterranean_security.crimson.viewer.ui.screen.netman;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.UIStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.netman.listener.AddListener;

public class ListenerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ListenerTable lt = new ListenerTable(this);

	public JButton btnRemove;

	private EPanel ep;

	public ListenerPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel root = new JPanel();
		root.setLayout(new BorderLayout(0, 0));

		ep = new EPanel(root);
		add(ep, BorderLayout.CENTER);

		root.add(lt);

		JMenuBar menuBar = new JMenuBar();
		root.add(menuBar, BorderLayout.NORTH);

		JButton btnAdd = new JButton(UIUtil.getIcon("icons16/general/add_listener.png"));
		btnAdd.setMargin(new Insets(2, 2, 2, 2));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (UIStore.EAddListener == null) {
					UIStore.EAddListener = new AddListener(ep);
					ep.raise(UIStore.EAddListener, 200);
				}

			}
		});
		menuBar.add(btnAdd);

		btnRemove = new JButton(UIUtil.getIcon("icons16/general/remove_listener.png"));
		btnRemove.setEnabled(false);
		btnRemove.setMargin(new Insets(2, 2, 2, 2));
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						ViewerCommands.removeListener(lt.getSelected().getId());
					}
				}).start();
			}
		});
		menuBar.add(btnRemove);
	}

}
