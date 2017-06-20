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
package com.subterranean_security.crimson.universal.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.subterranean_security.crimson.universal.Universal;

public final class JarUtil {
	private JarUtil() {
	}

	/**
	 * Retrieve the value of a manifest attribute from the specified jar
	 * 
	 * @param attribute
	 *            The attribute to query
	 * @param jarFile
	 *            The jar file to query
	 * @return The attribute's value
	 * @throws IOException
	 */
	public static String getManifestValue(String attribute, File jarFile) throws IOException {
		if (attribute == null || jarFile == null)
			throw new IllegalArgumentException();

		try (JarFile jar = new JarFile(jarFile)) {
			if (jar.getManifest() == null)
				throw new IOException("Manifest not found");

			return jar.getManifest().getMainAttributes().getValue(attribute);
		}
	}

	/**
	 * Retrieve the value of a manifest attribute from the instance jar.
	 * 
	 * @param attribute
	 *            The attribute to query
	 * @return The attribute's value
	 * @throws IOException
	 */
	public static String getManifestValue(String attribute) throws IOException {
		if (attribute == null)
			throw new IllegalArgumentException();

		return getManifestValue(attribute, Universal.jar);
	}

	/**
	 * Calculate the size of a resource by reading it entirely.
	 * 
	 * @param path
	 *            Location of resource in jar
	 * @return The size of the target resource in bytes
	 */
	public static long getResourceSize(String path) throws IOException {
		long size = 0;
		try (InputStream is = new BufferedInputStream(JarUtil.class.getResourceAsStream(path))) {
			byte[] buf = new byte[4096];
			int r = is.read(buf);
			while (r != -1) {
				size += r;
				r = is.read(buf);
			}
		}

		return size;
	}

	/**
	 * Determine if the jar contains a specific resource
	 * 
	 * @param path
	 *            Location of resource
	 * @return True if the specified resource exists in the jar
	 */
	public static boolean containsResource(String path) {
		if (path == null)
			throw new IllegalArgumentException();

		return JarUtil.class.getResourceAsStream(path) != null;
	}

	/**
	 * Read a small resource from the instance's jar.
	 *
	 * @param path
	 *            Location of resource
	 * @return resource as a byte[]
	 * @throws IOException
	 */
	public static byte[] readResource(String path) throws IOException {
		if (path == null)
			throw new IllegalArgumentException();

		try (InputStream is = new BufferedInputStream(JarUtil.class.getResourceAsStream(path))) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			for (int b; (b = is.read()) != -1;) {
				out.write(b);
			}

			return out.toByteArray();
		}
	}

	public static URL[] getClassPath() {
		return ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
	}

	/**
	 * Test if the specified class exists
	 * 
	 * @param c
	 * @return True if the class exists
	 */
	public static boolean classExists(String c) {
		try {
			Class.forName(c, false, Universal.class.getClassLoader());
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * Extract a resource
	 * 
	 * @param cl
	 *            The target classloader
	 * @param res
	 *            The target resource
	 * @param dest
	 *            The target destination
	 * @throws IOException
	 */
	public static void extract(ClassLoader cl, String res, String dest) throws IOException {
		if (cl == null)
			throw new IllegalArgumentException();
		if (res == null)
			throw new IllegalArgumentException();
		if (dest == null)
			throw new IllegalArgumentException();

		try (InputStream stream = cl.getResourceAsStream(res); FileOutputStream fos = new FileOutputStream(dest)) {
			byte[] buf = new byte[2048];
			int r = stream.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = stream.read(buf);
			}
		}

	}

	/**
	 * Extract a resource from the instance jar
	 * 
	 * @param res
	 *            The target resource
	 * @param dest
	 *            The target destination
	 * @throws IOException
	 */
	public static void extract(String res, String dest) throws IOException {
		extract(JarUtil.class.getClassLoader(), res, dest);
	}

	/**
	 * Extract an entire jar/zip file before the zip library has loaded.
	 * 
	 * @param zipFilePath
	 *            The inputstream of a zip file
	 * @param destDirectory
	 *            The extraction target
	 * @throws IOException
	 */
	public static void extract(InputStream zipFilePath, String destDirectory) throws IOException {
		if (zipFilePath == null)
			throw new IllegalArgumentException();
		if (destDirectory == null)
			throw new IllegalArgumentException();

		File destDir = new File(destDirectory);
		if (!destDir.exists() && !destDir.mkdirs()) {
			throw new IOException("Failed to make directory: " + destDir.getAbsolutePath());
		}

		try (ZipInputStream zipIn = new ZipInputStream(zipFilePath)) {
			ZipEntry entry = zipIn.getNextEntry();
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = destDir.getAbsolutePath() + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					// if the entry is a file, extracts it
					try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
						byte[] bytesIn = new byte[4096];
						int read = 0;
						while ((read = zipIn.read(bytesIn)) != -1) {
							bos.write(bytesIn, 0, read);
						}
					}
				} else {
					new File(filePath).mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
		}
	}

	public static void loadFully(String path) throws IOException, ClassNotFoundException {
		if (path == null)
			throw new IllegalArgumentException();

		try (JarFile jarFile = new JarFile(path)) {
			Enumeration<JarEntry> e = jarFile.entries();

			URL[] urls = { new URL("jar:file:" + path + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);

			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				if (je.isDirectory() || !je.getName().endsWith(".class")) {
					continue;
				}
				// -6 because of .class
				String className = je.getName().substring(0, je.getName().length() - 6);
				className = className.replace('/', '.');

				Class c = cl.loadClass(className);

			}
		}
	}

	public static void load(String path) throws SecurityException, FileNotFoundException {
		if (path == null)
			throw new IllegalArgumentException();

		File f = new File(path);
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		try {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { f.toURI().toURL() });
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new SecurityException();
		} catch (MalformedURLException e) {
			throw new FileNotFoundException();
		}
	}
}
