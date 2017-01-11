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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.LogManager;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JPasswordField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;
import com.subterranean_security.crimson.core.platform.Platform;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.viewer.Viewer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.handler.logging.LogLevel;

public enum CUtil {
	;
	private static final Logger log = LoggerFactory.getLogger(CUtil.class);

	public static class Files {

		public static class Temp {
			public static final String prefix = "crimson_temp_";

			public static File getFile(String name) {
				File f = new File(Common.Directories.tmp.getAbsolutePath() + File.separator + name);
				f.deleteOnExit();
				return f;
			}

			public static File getFile() {
				File f = new File(
						Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + Misc.randString(9));
				f.deleteOnExit();
				return f;
			}

			public static File getDir() {
				File f = new File(
						Common.Directories.tmp.getAbsolutePath() + File.separator + prefix + Misc.randString(9));
				f.mkdirs();
				f.deleteOnExit();

				return f;
			}

			public static void clear() {
				for (File f : Common.Directories.tmp.listFiles()) {
					if (f.getName().startsWith(prefix)) {
						// delete it
						if (!delete(f)) {
							log.warn("Could not delete temporary file: " + f.getAbsolutePath());
						}
					}
				}
			}
		}

		/**
		 * Copies sourceFile to destFile
		 *
		 * @param sourceFile
		 * @param destFile
		 * @throws IOException
		 */
		public static void copyFile(File sourceFile, File destFile) throws IOException {
			FileInputStream fis = new FileInputStream(sourceFile);
			copyFile(fis.getChannel(), destFile);
			fis.close();
		}

		// test this method
		public static void copyFile(InputStream sourceFile, File destFile) throws IOException {

			copyFile(((FileInputStream) sourceFile).getChannel(), destFile);
		}

		private static void copyFile(FileChannel source, File destFile) throws IOException {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			FileChannel destination = null;

			try (FileOutputStream fos = new FileOutputStream(destFile)) {
				destination = fos.getChannel();
				destination.transferFrom(source, 0, source.size());
			} finally {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			}
		}

