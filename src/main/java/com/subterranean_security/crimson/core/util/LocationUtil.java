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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subterranean_security.crimson.core.attribute.keys.singular.AK_LOC;

/**
 * Location utilities
 * 
 * @author cilki
 * @since 3.0.0
 */
public final class LocationUtil {
	private LocationUtil() {
	}

	private static final Logger log = LoggerFactory.getLogger(LocationUtil.class);

	public static float distance(float lat1, float lng1, float lat2, float lng2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) + (Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = 3958.75 * c;

		int meterConversion = 1609;

		return (float) (dist * meterConversion);
	}

	private static final int connectionTimeout = 800;
	private static final int readTimeout = 800;

	public static Map<AK_LOC, String> resolve(String ip) throws IOException, XMLStreamException {
		log.debug("Resolving location data for {}", ip);
		Map<AK_LOC, String> info = new HashMap<>();

		URLConnection connection = new URL("https://freegeoip.lwan.ws/xml/" + ip).openConnection();
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
		try (InputStream in = connection.getInputStream()) {
			XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
			try {
				String tag = "";
				String value = "";
				while (reader.hasNext()) {
					switch (reader.next()) {
					case XMLStreamConstants.START_ELEMENT:
						tag = reader.getLocalName().toLowerCase();
						break;
					case XMLStreamConstants.CDATA:
					case XMLStreamConstants.CHARACTERS:
						if (!tag.equals("response")) {
							value = reader.getText();
						}

						break;
					case XMLStreamConstants.END_ELEMENT:
						if (!tag.equals("response")) {
							info.put(AK_LOC.valueOf(tag.toUpperCase()), value.trim());
						}
						break;
					}
				}
			} finally {
				reader.close();
			}
		}

		return info;
	}
}