/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.viewer.ui.screen.login;

import static com.subterranean_security.crimson.universal.Flags.DEV_MODE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.MessageFlowException;
import com.subterranean_security.crimson.core.net.MessageFuture.Timeout;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.proto.Login.RQ_ServerInfo;
import com.subterranean_security.crimson.core.proto.Login.RS_ServerInfo;
import com.subterranean_security.crimson.core.proto.MSG.Message;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.core.ui.FieldLimiter;
import com.subterranean_security.crimson.core.util.ValidationUtil;
import com.subterranean_security.crimson.universal.stores.PrefStore;
import com.subterranean_security.crimson.universal.stores.PrefStore.PTag;
import com.subterranean_security.crimson.viewer.net.ViewerExecutor;
import com.subterranean_security.crimson.viewer.ui.common.components.labels.StatusLabel;

public class ServerSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ServerSelectionPanel.class);

	// panels
	private JPanel panel_server;

	// server address
	private JLabel lbl_address;
	private JComboBox<String> fld_address;

	// server port
	private JTextField fld_port;
	private JLabel lbl_port;

	// status label
	private StatusLabel lbl_status;

	// gathered from server
	private Connector connection;
	private RS_ServerInfo serverInfo;

	//
	private String server;
	private int port;

	// parent dialog
	private LoginDialog parent;

	//
	private boolean mouseOverStatus = true;

	public static final String LOCAL_SERVER = "Local Instance";
	public static final String EXAMPLE_SERVER = "Example Server";

	public ServerSelectionPanel(LoginDialog parent, boolean localServer) {
		this.parent = parent;
		init();
		initButtons();
		initRecents(localServer, true);
		if (DEV_MODE)
			initPreFill();
	}

	private void init() {
		setLayout(null);

		lbl_status = new StatusLabel("Enter server details");
		lbl_status.setVisible(true);
		lbl_status.setBounds(12, 125, 376, 15);
		add(lbl_status);

		panel_server = new JPanel();
		panel_server.setBounds(6, 6, 388, 60);
		add(panel_server);
		panel_server.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Server", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		panel_server.setLayout(null);

		lbl_address = new JLabel("Address");
		lbl_address.setBounds(20, 12, 280, 15);
		lbl_address.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_server.add(lbl_address);

		fld_address = new JComboBox<String>();
		fld_address.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_address.setBounds(20, 28, 280, 21);
		fld_address.setEditable(true);
		fld_address.setRenderer(new AddressFieldRenderer(fld_address));
		fld_address.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fld_address.getSelectedIndex() == -1) {
					return;
				}

				String[] parts = ((String) fld_address.getSelectedItem()).split(":");

				String server = null;
				String port = null;
				switch (parts[0]) {
				case LOCAL_SERVER: {
					server = "127.0.0.1";
					port = "10101";
					break;
				}
				case EXAMPLE_SERVER: {
					server = "example.subterranean-security.com";
					port = "10101";
					break;
				}
				default: {
					if (parts.length == 2) {
						server = parts[0];
						port = parts[1];
					} else {
						return;
					}
					break;
				}
				}

				// insert info
				fld_address.setSelectedItem(server);

				// insert port
				try {
					fld_port.getDocument().remove(0, fld_port.getDocument().getLength());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// TODO find better way to add text to field
				for (char c : port.toCharArray()) {
					fld_port.dispatchEvent(new KeyEvent(fld_port, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
							KeyEvent.KEY_FIRST, KeyEvent.VK_UNDEFINED, c));

				}

			}
		});
		fld_address.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setInfo("enter an ip address, hostname, or DNS name");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setDefault();
			}
		});
		panel_server.add(fld_address);

		lbl_port = new JLabel("Port");
		lbl_port.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_port.setBounds(312, 12, 56, 15);
		lbl_port.setFont(new Font("Dialog", Font.BOLD, 10));
		panel_server.add(lbl_port);

		fld_port = new JTextField();
		fld_port.setFont(new Font("Dialog", Font.PLAIN, 10));
		fld_port.setColumns(10);
		fld_port.setDocument(new FieldLimiter(5, "\\d"));
		fld_port.setBounds(312, 28, 56, 21);
		fld_port.setHorizontalAlignment(SwingConstants.CENTER);
		fld_port.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setInfo("corresponding port on the server");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (mouseOverStatus)
					lbl_status.setDefault();
			}
		});
		panel_server.add(fld_port);

	}

	/**
	 * Initialize visible menu buttons
	 */
	public void initButtons() {
		parent.resetButtons();

		parent.getBack().setText("Exit");
		parent.getBack().addActionListener(e -> System.exit(0));

		parent.getNext().setText("Connect");
		parent.getNext().addActionListener(e -> {
			if (!testValues()) {
				return;
			}

			disableControls();
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {

					if (makeConnection()) {
						lbl_status.setGood("Connection Established");

						// pass information to next panel
						parent.getUserSelectionPanel().setServerInfo(connection, serverInfo);

						// save the address in recents
						addRecent(server + ":" + port);

						Thread.sleep(600);

						// show the next panel
						parent.showUserSelectionPanel();
						parent.getUserSelectionPanel().initButtons();
					}

					return null;
				}

				protected void done() {
					try {
						get();
					} catch (Exception e) {
						if (e.getCause() instanceof UnknownHostException) {
							lbl_status.setBad("Unknown Host");
						} else {
							lbl_status.setBad("Unknown Error");
							e.printStackTrace();
						}

					}

					enableControls();
				};
			}.execute();

		});
	}

	/**
	 * Pre-fill fields to speed up testing
	 */
	private void initPreFill() {
		String address = System.getProperty("debug.prefill.address", "");
		fld_address.getEditor().setItem(address);
		fld_address.setSelectedItem(address);

		// TODO find better way
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(500);
				return null;
			}

			protected void done() {
				for (char c : System.getProperty("debug.prefill.port", "").toCharArray()) {
					fld_port.dispatchEvent(new KeyEvent(fld_port, KeyEvent.KEY_TYPED, System.currentTimeMillis(),
							KeyEvent.KEY_FIRST, KeyEvent.VK_UNDEFINED, c));

				}
			};
		}.execute();
	}

	/**
	 * Initialize recent menu
	 * 
	 * @param localServer
	 *            If the local instance should be included
	 * @param exampleServer
	 *            If the example server should be included
	 */
	private void initRecents(boolean localServer, boolean exampleServer) {
		List<String> recent = new ArrayList<String>();

		if (localServer) {
			recent.add(LOCAL_SERVER);
		}

		if (exampleServer) {
			recent.add(EXAMPLE_SERVER);
		}

		for (String saved : PrefStore.getPref().getString(PTag.LOGIN_RECENT).split(",")) {
			recent.add(saved);
		}

		String[] r = new String[recent.size()];
		for (int i = 0; i < recent.size(); i++) {
			r[i] = recent.get(i);
		}

		fld_address.setModel(new DefaultComboBoxModel<String>(r));
		fld_address.setSelectedIndex(-1);
	}

	/**
	 * Save a nonspecial entry to the recent menu
	 * 
	 * @param entry
	 */
	private void addRecent(String entry) {
		if (entry.equals("127.0.0.1") || entry.equals("example.subterranean-security.com"))
			return;

		StringBuffer buffer = new StringBuffer();
		buffer.append(entry);

		for (String saved : PrefStore.getPref().getString(PTag.LOGIN_RECENT).split(",")) {
			if (!saved.equals(entry)) {
				buffer.append(',');
				buffer.append(saved);
			}
		}
		PrefStore.getPref().putString(PTag.LOGIN_RECENT, buffer.toString());

	}

	/**
	 * Make a connection and download ServerInfo from the current server
	 * 
	 * @return True if the connection suceeded and the ServerInfo was downloaded
	 */
	private boolean makeConnection() {
		lbl_status.setOngoing("Contacting server");
		connection = ConnectionStore.makeConnection(new ExecutorFactory(ViewerExecutor.class), server, port, false);
		if (connection == null) {
			lbl_status.setBad("Connection failed");
			return false;
		}

		try {
			Message rs = connection
					.writeAndGetResponse(Message.newBuilder().setRqServerInfo(RQ_ServerInfo.newBuilder()).build())
					.get(2000);

			if (!rs.hasRsServerInfo()) {
				throw new MessageFlowException(RQ_ServerInfo.class, rs);
			}
			serverInfo = rs.getRsServerInfo();
		} catch (InterruptedException | Timeout e) {
			connection.close();
			lbl_status.setBad("Server error");
			return false;
		}

		if (serverInfo.getMaintainance()) {
			lbl_status.setWarn("*Server is in maintainance mode*");
			return false;
		}

		return true;
	}

	private void setServerError() {
		fld_address.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid server address");
	}

	private void setPortError() {
		fld_port.setBorder(new LineBorder(Color.RED));
		lbl_status.setBad("Invalid server port");
	}

	private void removeErrorBorders() {
		fld_address.setBorder(new JComboBox<Object>().getBorder());
		fld_port.setBorder(new JTextField().getBorder());
	}

	/**
	 * Validate current server parameters
	 * 
	 * @return True if the currently entered DNS/IP and port passed validation
	 */
	private boolean testValues() {
		removeErrorBorders();
		if (!ValidationUtil.port(fld_port.getText())) {
			setPortError();
			return false;
		}
		if (!ValidationUtil.dns((String) fld_address.getSelectedItem())
				&& !ValidationUtil.ipv4((String) fld_address.getSelectedItem())) {
			setServerError();
			return false;
		}

		server = (String) fld_address.getSelectedItem();
		port = Integer.parseInt(fld_port.getText());
		return true;
	}

	private void disableControls() {
		mouseOverStatus = false;

		parent.getBack().setEnabled(false);
		parent.getNext().setEnabled(false);
		fld_port.setEnabled(false);
		fld_address.setEnabled(false);
		lbl_address.setEnabled(false);
		lbl_port.setEnabled(false);

	}

	private void enableControls() {
		mouseOverStatus = true;

		parent.getBack().setEnabled(true);
		parent.getNext().setEnabled(true);
		fld_port.setEnabled(true);
		fld_address.setEnabled(true);
		lbl_address.setEnabled(true);
		lbl_port.setEnabled(true);

	}

	public void reset() {
		lbl_status.setDefault();
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

}

/**
 * Custom renderer which makes certain entries in the address field special
 * colors
 */
class AddressFieldRenderer extends JPanel implements ListCellRenderer<String> {

	private static final long serialVersionUID = -1L;

	private JPanel textPanel;
	private JLabel text;
	private Color defaultForeground;
	private Color defaultBackground;

	public AddressFieldRenderer(JComboBox<String> combo) {
		textPanel = new JPanel();
		textPanel.add(this);
		text = new JLabel();
		text.setOpaque(true);
		text.setFont(combo.getFont());
		defaultForeground = text.getForeground();
		defaultBackground = text.getBackground();
		textPanel.add(text);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(defaultBackground);
		}

		text.setBackground(getBackground());

		text.setText(value);
		if (index > -1) {
			switch (text.getText()) {
			case ServerSelectionPanel.LOCAL_SERVER: {
				text.setForeground(StatusLabel.good);
				break;
			}
			case ServerSelectionPanel.EXAMPLE_SERVER: {
				text.setForeground(Color.PINK);
				break;
			}
			default: {
				text.setForeground(defaultForeground);
				break;
			}
			}
		}

		return text;
	}
}
