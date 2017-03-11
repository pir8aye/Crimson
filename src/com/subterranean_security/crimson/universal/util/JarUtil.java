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

	public static String getManifestValue(String attribute, File jarFile) throws IOException {
		try (JarFile jar = new JarFile(jarFile)) {
			return jar.getManifest().getMainAttributes().getValue(attribute);
		}
	}

	public static String getManifestValue(String attr) throws IOException {
		try {
			return getManifestValue(attr,
					new File(Universal.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}

		return null;

	}

	public static void loadFully(String path) throws IOException, ClassNotFoundException {
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

	public static void extract(ClassLoader cl, String res, String dest) {
		InputStream stream = cl.getResourceAsStream(res);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dest);
			byte[] buf = new byte[2048];
			int r = stream.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = stream.read(buf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void extract(String res, String dest) {
		extract(JarUtil.class.getClassLoader(), res, dest);
	}

	/**
	 * Gets the size of a jar resource by reading the entire file to memory. Can
	 * be improved by throwing away data as it is read. (Copy readResource code)
	 * 
	 * @param path
	 * @return
	 */
	public static int getResourceSize(String path) {
		try {
			return JarUtil.readResource(path).length;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Reads a resource from the instance jar
	 *
	 * @param path
	 *            path to resource
	 * @return resource as a byte[]
	 * @throws IOException
	 */
	public static byte[] readResource(String path) throws Exception {
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

	public static boolean classExists(String c) {
		try {
			Class.forName(c, false, Universal.class.getClassLoader());
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	public static void extractZip(InputStream zipFilePath, String destDirectory) throws IOException {
		if (zipFilePath == null) {
			throw new IOException();
		}
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(zipFilePath);
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}
}
