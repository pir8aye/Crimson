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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Object serialization utilities.
 * 
 * @author cilki
 * @since 1.0.0
 */
public final class SerialUtil {

	private SerialUtil() {
	}

	public static final boolean compress = false;

	/**
	 * Serialize an object with the default serializer.
	 * 
	 * @param object
	 *            Object which must be Serializable
	 * @return An array representing the object
	 */
	public static byte[] serialize(Object object) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		OutputStream out = byteOut;
		if (compress) {
			out = new DeflaterOutputStream(byteOut);
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
			oos.writeObject(object);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return byteOut.toByteArray();
	}

	/**
	 * Deserialize an object which was serialized with the default serializer
	 * 
	 * @param object
	 * @return The restored object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Serializable deserialize(byte[] object) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(object);

		InputStream in = byteIn;
		if (compress) {
			in = new InflaterInputStream(byteIn);
		}

		try (ObjectInputStream ois = new ObjectInputStream(in)) {
			return (Serializable) ois.readObject();
		}
	}

}