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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Crypto {
	;

	private static byte[] hash(String type, byte[] target) throws NoSuchAlgorithmException {
		MessageDigest digest;

		digest = MessageDigest.getInstance(type);
		digest.update(target);
		return digest.digest();

	}

	public static String hashPass(char[] pass, String salt) {
		byte[] hash = (new String(pass) + salt).getBytes(StandardCharsets.UTF_16);
		try {
			for (int i = 0; i < 999; i++) {
				hash = hash("SHA-256", hash);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(hash, StandardCharsets.UTF_16);
	}

	public static String hashPass(char[] pass) {
		byte[] hash = new String(pass).getBytes(StandardCharsets.UTF_16);
		try {
			for (int i = 0; i < 999; i++) {
				hash = hash("SHA-256", hash);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(hash, StandardCharsets.UTF_16);
	}

	public static String genSalt() {
		return CUtil.Misc.randString(8);
	}

	public static String sign(String magic, String key) {
		try {

			return new String(B64.encode(hash("SHA-256", (magic + key).getBytes())));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CUtil.Misc.randString(8);// return something random because signing
										// failed
	}

}
