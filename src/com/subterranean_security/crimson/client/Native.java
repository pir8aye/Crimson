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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Native {

	public static native String getActiveWindow();

	public static native long getSystemUptime();

	// move this!
	private static ArrayList<RandomAccessFile> cores = new ArrayList<RandomAccessFile>();
	private static int maxCores = 64;

	public static double[] getLinuxCPUTemps() {

		if (cores.size() == 0) {
			for (int i = 2; i < maxCores + 2; i++) {
				File f = new File("/sys/class/hwmon/hwmon1/temp" + i + "_input");
				if (f.exists()) {
					try {
						cores.add(new RandomAccessFile(f, "r"));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
		}
		double[] temps = new double[cores.size()];
		for (int i = 0; i < cores.size(); i++) {
			try {
				cores.get(i).seek(0);
				temps[i] = cores.get(i).readDouble();
				System.out.println("Core " + i + " temp: " + temps[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

}
