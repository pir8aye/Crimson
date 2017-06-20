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
package com.subterranean_security.crimson.universal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subterranean_security.crimson.universal.util.JarUtil;

public final class Universal {

	private Universal() {
	}

	/**
	 * Instance initialization Timestamp
	 */
	public static final Date start = new Date();

	/**
	 * The running instance's jar
	 */
	public static final File jar = discoverJar();

	/**
	 * Version Syntax: X.X.X.X with major versions being on the left and minor
	 * versions and fixes on the right
	 */
	public static final String version = discoverVersion();

	/**
	 * The build number
	 */
	public static final int build = discoverBuild();

	/**
	 * Identifies this instance
	 */
	public static final Instance instance = discoverInstance();

	public enum Instance {
		SERVER, CLIENT, VIEWER, INSTALLER, VIRIDIAN, CHARCOAL;

		public String getLabel() {
			switch (this) {
			case CLIENT:
				return "C";
			case INSTALLER:
				return "I";
			case SERVER:
				return "S";
			case VIEWER:
				return "V";
			case VIRIDIAN:
				return "Q";
			case CHARCOAL:
				return "X";
			default:
				return null;

			}
		}
	}

	private static File discoverJar() {
		try {
			return new File(Universal.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String discoverVersion() {
		try {
			return JarUtil.getManifestValue("Crimson-Version");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static int discoverBuild() {
		try {
			return Integer.parseInt(JarUtil.getManifestValue("Build-Number"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	private static Instance discoverInstance() {
		try {
			return Instance.valueOf(JarUtil.getManifestValue("Instance", jar));
		} catch (Throwable t) {
			System.out.println("Failed to read instance");
			System.exit(0);
		}

		return null;
	}

	public static void loadTemporarily(String libZip, File temp) throws IOException, SecurityException {
		JarUtil.extract(Universal.class.getResourceAsStream(libZip), temp.getAbsolutePath());

		for (String lib : getInstancePrerequisites(discoverInstance())) {
			JarUtil.load(temp.getAbsolutePath() + "/java/" + lib + ".jar");
		}

	}

	public static ArrayList<String> getInstancePrerequisites(Universal.Instance instance) {
		List<Element> elements = null;
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

	private static List<Element> readDependancyXML() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(Universal.class.getClassLoader()
				.getResourceAsStream("com/subterranean_security/crimson/universal/res/Dependancies.xml"));

		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("Lib");
		List<Element> elements = new ArrayList<Element>();
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
