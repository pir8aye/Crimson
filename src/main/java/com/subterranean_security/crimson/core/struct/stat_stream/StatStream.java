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
package com.subterranean_security.crimson.core.struct.stat_stream;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class StatStream {

	private StatStreamBuffer<StatPoint> buffer;

	private Callable<Long> c;
	private int period;
	private double conversion;

	public StatStream(double conversion, int keep, int period, Callable<Long> c) {
		this(conversion, keep);
		this.c = c;
		this.period = period;
	}

	public StatStream(double conversion, int keep) {
		buffer = new StatStreamBuffer<>(keep);
		this.conversion = conversion;
	}

	private Timer timer;

	public void start() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						buffer.add(new StatPoint(System.currentTimeMillis(), c.call()));
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
		if (buffer.size() < 2) {
			return 0;
		} else {
			StatPoint recent1 = buffer.get(0);
			StatPoint recent2 = buffer.get(1);

			return ((recent1.value - recent2.value) * conversion / (recent1.time - recent2.time));
		}
	}

	public double addPoint(long l) {
		buffer.add(new StatPoint(System.currentTimeMillis(), l));

		return getInstantaneousSpeed();
	}

	private static class StatPoint {
		private long time;
		private long value;

		public StatPoint(long time, long value) {
			this.time = time;
			this.value = value;
		}
	}

}
