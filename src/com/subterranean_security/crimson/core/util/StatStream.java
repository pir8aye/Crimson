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
package com.subterranean_security.crimson.core.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class StatStream {

	// parallel
	private LimitedQueue<Long> points;
	private LimitedQueue<Long> times;

	private Callable<Long> c;
	private int period;
	private double conversion;

	public StatStream(double conversion, int keep, int period, Callable<Long> c) {
		this(conversion, keep);
		this.c = c;
		this.period = period;
	}

	public StatStream(double conversion, int keep) {
		points = new LimitedQueue<Long>(keep);
		times = new LimitedQueue<Long>(keep);
		this.conversion = conversion;
	}

	private Timer timer = null;

	public void start() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						long d = c.call();
						times.add(System.currentTimeMillis());
						points.add(d);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}, 0, period);
		}
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public double getInstantaneousSpeed() {
		int s = points.size();
		if (s < 2) {
			return 0;
		} else {
			long n = points.get(s - 1) - points.get(s - 2);
			long d = times.get(s - 1) - times.get(s - 2);

			return (n * conversion / d);
		}
	}

	public double addPoint(long l) {
		times.add(System.currentTimeMillis());
		points.add(l);

		return getInstantaneousSpeed();
	}

}
