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
package com.subterranean_security.crimson.client.modules;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.util.CUtil;

public class Idler extends Thread {
	private static final Logger log = CUtil.Logging.getLogger(Idler.class);

	private Queue<Runnable> buffer = new ConcurrentLinkedQueue<Runnable>();

	private boolean idle = false;
	private int segments = 0;
	private int seglen;
	private int segThresh = 50;
	private Date idleSince;

	public Idler(int segmentLength) {
		seglen = segmentLength;
		if (MouseInfo.getPointerInfo().getLocation() == null) {
			log.error("Could not poll mouse");
		}
	}

	public void addIdleTask(Runnable r) {
		buffer.offer(r);
	}

	public void runNextTask() {
		try {
			new Thread(buffer.poll()).start();
		} catch (Throwable e) {
			if (!(e instanceof NullPointerException)) {
				log.error("Could not run a queued task");
			}

		}
	}

	public Date getLastIdle() {
		return idleSince;
	}

	public boolean isIdle() {
		return idle;
	}

	public void clearSegments() {
		segments = 0;
	}

	public void run() {
		while (!Thread.interrupted()) {
			Point one = MouseInfo.getPointerInfo().getLocation();
			try {
				Thread.sleep(seglen);
			} catch (InterruptedException e) {
				return;
			}
			Point two = MouseInfo.getPointerInfo().getLocation();

			if (equal(one, two)) {
				segments++;
				if (segments > segThresh) {
					if (segments > segThresh + 1) {
						log.info("System is now IDLE");
						idleSince = new Date(new Date().getTime() - (seglen * segments));
					}
					idle = true;
					runNextTask();

				}
			} else {
				if (idle) {
					log.debug("System is now ACTIVE");
				}
				idle = false;
				segments = 0;
			}

		}
	}

	private boolean equal(Point one, Point two) {
		if (one == null && two == null) {
			return true;
		}
		return one.x == two.x && one.y == two.y;

	}

}
