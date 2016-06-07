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
package com.subterranean_security.crimson.viewer.ui.screen.eula;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.common.components.StatusLights;

public class EULADialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean exitOnDisp;
	public boolean accepted = false;

	public Date start;

	// * COMPONENTS *//
	private JButton okButton;
	private StatusLights statusLights;
	private StatusLights statusLights_1;
	private StatusLights statusLights_2;

	private Color goodColor = new Color(0, 149, 39);
	private Color badColor = new Color(200, 0, 0);

	@Override
	public void dispose() {
		if (exitOnDisp) {
			if (accepted) {
				statusLights.setLight(goodColor, 0);
				statusLights_1.setLight(goodColor, 0);
				statusLights_2.setLight(goodColor, 0);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}
				super.dispose();

				synchronized (this) {
					this.notifyAll();
				}
			} else {
				statusLights.setLight(badColor, 0);
				statusLights_1.setLight(badColor, 0);
				statusLights_2.setLight(badColor, 0);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}
				System.exit(0);
			}

		} else {
			super.dispose();
		}
	}

	public EULADialog(boolean exitOnDispose) {
		exitOnDisp = exitOnDispose;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setAlwaysOnTop(true);
		// setIconImage(UICommon.appIcon);

		setResizable(false);

		setTitle("Crimson License Agreement");

		setBounds(100, 100, 639, 310);
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getViewport().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int extent = scrollPane.getVerticalScrollBar().getModel().getExtent();
				if (scrollPane.getVerticalScrollBar().getValue() + extent == scrollPane.getVerticalScrollBar()
						.getMaximum()) {

					okButton.setEnabled(true);
					startAnimation();
				}

			}

		});
		contentPanel.setLayout(new BorderLayout(0, 0));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane);

		// load the license from file

		scrollPane.setViewportView(new JLabel(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(EULADialog.class.getResource(UICommon.getLicensePath("en"))))));
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.PAGE_END);
			{
				buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
			}
			{
				JButton cancelButton = new JButton("Decline");
				cancelButton.setMargin(new Insets(2, 10, 2, 10));
				cancelButton.setPreferredSize(new Dimension(100, 25));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						stopAnimation();
						new Thread(new Runnable() {
							public void run() {
								dispose();
								System.exit(0);
							}
						}).start();

					}
				});
				cancelButton.setMnemonic('D');
				buttonPane.add(cancelButton);
			}

			statusLights = new StatusLights();
			statusLights.setPreferredSize(new Dimension(9, 25));

			JSeparator separator = new JSeparator();
			separator.setPreferredSize(new Dimension(10, 0));
			buttonPane.add(separator);
			buttonPane.add(statusLights);

			statusLights_1 = new StatusLights();
			statusLights_1.setPreferredSize(new Dimension(9, 25));

			buttonPane.add(statusLights_1);

			statusLights_2 = new StatusLights();
			statusLights_2.setPreferredSize(new Dimension(9, 25));

			buttonPane.add(statusLights_2);
			okButton = new JButton("Accept");
			okButton.setEnabled(false);
			okButton.setMnemonic('A');
			okButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!okButton.isEnabled()) {
						return;
					}
					stopAnimation();
					// accepted
					accepted = true;
					new Thread(new Runnable() {
						public void run() {
							dispose();
						}
					}).start();

				}
			});

			JSeparator separator_1 = new JSeparator();
			separator_1.setPreferredSize(new Dimension(10, 0));
			buttonPane.add(separator_1);
			okButton.setPreferredSize(new Dimension(100, 26));

			buttonPane.add(okButton);

			statusLights.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (statusLights_2.isAnimating()) {
						statusLights_2.stopAnimation();
						statusLights_1.stopAnimation();
						statusLights.stopAnimation();
					} else {
						statusLights_2.animate("random2");
						statusLights_1.animate("random2");
						statusLights.animate("random2");
					}

				}
			});

			statusLights_1.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (statusLights_2.isAnimating()) {
						statusLights_2.stopAnimation();
						statusLights_1.stopAnimation();
						statusLights.stopAnimation();
					} else {
						statusLights_2.animate("random2");
						statusLights_1.animate("random2");
						statusLights.animate("random2");
					}
				}
			});

			statusLights_2.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (statusLights_2.isAnimating()) {
						statusLights_2.stopAnimation();
						statusLights_1.stopAnimation();
						statusLights.stopAnimation();
					} else {
						statusLights_2.animate("random2");
						statusLights_1.animate("random2");
						statusLights.animate("random2");
					}
				}
			});
		}
		start = new Date();
	}

	private void startAnimation() {
		statusLights_2.animate("random2");
		statusLights_1.animate("random2");
		statusLights.animate("random2");
	}

	private void stopAnimation() {
		statusLights_2.stopAnimation();
		statusLights_1.stopAnimation();
		statusLights.stopAnimation();
	}
}
