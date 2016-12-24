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
package com.subterranean_security.crimson.core.platform.info;

import com.subterranean_security.crimson.core.platform.SigarStore;
import com.subterranean_security.crimson.core.util.CUtil;

public final class RAM {
	private RAM() {
	}

	// Try biosdecode/dmidecode
	public static String getFrequency() {
		return "N/A";
	}

	public static String getTemperature() {
		return "N/A";
	}

	public static String getSize() {
		return CUtil.UnitTranslator.translateMemSize(SigarStore.getMem().getTotal());
	}

	public static String getUsage() {
		SigarStore.refreshMem();
		return CUtil.UnitTranslator.translateMemSize(SigarStore.getMem().getActualUsed());
	}

	public static String getClientUsage() {
		SigarStore.refreshProcessMem();
		return CUtil.UnitTranslator.translateMemSize(SigarStore.getProcessMem().getResident());
	}

}
