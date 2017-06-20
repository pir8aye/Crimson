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
package com.subterranean_security.crimson.viewer.ui.screen.main.dmodules;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;

public abstract class TracedPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected Chart2D chart;

	protected Map<Integer, List<ITrace2D>> traceList;

	protected long updatePeriod = 900;

	protected Date start = new Date();
	protected Date last = new Date();
	private Timer updateTimer;

	public TracedPanel() {
		traceList = new HashMap<Integer, List<ITrace2D>>();
	}

	public void startRefresh(TimerTask task) {
		if (updateTimer == null) {
			updateTimer = new Timer();
			updateTimer.schedule(task, 0, updatePeriod);
		}
	}

	public void stopRefresh() {
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		stopRefresh();
		super.finalize();
	}

}
