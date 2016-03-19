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
package com.subterranean_security.crimson.core.stream;

import java.util.HashMap;

import org.slf4j.Logger;

import com.subterranean_security.crimson.core.util.CUtil;

/**
 * Stream storage for endpoints only
 *
 */
public enum StreamStore {
	;
	private static Logger log = CUtil.Logging.getLogger(StreamStore.class);
	private static HashMap<Integer, Stream> streams = new HashMap<Integer, Stream>();

	public static Stream getStream(int id) {
		return streams.get(id);
	}

	public static HashMap<Integer, Stream> getStreams() {
		return streams;
	}

	public static void removeStream(int id) {
		try {
			streams.remove(id).stop();
		} catch (NullPointerException e) {
			// that stream doesnt exist
		}
	}

	public static void addStream(int id, Stream s) {
		streams.put(id, s);
	}

	public static int size() {
		return streams.size();
	}

}
