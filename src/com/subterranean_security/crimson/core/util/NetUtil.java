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

	// TODO implement
	public static String post(String host, String parameters) {
		return "OK";
	}

	/**
	 * Download small files from the internet
	 * 
	 * @return
	 */
	public static byte[] download(String rlocation) throws IOException {

		URLConnection con;
		DataInputStream dis;
		byte[] fileData = null;

		con = new URL(rlocation).openConnection();
		dis = new DataInputStream(con.getInputStream());
		fileData = new byte[con.getContentLength()];
		for (int i = 0; i < fileData.length; i++) {
			fileData[i] = dis.readByte();
		}
		dis.close();

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
	 * Tests the availability of a port on a remote host by making a connection.
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public static boolean testPortVisibility(String host, int port) {
		try (Socket sock = new Socket(host, port)) {
			return sock.isConnected();
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}