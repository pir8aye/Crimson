/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Certificate utilities.
 * 
 * @author cilki
 * @since 5.0.0
 */
public final class CertUtil {
	private CertUtil() {
	}

	public static String certificateToString(String cert) throws CertificateException, IOException {
		return certificateToString(parseCertificate(cert));
	}

	public static String certificateToString(X509Certificate cert) {
		if (cert == null)
			throw new IllegalArgumentException();

		StringBuffer buffer = new StringBuffer();

		// sig algorithm
		buffer.append(String.format("Signature: (%s)%n", cert.getSigAlgName()));

		//
		buffer.append("\t" + cert.getSubjectX500Principal().getName() + "\n");

		// validity
		buffer.append("\tValidity\n");
		buffer.append("\t\tNot Before: " + cert.getNotBefore().toString() + "\n");
		buffer.append("\t\tNot After: " + cert.getNotAfter().toString() + "\n");

		PublicKey pub = cert.getPublicKey();
		buffer.append(String.format("Public key: (%s)%n", pub.getAlgorithm()));
		buffer.append(formatKey(pub.getEncoded(), 16, "\t"));

		return buffer.toString();
	}

	public static String certificateToHtml(X509Certificate cert) {
		return String.format("<html>%s</html>",
				certificateToString(cert).replaceAll("\n", "<br>").replaceAll("\t", "&emsp;"));
	}

	/**
	 * Format a hexidecimal key
	 * 
	 * @param data
	 *            Key data
	 * @param columns
	 *            Formatted key width
	 * @param padding
	 *            A padding String to go before the first byte in each row
	 * @return A nicely formatted key with bytes separated by colons
	 */
	private static String formatKey(byte[] data, int columns, String padding) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < data.length;) {
			buffer.append(padding);
			for (int j = 0; j < columns && i < data.length; j++, i++) {
				buffer.append(String.format("%02x:", data[i]));
			}
			buffer.append('\n');
		}

		// fencepost
		buffer.deleteCharAt(buffer.length() - 2);
		return buffer.toString();
	}

	/**
	 * Convert a Base64 String into a X509Certificate
	 * 
	 * @param cert
	 * @return
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static X509Certificate parseCertificate(String cert) throws CertificateException, IOException {
		if (cert == null)
			throw new IllegalArgumentException();

		return parseCertificate(Base64.getDecoder().decode(cert));
	}

	/**
	 * Convert a byte array into a X509Certificate
	 * 
	 * @param cert
	 * @return
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static X509Certificate parseCertificate(byte[] cert) throws CertificateException, IOException {
		if (cert == null)
			throw new IllegalArgumentException();

		try (InputStream in = new ByteArrayInputStream(cert)) {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
		}
	}
}
