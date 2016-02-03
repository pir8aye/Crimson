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
import java.util.Date;

import org.zeroturnaround.zip.ZipUtil;

import com.subterranean_security.crimson.core.Common;
import com.subterranean_security.crimson.core.proto.msg.Gen.ClientConfig;
import com.subterranean_security.crimson.core.proto.msg.Gen.GenReport;
import com.subterranean_security.crimson.core.storage.ViewerDB;
import com.subterranean_security.crimson.core.util.CUtil;

public class Generator {

	private File temp = CUtil.Files.Temp.getLDir();
	private GenReport.Builder report = GenReport.newBuilder();

	public GenReport getReport() {
		return report.build();
	}

	public Generator(ClientConfig config) throws Exception {

		// store the group
		ServerStore.Groups.groups.add(config.getGroup());

		String output = config.getOutputType().toLowerCase();
		if (output.endsWith("(.jar)")) {

			report = genJar(config).setOutputType("Runnable Java Archive");

		} else if (output.endsWith("(.exe)")) {

		} else if (output.endsWith("(.sh)")) {

		} else {

		}
	}

	public byte[] getResult() throws IOException {

		return CUtil.Files.readFile(new File(temp.getAbsolutePath() + "/installer.jar"));
	}

	private GenReport.Builder genJar(ClientConfig ic) throws IOException {
		GenReport.Builder gReport = GenReport.newBuilder();
		Date start = new Date();
		File clientJar = new File(temp.getAbsolutePath() + "/installer.jar");
		File clientDB = new File(temp.getAbsolutePath() + "/client.db");
		File internal = new File(temp.getAbsolutePath() + "/internal.txt");

		// create a database for the client
		try {
			ViewerDB database = new ViewerDB(clientDB);
			database.storeObject("generation_date", start);
			database.storeObject("reconnect_period", ic.getReconnectPeriod());
			database.storeObject("group", ic.getGroup());
			database.storeObject("nts", ic.getTargetList());
			database.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// copy the client jar out
		CUtil.Files.extract("com/subterranean_security/crimson/server/res/bin/client.jar", clientJar.getAbsolutePath());

		// add client database to jar
		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/res/bin/client.db", clientDB);

		// add libraries to jar // TODO only copy needed libs
		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/res/bin/lib.zip",
				new File(Common.base.getAbsolutePath() + "/lib/lib.zip"));

		// create and add the internal.txt
		internal.createNewFile();
		PrintWriter pw = new PrintWriter(internal);
		pw.println("base_win<>" + ic.getPathWin());
		pw.println("base_lin<>" + ic.getPathLin());
		pw.println("base_osx<>" + ic.getPathOsx());
		pw.println("base_sol<>" + ic.getPathSol());
		pw.println("base_bsd<>" + ic.getPathBsd());
		pw.close();

		ZipUtil.addEntry(clientJar, "com/subterranean_security/crimson/client/internal.txt", internal);

		// delete jars
		// CUtil.Files.delete(clientJar);

		gReport.setHashMd5(CUtil.Files.getHash(clientJar.getAbsolutePath(), "MD5"));
		gReport.setHashSha256(CUtil.Files.getHash(clientJar.getAbsolutePath(), "SHA-256"));
		gReport.setFileSize((int) clientJar.length());
		gReport.setResult(true);
		gReport.setGenTime((int) (new Date().getTime() - start.getTime()));
		return gReport;
	}

}
