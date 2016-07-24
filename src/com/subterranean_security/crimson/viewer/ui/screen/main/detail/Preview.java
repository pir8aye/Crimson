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
package com.subterranean_security.crimson.viewer.ui.screen.main.detail;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.subterranean_security.crimson.core.stream.remote.RemoteMaster;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel;
import com.subterranean_security.crimson.cv.ui.remote.RDPanel.Type;
import com.subterranean_security.crimson.sv.profile.ClientProfile;

public class Preview extends JPanel implements DModule {

	private static final long serialVersionUID = 1L;

	private boolean showing = false;
	private RDPanel rdp;

	public Preview() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100, 100));
	}

	private ClientProfile cp;

	@Override
	public void setTarget(ClientProfile p) {
		this.cp = p;
		removeAll();
		rdp = new RDPanel(Type.VIEW_ONLY, p.getCvid());
		add(rdp, BorderLayout.CENTER);
	}

	@Override
	public void updateGraphics() {
		// TODO Auto-generated method stub

	}

	private RemoteMaster rm;

	@Override
	public void setShowing(boolean showing) {
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
	public int compareTo(DModule o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDetailOpen() {
		return showing;
	}

	@Override
	public Dimension getPreferredSize() {

		try {
			Dimension d = this.getParent().getSize();

			double nw = rdp.rdArea.screenImage.getWidth();
			double nh = rdp.rdArea.screenImage.getHeight();

			double ratio = nw / nh;

			if (nw > d.getWidth()) {
				nw = d.getWidth();
				nh = nw / ratio;
			}
			if (nh > d.getHeight()) {
				nh = d.getHeight();
				nw = nh * ratio;
			}

			return new Dimension((int) nw, (int) nh);
		} catch (Exception e) {
			return new Dimension(110, 110);
		}
	}

}
