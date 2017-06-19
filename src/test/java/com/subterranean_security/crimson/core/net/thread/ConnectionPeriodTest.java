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

import static org.junit.Assert.*;

import org.junit.Test;

public class ConnectionPeriodTest {

	@Test
	public void testGetPeriod() {
		ConnectionPeriod period = new ConnectionPeriod(8000);
		for (int i = 0; i < 100000; i++)
			assertEquals(8000, period.getPeriod());

		period = new ConnectionPeriod(1000, 1000);
		for (int i = 0; i < 100000; i++)
			assertEquals(1000, period.getPeriod());

		period = new ConnectionPeriod(1000, 10000);
		for (int i = 1000; i <= 10000; i += 1000)
			assertEquals(i, period.getPeriod());

		period = new ConnectionPeriod(1000, 10000, 100);
		for (int i = 1000; i <= 10000; i += 100)
			assertEquals(i, period.getPeriod());
	}

}
