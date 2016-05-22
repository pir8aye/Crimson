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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import com.subterranean_security.cinstaller.Main;
import com.subterranean_security.crimson.client.Client;
import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Platform;
import com.subterranean_security.crimson.server.Server;
import com.subterranean_security.crimson.viewer.Viewer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.joran.spi.JoranException;

public enum CUtil {
	;
	private static final Logger log = CUtil.Logging.getLogger(CUtil.class);

	public static class Files {

		public static class Temp {
			public static File getLFile(String name) {
				File f = new File(Common.tmp.getAbsolutePath() + File.separator + name);
				f.deleteOnExit();
				return f;
			}

			public static File getGFile(String name) {
				File f = new File(Common.gtmp.getAbsolutePath() + File.separator + name);
				f.deleteOnExit();
				return f;
			}

			public static File getLFile() {
				File f = new File(Common.tmp.getAbsolutePath() + File.separator + Misc.randString(9));
				f.deleteOnExit();
				return f;
			}

			public static File getGFile() {
				File f = new File(Common.gtmp.getAbsolutePath() + File.separator + "cr_" + Misc.randString(9));
				f.deleteOnExit();
				return f;
			}

			public static File getLDir() {
				File f = new File(Common.tmp.getAbsolutePath() + File.separator + Misc.randString(9));
				f.mkdirs();
				if (!Common.isDebugMode()) {
					f.deleteOnExit();
				}

				return f;
			}

			public static File getGDir() {
				File f = new File(Common.gtmp.getAbsolutePath() + File.separator + "cr_" + Misc.randString(9));
				f.mkdirs();
				f.deleteOnExit();
				return f;
			}

			public static void clearL() {
				for (File f : Common.tmp.listFiles()) {
					// delete it
					if (!delete(f)) {
						log.error("Could not delete temporary file: " + f.getAbsolutePath());
					}
				}

			}

			public static void clearG() {
				for (File f : Common.tmp.listFiles()) {
					if (f.getName().startsWith("temp_")) {
						// delete it
						if (!delete(f)) {
							log.error("Could not delete temporary file: " + f.getAbsolutePath());
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
		public static boolean delete(File f) {

			if (f.exists()) {
				File[] files = f.listFiles();
				if (null != files) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							delete(files[i]);
						} else {
							files[i].delete();
						}
					}
				}
			}
			return (f.delete());
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

		public static String getStack(Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}

		public static void runBackgroundCommand(String c) throws IOException {
			String command = "";
			switch (Platform.osFamily) {
			case SOL:
			case BSD:
			case LIN:
				command = "nohup " + c;
				break;
			case OSX:
				break;

			case WIN:
				command = "cmd /c start cmd /k \"" + c + "\"";
				break;
			default:
				break;

			}
			log.debug("Running background command: \"" + command + "\"");
			Runtime.getRuntime().exec(command);
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
				return getManifestAttr(attr,
						new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
			} catch (Throwable e1) {
			}
			log.error("Failed to get manifest attribute '%s' from default jar", attr);
			return null;

		}

		public static String getLibManifestAttr(String attr, String lib) throws IOException {
			if (!lib.endsWith(".jar")) {
				lib += ".jar";
			}
			return getManifestAttr(attr, new File(Common.base.getAbsolutePath() + "/lib/java/" + lib));
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

		public static HashMap<String, String> resolve(String ip) throws IOException, XMLStreamException {
			log.debug("Resolving location for: {}", ip);
			HashMap<String, String> info = new HashMap<String, String>();
			XMLStreamReader reader = XMLInputFactory.newInstance()
					.createXMLStreamReader(new URL("https://freegeoip.lwan.ws/xml/" + ip).openStream());

			while (reader.hasNext()) {
				switch (reader.next()) {
				case XMLStreamConstants.END_ELEMENT:
					info.put(reader.getLocalName().toLowerCase(), reader.getText().trim());

				}
			}
			return info;
		}
	}

	public static class Validation {

		private static final Pattern pDNS = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");
		private static final Pattern pUSER = Pattern.compile("");// invalid
																	// username
																	// chars
		private static final Pattern pIP = Pattern
				.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
						+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		public static boolean username(String user) {
			if (user.length() < 4 || user.length() > 20) {
				return false;
			}

			return !pUSER.matcher(user).matches();
		}

		public static boolean password(String password) {
			if (password.length() < 4 || password.length() > 32) {
				return false;
			}

			return true;
		}

		public static boolean password(char[] password) {
			return password(new String(password));
		}

		public static boolean dns(String dns) {
			return pDNS.matcher(dns).find();
		}

		public static boolean ip(String ip) {
			return pIP.matcher(ip).matches();
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
			return false;
		}

	}

	public static class Versions {

		public static String[] getRequisites() {
			ArrayList<Element> elements = null;
			try {
				elements = readDependancyXML(new FileInputStream(new File("Dependancies.xml")));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String[] a = new String[elements.size()];
			for (int i = 0; i < a.length; i++) {
				a[i] = elements.get(i).getElementsByTagName("Requisites").item(0).getTextContent();
			}
			return a;
		}

		private static ArrayList<Element> readDependancyXML(InputStream xml) throws Exception {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document doc = builder.parse(xml);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Lib");
			ArrayList<Element> elements = new ArrayList<Element>();
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					elements.add(eElement);
					// System.out.println("CID: " +
					// eElement.getAttribute("CID"));
					// System.out
					// .println("EVersion: " +
					// eElement.getElementsByTagName("EVersion").item(0).getTextContent());
					// System.out.println(
					// "Requisites: " +
					// eElement.getElementsByTagName("Requisites").item(0).getTextContent());
				}
			}

			return elements;
		}

	}

	public static class Logging {

		static {
			// TODO find a better way to exclude netty
			ch.qos.logback.classic.Logger netty = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("io.netty");
			netty.setLevel(Level.ERROR);
		}

		public static Logger getLogger(Class c) {
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			PatternLayoutEncoder ple = new PatternLayoutEncoder();

			// ple.setPattern("[%date{yyyy-MM-dd HH:mm:ss}][%level{1}][%thread]
			// %logger{10} %msg%n");
			ple.setPattern("[%date{yyyy-MM-dd HH:mm:ss}][%level{1}][%logger{0}] %msg%n");
			ple.setContext(lc);
			ple.start();

			ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(c);
			ConsoleAppender<ILoggingEvent> stdout = new ConsoleAppender<ILoggingEvent>();
			stdout.setEncoder(ple);
			stdout.setContext(lc);
			stdout.setName("com.subterranean_security");
			stdout.start();
			logger.addAppender(stdout);
			logger.setLevel(Common.isDebugMode() ? Level.DEBUG : Level.INFO);
			logger.setAdditive(false);

			return logger;
		}

		public static void tmp() {
			FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
			/*
			 * fileAppender.setFile(file); fileAppender.setEncoder(ple);
			 * fileAppender.setContext(lc); fileAppender.start();
			 * logger.addAppender(fileAppender);
			 */
		}
	}

}
