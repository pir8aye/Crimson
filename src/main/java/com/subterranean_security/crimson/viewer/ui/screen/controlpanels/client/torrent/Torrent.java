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
package com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.torrent;

import java.awt.BorderLayout;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.subterranean_security.crimson.viewer.ui.screen.controlpanels.client.CPPanel;

public class Torrent extends JPanel implements CPPanel {
	private static final long serialVersionUID = 1L;

	public Torrent() {
		setLayout(new BorderLayout(0, 0));

		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
	}

	@Override
	public void clientOffline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverOffline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverOnline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tabClosed() {
		// TODO Auto-generated method stub

	}

}
