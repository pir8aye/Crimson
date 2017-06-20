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
package com.subterranean_security.crimson.hcp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	public static HashMap<String, ArrayList<String>> arguments = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) {
		for (String s : args) {
			try {
				String key = s.substring(0, s.indexOf('='));
				String value = s.substring(s.indexOf('=') + 1);
				if (!arguments.containsKey(key)) {
					arguments.put(key, new ArrayList<String>());
				}
				arguments.get(key).add(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (arguments.containsKey("sleep")) {
			try {
				Thread.sleep(1000 * Integer.parseInt(arguments.remove("sleep").get(0)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (arguments.containsKey("delete")) {
			for (String path : arguments.remove("delete")) {
				Operations.delete(new File(path));
			}
		}

		if (arguments.containsKey("run")) {
			for (String s : arguments.remove("run")) {
				Operations.run(s);
			}
		}

	}

}
