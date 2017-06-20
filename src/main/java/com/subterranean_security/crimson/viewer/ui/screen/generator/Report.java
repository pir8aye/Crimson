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
package com.subterranean_security.crimson.viewer.ui.screen.generator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.core.util.UnitTranslator;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.components.DataViewer;

public class Report extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public Report(GenReport gr) {
		init(gr);
	}

	public void init(GenReport gr) {
		setIconImages(UIUtil.getAppIcons());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPreferredSize(UICommon.dim_generation_report);
		setMinimumSize(UICommon.dim_generation_report);
		setTitle("Generation Report: " + gr.getOutputType());

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}

		String[] h = new String[] { "Property", "Value" };
		ArrayList<String[]> v = new ArrayList<String[]>();

		v.add(new String[] { "Result", gr.getResult() ? "Success" : "Failed" });
		v.add(new String[] { "Generation Date", "" + new Date(gr.getGenDate()).toString() });
		v.add(new String[] { "Generation Time", "" + gr.getGenTime() + " milliseconds" });
		if (!gr.getHashMd5().isEmpty()) {
			v.add(new String[] { "MD5 Hash", gr.getHashMd5() });
		}
		if (!gr.getHashSha256().isEmpty()) {
			v.add(new String[] { "SHA256 Hash", gr.getHashSha256() });
		}
		if (!gr.getOutputType().isEmpty()) {
			v.add(new String[] { "Output Type", gr.getOutputType() });
		}
		if (gr.getFileSize() != 0) {
			v.add(new String[] { "Output Size: ", UnitTranslator.familiarize(gr.getFileSize(), UnitTranslator.BYTES)
					+ " (" + gr.getFileSize() + " bytes)" });
		}
		if (!gr.getComment().isEmpty()) {
			v.add(new String[] { "Comment", gr.getComment() });
		}

		DataViewer dv = new DataViewer();
		dv.setList(v);
		dv.setHeaders(h);

		getContentPane().add(dv);
	}

}
