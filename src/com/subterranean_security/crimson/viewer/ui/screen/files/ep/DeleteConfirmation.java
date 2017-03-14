/******************************************************************************
 *                                                                            *
 *                    Copyright 2016 Subterranean Security                    *
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
package com.subterranean_security.crimson.viewer.ui.screen.files.ep;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import com.subterranean_security.crimson.core.platform.LocalFS;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;
import com.subterranean_security.crimson.viewer.net.ViewerCommands;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;
import com.subterranean_security.crimson.viewer.ui.screen.files.Pane;
import com.subterranean_security.crimson.viewer.ui.screen.files.Pane.TYPE;

public class DeleteConfirmation extends JPanel {

	private static final long serialVersionUID = 1L;

	private Pane parent;
	private EPanel ep;
	private int cid;
	private ArrayList<String> targets;
	private TYPE type;

	private JCheckBox chckbxOverwrite;

	private JLabel lblDelete;

	private JButton btnDelete;

	private JButton btnCancel;

	public DeleteConfirmation(Pane parent, int cid, ArrayList<String> targets, TYPE type) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.parent = parent;
		this.ep = parent.parent.ep;
		this.type = type;
		this.cid = cid;
		this.targets = targets;
		init();
	}

	public void init() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ep.drop();
			}
		});
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnCancel);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chckbxOverwrite.setEnabled(false);
				lblDelete.setEnabled(false);
				btnDelete.setEnabled(false);
				btnCancel.setEnabled(false);

				new SwingWorker<Outcome, Void>() {

					@Override
					protected Outcome doInBackground() throws Exception {
						switch (type) {
						case CLIENT:
						case SERVER:
							return ViewerCommands.fm_delete(cid, targets, chckbxOverwrite.isSelected());
						case VIEWER:
							return LocalFS.delete(targets, chckbxOverwrite.isSelected());
						default:
							return null;

						}

					}

					protected void done() {
						try {
							Outcome outcome = get();
							if (!outcome.getResult()) {
								ep.raise(new Error(ep, outcome.getComment()), 65);
							} else {
								ep.drop();
								parent.refresh();
							}

						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					};
				}.execute();
			}
		});
		btnDelete.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnDelete);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);

		chckbxOverwrite = new JCheckBox("Overwrite with random data");
		panel_3.add(chckbxOverwrite);
		chckbxOverwrite.setFont(new Font("Dialog", Font.BOLD, 10));

		lblDelete = new JLabel("Delete " + targets.size() + " items?");
		panel_2.add(lblDelete, BorderLayout.NORTH);
		lblDelete.setHorizontalAlignment(SwingConstants.CENTER);

	}

}
