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
package com.subterranean_security.crimson.client;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Logger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		Logger.debug("An unexpected exception has occurred");
		if (Common.isDebugMode()) {
			arg1.printStackTrace();
		}



	}

}
