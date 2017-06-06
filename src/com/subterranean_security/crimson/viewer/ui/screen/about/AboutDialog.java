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
package com.subterranean_security.crimson.viewer.ui.screen.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.subterranean_security.crimson.core.util.DateUtil;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.stores.DatabaseStore;
import com.subterranean_security.crimson.universal.util.JarUtil;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private String buildNo;
	private String buildTime;

	private Timer timer = new Timer();
	private JLabel lbl_uptime;

	class Uptimer extends TimerTask {

		@Override
		public void run() {
			lbl_uptime.setText(DateUtil.timeBetween(Universal.start, new Date()));

		}

	}

	public AboutDialog() {
		setStaticValues();
		init();
	}

	public void init() {
		setTitle("About Crimson");
		setBackground(UICommon.bg);
		setIconImages(UIUtil.getIconList());
		setMinimumSize(UICommon.dim_about);
		setPreferredSize(UICommon.dim_about);
		setResizable(false);

		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel.setBackground(UICommon.bg);
		contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(contentPanel);
		{

			GridBagLayout gbl_contentPanel = new GridBagLayout();
			gbl_contentPanel.columnWidths = new int[] { 600, 0 };
			gbl_contentPanel.rowHeights = new int[] { 150, 0, 0, 0, 0, 0, 0, 0, 4, 0 };
			gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0,
					Double.MIN_VALUE };
			contentPanel.setLayout(gbl_contentPanel);

		}

		ImageIcon gif = new ImageIcon(this.getClass().getClassLoader()
				.getResource("com/subterranean_security/crimson/viewer/ui/res/image/gif-550.gif"));

		JLabel lblImage = new JLabel(gif, SwingUtilities.CENTER);

		gif.setImageObserver(lblImage);
		lblImage.setVisible(true);
		GridBagConstraints gbc_lblImage = new GridBagConstraints();
		gbc_lblImage.insets = new Insets(0, 0, 5, 0);
		gbc_lblImage.fill = GridBagConstraints.BOTH;
		gbc_lblImage.gridx = 0;
		gbc_lblImage.gridy = 0;
		contentPanel.add(lblImage, gbc_lblImage);
		{
			JPanel stat_version = new JPanel();
			GridBagConstraints gbc_stat_version = new GridBagConstraints();
			gbc_stat_version.insets = new Insets(0, 0, 5, 0);
			gbc_stat_version.fill = GridBagConstraints.BOTH;
			gbc_stat_version.gridx = 0;
			gbc_stat_version.gridy = 1;
			contentPanel.add(stat_version, gbc_stat_version);
			stat_version.setLayout(new BoxLayout(stat_version, BoxLayout.X_AXIS));
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_version.add(panel_1);
				panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				{
					JLabel lblCrimsonVersion = new JLabel("Version");
					lblCrimsonVersion.setHorizontalAlignment(SwingConstants.CENTER);
					lblCrimsonVersion.setHorizontalTextPosition(SwingConstants.CENTER);
					lblCrimsonVersion.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblCrimsonVersion);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_version.add(panel_1);
				panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				{
					JLabel lblVal = new JLabel(Universal.version);
					lblVal.setFont(new Font("Dialog", Font.BOLD, 11));
					lblVal.setHorizontalAlignment(SwingConstants.CENTER);
					lblVal.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblVal);
				}
			}
		}
		{
			JPanel stat_buildnumber = new JPanel();
			GridBagConstraints gbc_stat_buildnumber = new GridBagConstraints();
			gbc_stat_buildnumber.insets = new Insets(0, 0, 5, 0);
			gbc_stat_buildnumber.fill = GridBagConstraints.BOTH;
			gbc_stat_buildnumber.gridx = 0;
			gbc_stat_buildnumber.gridy = 2;
			contentPanel.add(stat_buildnumber, gbc_stat_buildnumber);
			{

			}
			stat_buildnumber.setLayout(new BoxLayout(stat_buildnumber, BoxLayout.X_AXIS));
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_buildnumber.add(panel_1);
				{
					JLabel lblLicenseType = new JLabel("Build Number");
					lblLicenseType.setHorizontalAlignment(SwingConstants.CENTER);
					lblLicenseType.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblLicenseType);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_buildnumber.add(panel_1);
				JLabel lblVal_1 = new JLabel(buildNo);
				lblVal_1.setFont(new Font("Dialog", Font.BOLD, 11));
				lblVal_1.setHorizontalAlignment(SwingConstants.CENTER);
				lblVal_1.setPreferredSize(new Dimension(299, 15));
				panel_1.add(lblVal_1);
			}
		}
		{
			JPanel stat_buildtime = new JPanel();
			GridBagConstraints gbc_stat_buildtime = new GridBagConstraints();
			gbc_stat_buildtime.insets = new Insets(0, 0, 5, 0);
			gbc_stat_buildtime.fill = GridBagConstraints.BOTH;
			gbc_stat_buildtime.gridx = 0;
			gbc_stat_buildtime.gridy = 3;
			contentPanel.add(stat_buildtime, gbc_stat_buildtime);
			stat_buildtime.setLayout(new BoxLayout(stat_buildtime, BoxLayout.X_AXIS));
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_buildtime.add(panel_1);
				{
					JLabel lblBuildTime = new JLabel("Build Time");
					lblBuildTime.setHorizontalAlignment(SwingConstants.CENTER);
					panel_1.add(lblBuildTime);
					lblBuildTime.setPreferredSize(new Dimension(299, 15));

				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_buildtime.add(panel_1);
				{

					JLabel lblVal_2 = new JLabel(buildTime);
					lblVal_2.setFont(new Font("Dialog", Font.BOLD, 11));
					lblVal_2.setHorizontalAlignment(SwingConstants.CENTER);
					lblVal_2.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblVal_2);
				}
			}
		}
		{
			JPanel stat_installtime = new JPanel();
			GridBagConstraints gbc_stat_installtime = new GridBagConstraints();
			gbc_stat_installtime.insets = new Insets(0, 0, 5, 0);
			gbc_stat_installtime.fill = GridBagConstraints.BOTH;
			gbc_stat_installtime.gridx = 0;
			gbc_stat_installtime.gridy = 4;
			contentPanel.add(stat_installtime, gbc_stat_installtime);
			stat_installtime.setLayout(new BoxLayout(stat_installtime, BoxLayout.X_AXIS));
			{
				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(UICommon.bg));
				stat_installtime.add(panel);
				{
					JLabel lblInstallTime_1 = new JLabel("Java Uptime");
					lblInstallTime_1.setHorizontalAlignment(SwingConstants.CENTER);
					lblInstallTime_1.setPreferredSize(new Dimension(299, 15));
					panel.add(lblInstallTime_1);
				}
			}
			{
				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(UICommon.bg));
				stat_installtime.add(panel);
				{
					lbl_uptime = new JLabel("");
					lbl_uptime.setFont(new Font("Dialog", Font.BOLD, 11));
					lbl_uptime.setHorizontalAlignment(SwingConstants.CENTER);
					lbl_uptime.setPreferredSize(new Dimension(299, 15));
					panel.add(lbl_uptime);
				}
			}
		}
		{
			JPanel stat_jre = new JPanel();
			GridBagConstraints gbc_stat_jre = new GridBagConstraints();
			gbc_stat_jre.insets = new Insets(0, 0, 5, 0);
			gbc_stat_jre.fill = GridBagConstraints.BOTH;
			gbc_stat_jre.gridx = 0;
			gbc_stat_jre.gridy = 5;
			contentPanel.add(stat_jre, gbc_stat_jre);
			stat_jre.setLayout(new BoxLayout(stat_jre, BoxLayout.X_AXIS));
			{
				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(UICommon.bg));
				stat_jre.add(panel);
				{
					JLabel lblJre = new JLabel("Java Version");
					lblJre.setHorizontalAlignment(SwingConstants.CENTER);
					lblJre.setPreferredSize(new Dimension(299, 15));
					panel.add(lblJre);
				}
			}
			{
				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(UICommon.bg));
				stat_jre.add(panel);
				{
					JLabel lblValue_1 = new JLabel(
							System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
					lblValue_1.setFont(new Font("Dialog", Font.BOLD, 11));
					lblValue_1.setHorizontalAlignment(SwingConstants.CENTER);
					lblValue_1.setPreferredSize(new Dimension(299, 15));
					panel.add(lblValue_1);
				}
			}
		}
		{
			JPanel stat_edition = new JPanel();
			GridBagConstraints gbc_stat_edition = new GridBagConstraints();
			gbc_stat_edition.insets = new Insets(0, 0, 5, 0);
			gbc_stat_edition.fill = GridBagConstraints.BOTH;
			gbc_stat_edition.gridx = 0;
			gbc_stat_edition.gridy = 6;
			contentPanel.add(stat_edition, gbc_stat_edition);
			stat_edition.setLayout(new BoxLayout(stat_edition, BoxLayout.X_AXIS));
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_edition.add(panel_1);
				{
					JLabel lblInstallTime = new JLabel("Edition");
					lblInstallTime.setHorizontalAlignment(SwingConstants.CENTER);
					lblInstallTime.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblInstallTime);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(UICommon.bg));
				stat_edition.add(panel_1);
				{
					JLabel lblValue_2 = new JLabel("ALPHA");
					lblValue_2.setFont(new Font("Dialog", Font.BOLD, 11));
					lblValue_2.setHorizontalAlignment(SwingConstants.CENTER);
					lblValue_2.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblValue_2);
				}
			}
		}
		{
			JPanel stat_serial = new JPanel();
			GridBagConstraints gbc_stat_serial = new GridBagConstraints();
			gbc_stat_serial.insets = new Insets(0, 0, 5, 0);
			gbc_stat_serial.fill = GridBagConstraints.BOTH;
			gbc_stat_serial.gridx = 0;
			gbc_stat_serial.gridy = 7;
			contentPanel.add(stat_serial, gbc_stat_serial);
			stat_serial.setLayout(new BoxLayout(stat_serial, BoxLayout.X_AXIS));
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				stat_serial.add(panel_1);
				{
					JLabel lblNewLabel = new JLabel("Serial Code");
					lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
					lblNewLabel.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblNewLabel);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				stat_serial.add(panel_1);
				{
					String serial = "XXXXXXXXXXXXXXXX";
					try {
						String tmp = DatabaseStore.getDatabase().getString("serial");
						serial = tmp.substring(0, 4) + "-" + tmp.substring(4, 8) + "-" + tmp.substring(8, 12) + "-"
								+ tmp.substring(12);
					} catch (Exception e) {

					}
					JLabel lblNewLabel_1 = new JLabel(serial);
					lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
					lblNewLabel_1.setPreferredSize(new Dimension(299, 15));
					panel_1.add(lblNewLabel_1);
				}
			}
		}

		timer.schedule(new Uptimer(), 0, 1000);
	};

	private void setStaticValues() {
		try {
			buildNo = JarUtil.getManifestValue("Build-Number");
		} catch (IOException e) {
			buildNo = "unknown";
		}

		try {
			buildTime = JarUtil.getManifestValue("Build-Time");
		} catch (IOException e) {
			buildTime = "unknown";
		}

	}
}
