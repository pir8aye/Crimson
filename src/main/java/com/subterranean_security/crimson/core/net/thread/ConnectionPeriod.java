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
package com.subterranean_security.crimson.core.net.thread;

/**
 * This class models a connection period over time. This period may remain
 * static or increase, depending on the requirements of the client code.
 */
public class ConnectionPeriod {

	/**
	 * The current period
	 */
	private int period;

	/**
	 * The maximum period
	 */
	private int maximum;

	/**
	 * The amount to change the period in each step
	 */
	private int interval;

	/**
	 * Create an unchanging ConnectionPeriod
	 * 
	 * @param period
	 */
	public ConnectionPeriod(int period) {
		this(period, period, 0);
	}

	/**
	 * Create a ConnectionPeriod which increases at the default rate
	 * 
	 * @param period
	 * @param maximum
	 */
	public ConnectionPeriod(int period, int maximum) {
		this(period, maximum, 1000);
	}

	/**
	 * Create a ConnectionPeriod which increases at the specified rate
	 * 
	 * @param period
	 * @param maximum
	 * @param interval
	 */
	public ConnectionPeriod(int period, int maximum, int interval) {
		if (period < 0)
			throw new IllegalArgumentException();
		if (maximum < period)
			throw new IllegalArgumentException(
					"The maximum period must be greater than or equal to the initial period");
		if (interval < 0)
			throw new IllegalArgumentException();

		this.period = period;
		this.maximum = maximum;
		this.interval = interval;
	}

	/**
	 * Get the period
	 * 
	 * @return The next period which may be equal to or greater than the last
	 *         period
	 */
	public int getPeriod() {
		int toReturn = period;
		if (period < maximum) {
			period += interval;
			if (period > maximum) {
				period = maximum;
			}
		}
		return toReturn;
	}
}
