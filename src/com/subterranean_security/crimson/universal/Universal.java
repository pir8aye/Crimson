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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Universal {
	private Universal() {
	}

	/**
	 * When true, debug messages will be logged and additional functionality
	 * enabled
	 */
	public static final boolean isDebug = true;

	/**
	 * When true, network data is logged
	 */
	public static final boolean isNetDebug = false;

	public enum Instance {
		SERVER, CLIENT, VIEWER, INSTALLER, VIRIDIAN;

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
			default:
				return null;

			}
		}
	}

	public static Universal.Instance discoverInstance() {

		try {
			return Instance.valueOf(JarUtil.getManifestValue("Instance",
					new File(Universal.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())));
		} catch (IOException e) {
			System.exit(0);
		} catch (URISyntaxException e) {
			System.exit(0);
		} catch (Throwable t) {
			System.exit(0);
		}

		return null;
	}

	public static void loadTemporarily(String libZip, File temp) throws IOException, SecurityException {
		JarUtil.extractZip(Universal.class.getResourceAsStream(libZip), temp.getAbsolutePath());

		for (String lib : getInstancePrerequisites(discoverInstance())) {
			JarUtil.load(temp.getAbsolutePath() + "/java/" + lib + ".jar");
		}

	}

	public static ArrayList<String> getInstancePrerequisites(Universal.Instance instance) {
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

		Document doc = builder.parse(Universal.class.getClassLoader()
				.getResourceAsStream("com/subterranean_security/crimson/universal/Dependancies.xml"));

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
