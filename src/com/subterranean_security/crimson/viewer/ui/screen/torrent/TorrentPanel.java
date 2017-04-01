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
package com.subterranean_security.crimson.viewer.ui.screen.torrent;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JMenuBar;

public class TorrentPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public TorrentPanel() {
		setLayout(new BorderLayout());
		TorrentList list = new TorrentList();
		add(list, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);
	}

}
