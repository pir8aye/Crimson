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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Misc.AuthMethod;
import com.subterranean_security.crimson.core.proto.Misc.Group;
import com.subterranean_security.crimson.core.proto.Misc.Outcome;

public enum Crypto {
	;

	private static byte[] hash(String type, byte[] target) throws NoSuchAlgorithmException {
		MessageDigest digest;

		digest = MessageDigest.getInstance(type);
		digest.update(target);
		return digest.digest();

	}

	private static String hash(String type, String target) throws NoSuchAlgorithmException {
		MessageDigest digest;

		digest = MessageDigest.getInstance(type);
		digest.update(target.getBytes());
		byte messageDigest[] = digest.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
			hexString.append(String.format("%02X", 0xFF & messageDigest[i]));

		return hexString.toString().toLowerCase();

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
		return new String(B64.encode(hash));
	}

	public static String hashOCPass(String pass, String salt) {
		try {
			return hash("SHA-1", salt + hash("SHA-1", salt + hash("SHA-1", pass)));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String hashPass(String pass, String salt) {
		return hashPass(pass.toCharArray(), salt);
	}

	public static String hashPass(char[] pass) {
		return hashPass(pass, "");
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
		return CUtil.Misc.randString(8);
	}

	public static Outcome exportGroup(AuthMethod am, File output) {

		try {
			PrintWriter pw = new PrintWriter(output);
			pw.println(B64.encode(am.toByteArray()));

			pw.close();
		} catch (FileNotFoundException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static AuthMethod importGroup(File input) {

		try (BufferedReader br = new BufferedReader(new FileReader(input))) {

			return AuthMethod.parseFrom(B64.decode(br.readLine()));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
