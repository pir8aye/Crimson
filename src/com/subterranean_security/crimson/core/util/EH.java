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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.Reporter;

public class EH implements Thread.UncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(EH.class);

	@Override
	public void uncaughtException(Thread thread, Throwable t) {

		log.debug(CUtil.Misc.getStack(t));

		Thread.currentThread().getStackTrace();// TODO send

		Reporter.report(Reporter.newReport().setStackTrace(CUtil.Misc.getStack(t)).build());

	}

	public static void handle(Throwable t) {
		log.debug(CUtil.Misc.getStack(t));
		Reporter.report(Reporter.newReport().setStackTrace(CUtil.Misc.getStack(t)).build());
	}

}
