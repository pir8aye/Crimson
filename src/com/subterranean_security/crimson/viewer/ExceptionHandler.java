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
package com.subterranean_security.crimson.viewer;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Reporter;
import com.subterranean_security.crimson.core.util.CUtil;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static final Logger log = CUtil.Logging.getLogger(ExceptionHandler.class);

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		if (Common.isDebugMode()) {
			arg1.printStackTrace();
		}

		Reporter.report(Reporter.newReport().setStackTrace(CUtil.Misc.getStack(arg1)).build());

	}

}
