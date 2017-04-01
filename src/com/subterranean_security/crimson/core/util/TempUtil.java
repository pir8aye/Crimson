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
package com.subterranean_security.crimson.core.util;

import java.io.File;

import com.subterranean_security.crimson.core.Common;

public class TempUtil {
	public static final String prefix = "crimson_temp_";

	public static File getFile(String name) {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + name);
		f.deleteOnExit();
		return f;
	}

	public static File getFile() {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + RandomUtil.randString(9));
		f.deleteOnExit();
		return f;
	}

	public static File getDir() {
		File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + RandomUtil.randString(9));
		f.mkdirs();
		f.deleteOnExit();

		return f;
	}

	public static void clear() {
		for (File f : Common.Directories.tmp.listFiles()) {
			if (f.getName().startsWith(prefix)) {
				// delete it
				FileUtil.delete(f);
			}
		}
	}
}