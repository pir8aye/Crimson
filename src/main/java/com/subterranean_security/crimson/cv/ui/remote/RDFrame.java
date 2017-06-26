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
package com.subterranean_security.crimson.cv.ui.remote;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_NET;
import com.subterranean_security.crimson.core.attribute.keys.singular.AK_USER;
import com.subterranean_security.crimson.viewer.store.ViewerProfileStore;
import com.subterranean_security.crimson.viewer.ui.UIUtil;

public class RDFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RDPanel rdp;

	public RDFrame(RDPanel.Type type, int cvid) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(450, 300));
		setSize(new Dimension(450, 300));
		setIconImages(UIUtil.getAppIcons());
		setTitle("Remote Desktop (" + ViewerProfileStore.getClient(cvid).get(AK_USER.NAME) + "@"
				+ ViewerProfileStore.getClient(cvid).get(AK_NET.EXTERNAL_IPV4) + ")");
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
