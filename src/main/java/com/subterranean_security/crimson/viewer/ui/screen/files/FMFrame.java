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

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.UICommon;
import com.subterranean_security.crimson.viewer.ui.UIUtil;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.epanel.EPanel;

public class FMFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private Type type;

	public EPanel epanel;

	public FMFrame(Type type) {

		this.type = type;

		setTitle("File Manager");
		setIconImages(UIUtil.getAppIcons());

		JPanel root = new JPanel();
		root.setLayout(new BorderLayout());

		epanel = new EPanel(root);
		FMPanel fmp = new FMPanel(Type.VV, epanel);
		root.add(fmp, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setMinimumSize(UICommon.dim_filemanager);
		setContentPane(epanel);
	}

	public static enum Type {
		VV, SV, SC, CV, CC;
	}

}