		public static String getHash(String filename, String type) {
			MessageDigest complete = null;
			try {
				InputStream fis = new FileInputStream(filename);
				byte[] buffer = new byte[1024];
				complete = MessageDigest.getInstance(type);
				int numRead;
				do {
					numRead = fis.read(buffer);
					if (numRead > 0) {
						complete.update(buffer, 0, numRead);
					}
				} while (numRead != -1);
				fis.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			byte[] b = complete.digest();

			StringBuffer result = new StringBuffer();

			for (int i = 0; i < b.length; i++) {
				result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
			}
			return result.toString();
		}

		/**
		 * Recursively delete a file or directory
		 *
		 * @param f
		 *            the file or directory to delete
		 * @return true on success false on failure
		 */
		public static boolean delete(String f) {
			return delete(new File(f));
		}

		/**
		 * Recursively delete a file or directory
		 *
		 * @param f
		 *            the file or directory to delete
		 * @return true on success false on failure
		 */
		public static boolean delete(File f) {
			return delete(f, false);
		}

		/**
		 * Recursively delete a file or directory
		 *
		 * @param f
		 *            the file or directory to delete
		 * @return true on success false on failure
		 */
		public static boolean delete(File f, boolean overwrite) {

			if (f.exists()) {
				File[] files = f.listFiles();
				if (null != files) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							delete(files[i]);
						} else {
							if (overwrite) {
								overwrite(files[i]);
							}
							files[i].delete();
						}
					}
				}
			}
			return (f.delete());
		}

		public static boolean overwrite(File f) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "w");
				for (long i = 0; i < raf.length(); i++) {
					raf.writeByte(0);// TODO random
				}
				raf.close();
				return true;
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		/**
		 * Writes the byte array at the given location
		 * 
		 * @param b
		 * @param target
		 * @throws IOException
		 */
		public static void writeFile(byte[] b, File target) throws IOException {

			FileOutputStream fileOuputStream = new FileOutputStream(target);
			fileOuputStream.write(b);
			fileOuputStream.close();

		}

		public static byte[] readFile(File f) throws IOException {

			java.nio.file.Path path = Paths.get(f.getAbsolutePath());
			return java.nio.file.Files.readAllBytes(path);

		}

		public static ArrayList<String> readFileLines(File f) throws IOException {
			ArrayList<String> a = new ArrayList<String>();
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = br.readLine()) != null) {
					a.add(line);
				}
			}
			return a;
		}

		public static String readFileString(File f) throws IOException {
			StringBuffer sb = new StringBuffer();
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
			}
			return sb.toString();
		}

		/**
		 * Gets a byte[] from a file in the main jar
		 *
		 * @param path
		 *            path to resource
		 * @return resource
		 * @throws IOException
		 */
		public static byte[] readResource(String path) throws IOException {
			InputStream is = new BufferedInputStream(CUtil.class.getResourceAsStream(path));
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			for (int b; (b = is.read()) != -1;) {
				out.write(b);
			}

			return out.toByteArray();

		}

		public static int getResourceSize(String path) {
			try {
				return readResource(path).length;
			} catch (IOException e) {
				return 0;
			}
		}

		/**
		 * Extracts a zip file specified by the zipFilePath to a directory
		 * specified by destDirectory (will be created if does not exists)
		 * 
		 * @param zipFilePath
		 * @param destDirectory
		 * @throws IOException
		 */
		public static void unzip(String zipFilePath, String destDirectory) throws IOException {
			File destDir = new File(destDirectory);
			if (!destDir.exists()) {
				destDir.mkdir();
			}
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
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

		/**
		 * Extracts an entry from a zip file specified by the zipFilePath to a
		 * directory specified by destDirectory (will be created if does not
		 * exists)
		 * 
		 * @param zipFilePath
		 * @param destDirectory
		 * @throws IOException
		 */
		public static void unzipFile(String zipFilePath, String targetEntry, String destDirectory) throws IOException {
			File destDir = new File(destDirectory);
			if (!destDir.exists()) {
				destDir.mkdir();
			}
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
			ZipEntry entry = zipIn.getNextEntry();
			// iterates over entries in the zip file
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory() && entry.getName().equals(targetEntry)) {
					// if the entry is a file, extracts it
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
					byte[] bytesIn = new byte[4096];
					int read = 0;
					while ((read = zipIn.read(bytesIn)) != -1) {
						bos.write(bytesIn, 0, read);
					}
					bos.close();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		}

		public static void extract(String res, String dest) {
			InputStream stream = CUtil.class.getClassLoader().getResourceAsStream(res);

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

		public static boolean isValidInstallPath(String s) {
			File f = new File(s);
			if (f.exists()) {
				f = new File(f.getAbsolutePath() + "/testDirectory");
				if (f.mkdir()) {
					f.delete();
					return true;
				} else {
					return false;
				}
			} else {
				return isValidInstallPath(f.getParent());
			}
		}

		public static void substitute(File f, String target, String replacement) {
			try {
				FileReader fr = new FileReader(f);
				String s;
				String totalStr = "";
				try (BufferedReader br = new BufferedReader(fr)) {

					while ((s = br.readLine()) != null) {
						totalStr += s;
					}
					totalStr = totalStr.replaceAll(target, replacement);
					FileWriter fw = new FileWriter(f);
					fw.write(totalStr);
					fw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public static void loadJar(String path) throws Exception {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { new File(path).toURI().toURL() });
		}

	}

	public static class Misc {

		private static Random rand = new Random();
		public static final String[] BYTES = { " B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB" };

		public static final String[] BYTES_PER_SECOND = { " B/s", " KB/s", " MB/s", " GB/s", " TB/s", " PB/s", " EB/s",
				" ZB/s", " YB/s" };

		public static String familiarize(long size, String[] units) {
			int measureQuantity = 1024;

			if (size <= 0) {
				return null;
			}

			if (size < measureQuantity) {
				return size + units[0];
			}

			int i = 1;
			double d = size;
			while ((d = d / measureQuantity) > (measureQuantity - 1)) {
				i++;
			}

			long l = (long) (d * 100);
			d = (double) l / 100;

			if (i < units.length) {
				return d + units[i];
			}
			return String.valueOf(size);
		}

		public static long defamiliarize(String s, String[] units) {
			for (int i = 0; i < units.length; i++) {
				if (s.toLowerCase().endsWith(units[i].toLowerCase())) {
					return (long) (Double.parseDouble(s.substring(0, s.indexOf(' '))) * (Math.pow(1024, i)));
				}
			}
			return Long.parseLong(s.substring(0, s.indexOf(' ')));
		}

		public static boolean isSameDay(Date d1, Date d2) {
			SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
			return formatter.format(d1).equals(formatter.format(d2));
		}

		public static String getStack(Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}

		public static int rand(int lower, int upper) {
			return rand.nextInt(upper - lower + 1) + lower;
		}

		public static long rand(long lower, long upper) {
			return nextLong(upper - lower + 1) + lower;
		}

		public static int rand() {
			return rand.nextInt();
		}

		private static long nextLong(long n) {
			// error checking and 2^x checking removed for simplicity.
			long bits, val;
			do {
				bits = (rand.nextLong() << 1) >>> 1;
				val = bits % n;
			} while (bits - val + (n - 1) < 0L);
			return val;
		}

		/**
		 * Generates a random ASCII string of given length
		 *
		 * @param characters
		 *            length of string
		 * @return random string
		 */
		public static String randString(int characters) {
			StringBuffer filename = new StringBuffer();
			for (int i = 0; i < characters; i++) {
				// append a random character
				char c = (char) (new Random().nextInt(25) + 97);
				filename.append(c);
			}

			return filename.toString();
		}

		public static boolean findClass(String c) {
			try {
				Class.forName(c, false, CUtil.Misc.class.getClassLoader());
			} catch (Throwable t) {
				return false;
			}
			return true;
		}

		public static String getManifestAttr(String attr, File f) throws IOException {

			try (JarFile jar = new JarFile(f)) {
				return jar.getManifest().getMainAttributes().getValue(attr);
			}
		}

		public static String getManifestAttr(String attr) throws IOException {
			try {
				return getManifestAttr(attr,
						new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}

			try {
				return getManifestAttr(attr,
						new File(Viewer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}

			try {
				return getManifestAttr(attr,
						new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}

			try {
				return getManifestAttr(attr, new File(com.subterranean_security.cinstaller.Main.class
						.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}
			try {
				return getManifestAttr(attr, new File(com.subterranean_security.viridian.Main.class
						.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}
			log.error("Failed to get manifest attribute '{}' from default jar", attr);
			return null;

		}

		public static String getLibManifestAttr(String attr, String lib) throws IOException {
			if (!lib.endsWith(".jar")) {
				lib += ".jar";
			}
			return getManifestAttr(attr, new File(Common.Directories.base.getAbsolutePath() + "/lib/java/" + lib));
		}

		public static int uptime() {
			Date now = new Date();
			return (int) (now.getTime() - Common.start.getTime()) / 1000;
		}

		public static URL[] getClassPath() {

			return ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
		}

		public static String datediff(Date d1, Date d2) {
			long seconds = 0;
			if (d1.getTime() > d2.getTime()) {
				seconds = (d1.getTime() - d2.getTime()) / 1000;
			} else if (d1.getTime() < d2.getTime()) {
				seconds = (d2.getTime() - d1.getTime()) / 1000;
			}

			long months = seconds / 2592000;
			seconds -= months * 2592000;
			long days = seconds / 86400;
			seconds -= days * 86400;
			long hours = seconds / 3600;
			seconds -= hours * 3600;
			long minutes = seconds / 60;
			seconds -= minutes * 60;

			return "" + (months == 0 ? "" : months + " months ") + (days == 0 ? "" : days + " days ")
					+ (hours == 0 ? "" : hours + " hours ") + (minutes == 0 ? "" : minutes + " minutes ") + seconds
					+ " seconds";
		}

		public static String getHWID() {
			// TODO Auto-generated method stub
			return null;
		}

		public static byte[] compress(byte[] target) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				OutputStream out = new DeflaterOutputStream(baos);
				out.write(target);
				out.close();
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			return baos.toByteArray();
		}

		public static byte[] decompress(byte[] bytes) {
			InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[8192];
				int len;
				while ((len = in.read(buffer)) > 0)
					baos.write(buffer, 0, len);
				return baos.toByteArray();
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		}

		public static void loadJar(String path) throws IOException {
			JarFile jarFile = new JarFile(path);
			Enumeration e = jarFile.entries();

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
				try {
					Class c = cl.loadClass(className);
				} catch (ClassNotFoundException e1) {

				}

			}
			jarFile.close();
		}

		/**
		 * Version format: X.X.X.X[-xxxx]
		 * 
		 * @param v1
		 * @param v2
		 * @return true if v1 is newer than v2
		 */
		// TODO move
		public static boolean isNewerVersion(String v1, String v2) {
			String[] pv1 = v1.split("-");

			String[] pv2 = v2.split("-");

			if (pv1.length == 2 && pv2.length == 2) {
				// simply compare build numbers
				return Integer.parseInt(pv1[1]) > Integer.parseInt(pv2[1]);
			}
			String[] ppv1 = pv1[0].split("\\.");
			String[] ppv2 = pv2[0].split("\\.");

			for (int i = 0; i < 4; i++) {
				if (Integer.parseInt(ppv1[i]) > Integer.parseInt(ppv2[i])) {
					return true;
				} else if (Integer.parseInt(ppv1[i]) < Integer.parseInt(ppv2[i])) {
					return false;
				}
			}

			return false;
		}

		public static double average(ArrayList<Double> list) {
			double sum = 0;
			for (double d : list) {
				sum += d;
			}
			return sum / list.size();
		}

		public static void clearChar(char[] a) {
			for (int i = 0; i < a.length; i++) {
				a[i] = (char) rand.nextInt();
			}
		}

		public static void clearByte(byte[] a) {
			for (int i = 0; i < a.length; i++) {
				a[i] = (byte) rand.nextInt();
			}
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

	public static class Network {

		/**
		 * Get the default internal IP address
		 * 
		 * @return
		 */
		public static String getIIP() {
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				return "Unknown";
			}
		}

		/**
		 * Gets the external IP by querying checkip
		 * 
		 * @return
		 */
		public static String getEIP() {

			// determine external IP address
			BufferedReader in = null;
			String extip = "";
			try {

				in = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()));

				extip = in.readLine();

			} catch (IOException e) {
				// may not be connected to the internet
				extip = "Unknown";
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						extip = null;
					}
				}
			}

			if (extip == null) {
				extip = "Unknown";
			}
			return extip;

		}

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

	public static class Location {
		public static float distance(float lat1, float lng1, float lat2, float lng2) {
			double dLat = Math.toRadians(lat2 - lat1);
			double dLng = Math.toRadians(lng2 - lng1);
			double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)) + (Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2));
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double dist = 3958.75 * c;

			int meterConversion = 1609;

			return (float) (dist * meterConversion);
		}

		private static final int connectionTimeout = 800;
		private static final int readTimeout = 800;

		public static HashMap<String, String> resolve(String ip) throws IOException, XMLStreamException {
			log.debug("Resolving location for: {}", ip);
			HashMap<String, String> info = new HashMap<String, String>();

			URLConnection connection = new URL("https://freegeoip.lwan.ws/xml/" + ip).openConnection();
			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(readTimeout);
			try (InputStream in = connection.getInputStream()) {
				XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);

				String tag = "";
				String value = "";
				while (reader.hasNext()) {
					switch (reader.next()) {
					case XMLStreamConstants.START_ELEMENT:
						tag = reader.getLocalName().toLowerCase();
						break;
					case XMLStreamConstants.CDATA:
					case XMLStreamConstants.CHARACTERS:
						if (!tag.equals("response")) {
							value = reader.getText();
						}

						break;
					case XMLStreamConstants.END_ELEMENT:
						if (!tag.equals("response")) {
							info.put(tag, value.trim());
						}
						break;
					}
				}
				reader.close();
			}

			return info;
		}
	}

	public static class Validation {

		private static final Pattern valid_dns = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");

		// TODO define valid username characters
		private static final Pattern valid_user = Pattern.compile("");

		private static final Pattern valid_ipv4 = Pattern
				.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
						+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		private static final Pattern valid_private_ipv4 = Pattern
				.compile("(^127\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

		private static final Pattern valid_email = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

		public static boolean username(String user) {
			if (user.length() < 4 || user.length() > 60) {
				return false;
			}

			return !valid_user.matcher(user).matches();
		}

		public static boolean groupname(String group) {
			return true;
		}

		public static boolean password(JPasswordField field) {
			char[] password = field.getPassword();
			boolean outcome = true;
			if (password.length < 4 || password.length > 64) {
				outcome = false;
			}
			CUtil.Misc.clearChar(password);
			return outcome;
		}

		public static boolean dns(String dns) {
			if (dns == null) {
				return false;
			}
			return valid_dns.matcher(dns).find();
		}

		public static boolean ip(String ip) {
			if (ip == null) {
				return false;
			}
			return valid_ipv4.matcher(ip).matches();
		}

		public static boolean privateIP(String ip) {
			return valid_private_ipv4.matcher(ip).find();
		}

		public static boolean port(String port) {
			try {
				int p = Integer.parseInt(port);
				return (p > 0 && p < 65536);
			} catch (Throwable t) {
				return false;
			}
		}

		public static boolean path(String path) {
			try {
				new File(path).getCanonicalPath();
			} catch (IOException e) {
				return false;
			}

			return true;
		}

		public static boolean serial(String key) {
			if (key.length() != 16) {
				return false;
			}

			if (!key.matches("^[A-Z0-9]*$")) {
				return false;
			}

			return true;
		}

		public static boolean email(String email) {
			return valid_email.matcher(email).matches();
		}

		public static boolean flushValue(String value) {
			try {
				return (Integer.parseInt(value) > 0);
			} catch (NumberFormatException e) {
				return false;
			}
		}

	}

	public static class JavaLibraries {

		public static boolean loadTemporarily(File temp) {
			CUtil.Files.extract("com/subterranean_security/cinstaller/res/bin/lib.zip",
					temp.getAbsolutePath() + "/lib.zip");
			try {
				CUtil.Files.unzip(temp.getAbsolutePath() + "/lib.zip", temp.getAbsolutePath());
			} catch (IOException e2) {
				return false;
			}

			// load java libraries
			try {
				for (String lib : getRequisites(Common.instance)) {
					CUtil.Files.loadJar(temp.getAbsolutePath() + "/java/" + lib + ".jar");
				}

			} catch (Exception e1) {
				return false;
			}
			return true;
		}

		public static ArrayList<String> getRequisites(Instance instance) {
			ArrayList<Element> elements = null;
			try {
				elements = readDependancyXML();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			ArrayList<String> req = new ArrayList<String>();
			for (Element e : elements) {

				if (e.getElementsByTagName("Requisites").item(0).getTextContent().contains(instance.getLabel())) {
					req.add(e.getAttribute("CID"));
				}
			}
			return req;
		}

		private static ArrayList<Element> readDependancyXML() throws Exception {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document doc = builder.parse(CUtil.class.getClassLoader()
					.getResourceAsStream("com/subterranean_security/crimson/core/res/xml/Dependancies.xml"));

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Lib");
			ArrayList<Element> elements = new ArrayList<Element>();
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					elements.add(eElement);
				}
			}

			return elements;
		}

	}

	public static class UnitTranslator {

		private static double parseNumeric(String s) throws NumberFormatException, IndexOutOfBoundsException {
			return Double.parseDouble(s.substring(0, s.indexOf(' ')));
		}

		private static long parseUnitMultiplier(String s) {
			switch (s.substring(1 + s.lastIndexOf(' ')).toLowerCase()) {
			case "b":
			case "b/s":
			case "hz":
				return 1L;

			case "kb":
			case "kb/s":
			case "khz":
				return 1000L;
			case "kib":
			case "kib/s":
				return 1024L;

			case "mb":
			case "mb/s":
			case "mhz":
				return 1000000L;
			case "mib":
			case "mib/s":
				return 1048576L;

			case "gb":
			case "gb/s":
			case "ghz":
				return 1000000000L;
			case "gib":
			case "gib/s":
				return 1073741824L;

			case "tb":
			case "tb/s":
			case "thz":
				return 1000000000000L;
			case "tib":
			case "tib/s":
				return 1099511627776L;

			}
			return 0;
		}

		public static String translateNicOutput(long l) {
			if (l < 1024) {
				return String.format("%.2f  B", l / 1.0);
			} else if (l < 1048576) {
				return String.format("%.2f KB", l / 1024.0);
			} else if (l < 1073741824) {
				return String.format("%.2f MB", l / (1048576.0));
			} else if (l < 1099511627776L) {
				return String.format("%.2f GB", l / (1073741824.0));
			} else {
				return String.format("%.2f TB", l / (1099511627776L));
			}
		}

		public static double nicSpeed(String l) {
			double d = 0;

			// parse numeric
			try {
				d = parseNumeric(l);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// parse unit
			long multiplier = 0;

			try {
				multiplier = parseUnitMultiplier(l);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return d * multiplier;
		}

		public static String nicSpeed(double l) {
			if (l < 1024) {
				return String.format("%.2f   B/s", l / 1.0);
			} else if (l < 1048576) {
				return String.format("%.2f KiB/s", l / 1024.0);
			} else if (l < 1073741824) {
				return String.format("%.2f MiB/s", l / (1048576.0));
			} else {
				return String.format("%.2f GiB/s", l / (1073741824.0));
			}
		}

		public static String translateDispMemSize(int l) {
			if (l < 1024) {
				return String.format("%.2f  B", l / 1.0);
			} else if (l < 1048576) {
				return String.format("%.2f KB", l / 1024.0);
			} else if (l < 1073741824) {
				return String.format("%.2f MB", l / (1048576.0));
			} else {
				return String.format("%.2f GB", l / (1073741824.0));
			}
		}

		public static String translateMemSize(long l) {
			if (l < 1024 * 1024) {
				return String.format("%.2f KB", l / 1024.0);
			} else if (l < 1024 * 1024 * 1024) {
				return String.format("%.2f MB", l / (1024.0 * 1024.0));
			} else {
				return String.format("%.2f GB", l / (1024.0 * 1024.0 * 1024.0));
			}
		}

		public static String translateCacheSize(long l) {
			if (l < 1024 * 1024) {
				return String.format("%f KB", l / 1024.0);
			} else {
				return String.format("%f MB", l / (1024.0 * 1024.0));
			}
		}

		public static String translateCpuFrequency(int l) {
			if (l < 1000) {
				return String.format("%.2f MHz", l);
			} else {
				return String.format("%.2f GHz", l / 1000.0);
			}
		}

	}

	public static class Logging {

		private static final boolean netlevel = new File("/netdebug.txt").exists();

		public static void configure() {
			File config = new File(Common.Directories.varLog.getAbsolutePath() + "/logback-"
					+ Common.instance.toString().toLowerCase() + ".xml");
			if (!config.exists()) {
				CUtil.Files.extract("com/subterranean_security/crimson/core/res/xml/logback.xml",
						config.getAbsolutePath());
				CUtil.Files.substitute(config, "%LEVEL%", Common.isDebugMode() ? LogLevel.DEBUG.toString().toLowerCase()
						: LogLevel.INFO.toString().toLowerCase());
				CUtil.Files.substitute(config, "%LOGDIR%", config.getParent().replaceAll("\\\\", "/"));
				CUtil.Files.substitute(config, "%INSTANCE%", Common.instance.toString().toLowerCase());
				CUtil.Files.substitute(config, "%NETLEVEL%",
						netlevel ? LogLevel.DEBUG.toString().toLowerCase() : LogLevel.ERROR.toString().toLowerCase());

			}

			LogManager.getLogManager().reset();
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);

			context.reset();
			try {
				configurator.doConfigure(config.getAbsolutePath());
			} catch (JoranException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
