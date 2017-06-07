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
package com.subterranean_security.crimson.viewer.ui.screen.main.dmodules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.subterranean_security.crimson.core.net.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel.Type;
import com.subterranean_security.crimson.sv.profile.ClientProfile;
import com.subterranean_security.crimson.viewer.ui.common.panels.sl.dpanel.DModule;

public class Preview extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private boolean showing = false;
	private RDPanel rdp;

	public Preview() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Preview", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
	}

	@Override
	public void setTarget(ClientProfile p) {
		System.out.println("Preview::setTarget");
		removeAll();
		rdp = new RDPanel(Type.VIEW_ONLY, p.getCvid(), false);
		add(rdp, BorderLayout.CENTER);
	}

	@Override
	public void updateGraphics() {
		// TODO Auto-generated method stub

	}

	private RemoteMaster rm;

	@Override
	public void setShowing(boolean showing) {
		System.out.println("Preview::setShowing: " + showing);
		this.showing = showing;
		if (showing) {
			rdp.start();
		} else {
			if (rm != null) {
				rdp.stop();
			}
		}

	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDWidth() {
		return 120;
	}

	@Override
	public boolean isDetailOpen() {
		return showing;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(120, 100);

	}

}
