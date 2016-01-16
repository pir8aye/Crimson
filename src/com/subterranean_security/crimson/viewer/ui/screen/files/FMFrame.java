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
package com.subterranean_security.crimson.viewer.ui.screen.files;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.subterranean_security.crimson.viewer.ui.panel.HPanel;
import com.subterranean_security.crimson.viewer.ui.utility.UUtil;

public class FMFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private HPanel contentPane;

	private Type type;

	public FMFrame(Type type) {
		setTitle("File Manager");
		this.type = type;
		setIconImages(UUtil.getIconList());

		FMPanel fmp = new FMPanel(Type.SV);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setMinimumSize(new Dimension(450, 300));
		contentPane = new HPanel(fmp);
		setContentPane(contentPane);
	}

	public static enum Type {
		SV, SC, CV, CC;
	}

}
