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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import com.subterranean_security.crimson.core.platform.Platform;

public class NetUtil {

	private static final int DOWNLOAD_LIMIT = 100 * 1024 * 1024 * 1024;

	/**
	 * Download small files ( < 100 MiB) from a website
	 * 
	 * @return A byte array containing the file with no padding
	 */
	public static byte[] download(String direct) throws IOException {
		if (direct == null)
			throw new IllegalArgumentException();

		URLConnection con = new URL(direct).openConnection();

		if (con.getContentLength() > DOWNLOAD_LIMIT)
			throw new IllegalArgumentException("File too large");

		byte[] fileData = null;
		try (DataInputStream dis = new DataInputStream(con.getInputStream())) {
			fileData = new byte[con.getContentLength()];
			for (int i = 0; i < fileData.length; i++) {
				fileData[i] = dis.readByte();
			}
		}

		return fileData;
	}

	// TODO rewrite
	public static double ping(String host) {
		switch (Platform.osFamily) {
		case BSD:
			break;
		case LIN:

			return Double.parseDouble(
					Native.execute("ping -c 1 " + host + " | tail -1| awk '{print $4}' | cut -d '/' -f 2"));
		case OSX:
			break;
		case SOL:
			break;
		case WIN:
			String output = Native.execute("ping /n 1 /w 1 " + host);
			double d = 0;
			try {
				d = Double.parseDouble(output.split("Average = ")[1].replaceAll("ms", ""));
			} catch (Exception e) {
				// nope
			}
			return d;
		default:
			break;

		}
		return 0.0;
	}

	/**
	 * Tests the availability of a port on a remote host by making a connection
	 * 
	 * @param host
	 *            The target dns name or ip address
	 * @param port
	 *            The target port
	 * @return True if the port is open
	 */
	public static boolean checkPort(String host, int port) {
		if (!ValidationUtil.dns(host) && !ValidationUtil.ipv4(host))
			throw new IllegalArgumentException("Invalid host: " + host);
		if (!ValidationUtil.port(port))
			throw new IllegalArgumentException("Invalid port: " + port);

		try (Socket sock = new Socket(host, port)) {
			return sock.isConnected();
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}