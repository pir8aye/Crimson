package com.subterranean_security.crimson.viewer.ui.screen.generator.ep;

import java.awt.Font;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.util.CUtil;

import javax.swing.SwingConstants;

public class ViewNetworktarget extends JPanel {

	private static final long serialVersionUID = 1L;

	private String server;
	private int port;

	private JLabel lblVisibility;
	private JLabel lblPing_1;

	private JLabel lblServerAddress;

	public ViewNetworktarget(String server, int port) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.server = server;
		this.port = port;
		init();
		loadIP();
	}

	public void init() {
		setLayout(null);

		JLabel lblServer = new JLabel("Server:");
		lblServer.setHorizontalAlignment(SwingConstants.TRAILING);
		lblServer.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServer.setBounds(12, 11, 100, 15);
		add(lblServer);

		lblServerAddress = new JLabel(server);
		lblServerAddress.setFont(new Font("Dialog", Font.BOLD, 10));
		lblServerAddress.setBounds(138, 11, 222, 15);
		add(lblServerAddress);

		JLabel lblPing = new JLabel("Ping:");
		lblPing.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPing.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPing.setBounds(12, 28, 100, 15);
		add(lblPing);

		JLabel lblTargetVisibility = new JLabel("Target visibility:");
		lblTargetVisibility.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTargetVisibility.setFont(new Font("Dialog", Font.BOLD, 10));
		lblTargetVisibility.setBounds(12, 45, 100, 15);
		add(lblTargetVisibility);

		lblPing_1 = new JLabel("loading...");
		lblPing_1.setFont(new Font("Dialog", Font.BOLD, 10));
		lblPing_1.setBounds(138, 28, 222, 15);
		add(lblPing_1);

		lblVisibility = new JLabel("loading...");
		lblVisibility.setFont(new Font("Dialog", Font.BOLD, 10));
		lblVisibility.setBounds(138, 45, 222, 15);
		add(lblVisibility);

	}

	private void loadIP() {

		// resolve address
		new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				return InetAddress.getByName(server).getHostAddress();
			}

			protected void done() {
				try {
					String ip = get();
					lblServerAddress.setText(lblServerAddress.getText() + " (" + ip + ")");
					loadPing();
				} catch (InterruptedException | ExecutionException e) {
					lblServerAddress.setText(lblServerAddress.getText() + " (failed to resolve)");
				}
			};
		}.execute();

	}

	private void loadPing() {

		// ping server
		new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				double ping = CUtil.Network.ping(server);
				if (ping == 0) {
					return "failed";
				} else {
					return "" + ping + "ms";
				}
			}

			protected void done() {
				try {
					String ping = get();
					lblPing_1.setText(ping);
				} catch (InterruptedException | ExecutionException e) {
					lblPing_1.setText("(failed to ping)");
				}
			};
		}.execute();

	}

	private void loadVisibility() {

		// ping server
		new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				return null;
			}

			protected void done() {
				try {
					String status = get();
					lblVisibility.setText(status);
				} catch (InterruptedException | ExecutionException e) {
					lblVisibility.setText("(failed to query visibility)");
				}
			};
		}.execute();

	}

}
