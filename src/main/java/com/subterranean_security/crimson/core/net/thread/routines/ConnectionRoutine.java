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

import java.util.LinkedList;
import java.util.List;

import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.factory.ExecutorFactory;
import com.subterranean_security.crimson.core.net.thread.ConnectionPeriod;
import com.subterranean_security.crimson.proto.core.Generator.NetworkTarget;

public abstract class ConnectionRoutine implements Runnable {
	/**
	 * A factory which will spawn an executor for the new connection
	 */
	protected ExecutorFactory executor;

	/**
	 * One or more targets to which the Thread will attempt a connection
	 */
	protected List<NetworkTarget> targets;

	/**
	 * The connection period which determines the wait interval
	 */
	protected ConnectionPeriod connectionPeriod;

	protected boolean forceCerts;

	/**
	 * The number of connection attempts made to each target
	 */
	protected int iterations;

	/**
	 * The number of connection attempts before giving up
	 */
	protected int maximumIterations;

	/**
	 * The resultant Connector if a connection to a network target succeeded
	 */
	protected Connector result;

	/**
	 * Single connection attempt
	 * 
	 * @param executor
	 * @param server
	 * @param port
	 */
	protected ConnectionRoutine(ExecutorFactory executor, String server, int port, boolean forceCerts) {
		this.executor = executor;
		this.forceCerts = forceCerts;
		this.maximumIterations = 1;
		this.connectionPeriod = new ConnectionPeriod(0);

		targets = new LinkedList<>();
		targets.add(NetworkTarget.newBuilder().setServer(server).setPort(port).build());
	}

	protected ConnectionRoutine(ExecutorFactory executor, List<NetworkTarget> targets,
			ConnectionPeriod connectionPeriod, int maximumIterations, boolean forceCerts) {
		this.executor = executor;
		this.forceCerts = forceCerts;
		this.targets = targets;
		this.connectionPeriod = connectionPeriod;
		this.maximumIterations = maximumIterations;
	}

	public Connector getResult() {
		return result;
	}

	/**
	 * @return A thread name for profiling purposes
	 */
	public abstract String getName();
}