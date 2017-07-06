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
package com.subterranean_security.crimson.core.misc;

import com.subterranean_security.crimson.universal.Universal.Instance;

public class CVID {

	public static final int IID_SPACE = 3;

	public static Instance extractIID(int cvid) {
		int iid = cvid & ((int) Math.pow(2, IID_SPACE) - 1);
		return Instance.values()[iid];
	}

	public static int generate(Instance instance) {
		// TODO get last cvid
		int cvid = 0;

		return (cvid << IID_SPACE) + instance.ordinal();
	}
}
