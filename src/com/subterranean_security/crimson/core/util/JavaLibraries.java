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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.Common.Instance;

public final class JavaLibraries {
	private JavaLibraries() {
	}

	public static boolean loadTemporarily(File temp) {
		JarUtil.extract("com/subterranean_security/cinstaller/res/bin/lib.zip", temp.getAbsolutePath() + "/lib.zip");
		try {
			FileUtil.unzip(temp.getAbsolutePath() + "/lib.zip", temp.getAbsolutePath());
		} catch (IOException e2) {
			return false;
		}

		// load java libraries
		try {
			for (String lib : getRequisites(Common.instance)) {
				JarUtil.load(temp.getAbsolutePath() + "/java/" + lib + ".jar");
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

		Document doc = builder.parse(JavaLibraries.class.getClassLoader()
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