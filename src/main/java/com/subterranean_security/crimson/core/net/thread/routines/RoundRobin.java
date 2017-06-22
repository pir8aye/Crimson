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
package com.subterranean_security.crimson.core.net.thread.routines;

import java.util.List;

import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.net.thread.ConnectionPeriod;
import com.subterranean_security.crimson.core.net.thread.ConnectionThread;
import com.subterranean_security.crimson.proto.core.Generator.NetworkTarget;

/**
 * Each network target is tried sequentially.
 */
public class RoundRobin extends ConnectionRoutine {

	public RoundRobin(ExecutorFactory executor, List<NetworkTarget> targets, ConnectionPeriod connectionPeriod,
			int maximumIterations, boolean forceCerts) {
		super(executor, targets, connectionPeriod, maximumIterations, forceCerts);
	}

	public RoundRobin(ExecutorFactory executor, String server, int port, boolean forceCerts) {
		super(executor, server, port, forceCerts);
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {

			iterations++;
			int period = connectionPeriod.getPeriod() / targets.size();
			for (NetworkTarget n : targets) {

				long t1 = System.currentTimeMillis();
				result = ConnectionThread.makeConnection(executor, n.getServer(), n.getPort(), forceCerts);
				if (result != null)
					return;
				long t2 = System.currentTimeMillis();

				try {
					Thread.sleep(period - (t2 - t1));
				} catch (InterruptedException e) {
					return;
				}
			}

			if (iterations == maximumIterations)
				return;
		}
	}

	@Override
	public String getName() {
		return String.format("RoundRobin on %d targets", targets.size());
	}

}