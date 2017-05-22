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
package com.subterranean_security.crimson.core.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public enum ObjectTransfer {
	;

	public static class ProtoBuffer {

		public static byte[] serialize(Object o) {

			return null;
		}

		public static Object deserialize(byte[] bytes) {

			return null;
		}

	}

	public static class Default {

		public static byte[] serialize(Serializable object) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return baos.toByteArray();

		}

		public static byte[] serialize(Object o) {
			return serialize((Serializable) o);
		}

		public static Serializable deserialize(byte[] object) throws Exception {
			if (object == null) {
				throw new Exception();
			}
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(object));
				return (Serializable) ois.readObject();
			} catch (IllegalArgumentException | IOException | ClassNotFoundException e1) {
				throw new Exception();
			} finally {
				try {
					ois.close();
				} catch (Exception e) {
				}
			}
		}

	}
}