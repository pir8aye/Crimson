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
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import com.google.protobuf.InvalidProtocolBufferException;
import com.subterranean_security.crimson.core.misc.AuthenticationGroup;
import com.subterranean_security.crimson.proto.core.Misc.AuthMethod;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;

public final class CryptoUtil {
	private CryptoUtil() {
	}

	public static String hash(String type, char[] target) throws NoSuchAlgorithmException {
		byte[] t = toBytes(target);

		MessageDigest digest;

		digest = MessageDigest.getInstance(type);
		digest.update(t);
		byte messageDigest[] = digest.digest();
		Arrays.fill(t, (byte) 0);

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
			hexString.append(String.format("%02X", 0xFF & messageDigest[i]));

		return hexString.toString().toLowerCase();

	}

	private static byte[] hash(String type, byte[] target) throws NoSuchAlgorithmException {
		MessageDigest digest;

		digest = MessageDigest.getInstance(type);
		digest.update(target);
		return digest.digest();

	}

	private static String hash(String type, String target) throws NoSuchAlgorithmException {

		byte messageDigest[] = hash(type, target.getBytes());

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
			hexString.append(String.format("%02X", 0xFF & messageDigest[i]));

		return hexString.toString().toLowerCase();

	}

	public static String hashCrimsonPassword(String pass, String salt) {
		return hashCrimsonPassword(pass.toCharArray(), salt);
	}

	public static String hashCrimsonPassword(char[] pass, String salt) {
		byte[] hash = (new String(pass) + salt).getBytes(StandardCharsets.UTF_16);
		try {
			for (int i = 0; i < 999; i++) {
				hash = hash("SHA-256", hash);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(hash);
	}

	public static String hashOpencartPassword(String pass, String salt) {
		try {
			return hash("SHA-1", salt + hash("SHA-1", salt + hash("SHA-1", pass)));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String genSalt() {
		return RandomUtil.randString(8);
	}

	public static String hashSign(String magic, String key) {
		try {
			return Base64.getEncoder().encodeToString(hash("SHA-256", (magic + key).getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return RandomUtil.randString(8);
	}

	public static String hashSign(String magic, byte[] key) {
		return hashSign(magic, new String(key));
	}

	public static String signGroupChallenge(String magic, byte[] key) {
		try {
			PrivateKey pkey = KeyFactory.getInstance("DSA").generatePrivate(new PKCS8EncodedKeySpec(key));
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(pkey);
			dsa.update(magic.getBytes());
			return Base64.getEncoder().encodeToString(dsa.sign());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean verifyGroupChallenge(String magic, byte[] key, String signature) {
		try {
			PublicKey pkey = KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(key));
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initVerify(pkey);
			dsa.update(magic.getBytes());
			return dsa.verify(Base64.getDecoder().decode(signature));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static final int seedSuffixLength = 128;

	public static AuthenticationGroup generateGroup(String name, byte[] seedPrefix) {

		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

			byte[] seed = new byte[seedPrefix.length + seedSuffixLength];
			for (int i = 0; i < seedPrefix.length; i++) {
				seed[i] = seedPrefix[i];
			}
			byte[] seedSuffix = SecureRandom.getSeed(seedSuffixLength);
			for (int i = 0; i < seedSuffix.length; i++) {
				seed[seedPrefix.length + i] = seedSuffix[i];
			}
			RandomUtil.clearByte(seedSuffix);
			RandomUtil.clearByte(seedPrefix);
			random.setSeed(seed);

			generator.initialize(1024, random);

			KeyPair pair = generator.generateKeyPair();
			return new AuthenticationGroup(name, pair.getPrivate(), pair.getPublic());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Outcome exportGroup(AuthMethod am, File output) {

		try {
			PrintWriter pw = new PrintWriter(output);
			pw.println(Base64.getEncoder().encodeToString(am.toByteArray()));

			pw.close();
		} catch (FileNotFoundException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}

	public static AuthMethod importGroup(File input) {

		try (BufferedReader br = new BufferedReader(new FileReader(input))) {

			return AuthMethod.parseFrom(Base64.getDecoder().decode(br.readLine()));

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

	public static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000');
		Arrays.fill(byteBuffer.array(), (byte) 0);
		return bytes;
	}

}
