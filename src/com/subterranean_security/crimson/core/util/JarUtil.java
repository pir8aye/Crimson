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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.viewer.Viewer;

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
					new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}

		try {
			return getManifestValue(attr,
					new File(Viewer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}

		try {
			return getManifestValue(attr,
					new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}

		try {
			return getManifestValue(attr, new File(com.subterranean_security.cinstaller.Main.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}
		try {
			return getManifestValue(attr, new File(com.subterranean_security.viridian.Main.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath()));
		} catch (Throwable e1) {
		}
		return null;

	}

	public static String getLibraryManifestValue(String attr, String lib) throws IOException {
		if (!lib.endsWith(".jar")) {
			lib += ".jar";
		}
		return getManifestValue(attr, new File(Common.Directories.base.getAbsolutePath() + "/lib/java/" + lib));
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

	public static void load(String path) throws Exception {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { new File(path).toURI().toURL() });
	}

	public static void extract(String res, String dest) {
		InputStream stream = JarUtil.class.getClassLoader().getResourceAsStream(res);

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

	public static boolean classExists(String c) {
		try {
			Class.forName(c, false, JarUtil.class.getClassLoader());
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	public static URL[] getClassPath() {
		return ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
	}
}
