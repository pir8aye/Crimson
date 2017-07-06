/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
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
package com.subterranean_security.crimson.server.exe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.subterranean_security.crimson.core.net.Connector;
import com.subterranean_security.crimson.core.net.executor.temp.ExeI;
import com.subterranean_security.crimson.core.net.executor.temp.Exelet;
import com.subterranean_security.crimson.core.store.ConnectionStore;
import com.subterranean_security.crimson.proto.core.Generator.GenReport;
import com.subterranean_security.crimson.proto.core.net.sequences.Generator.RS_Generate;
import com.subterranean_security.crimson.proto.core.net.sequences.MSG.Message;
import com.subterranean_security.crimson.server.Generator;
import com.subterranean_security.crimson.server.store.ServerProfileStore;
import com.subterranean_security.crimson.sv.permissions.Perm;

/**
 * @author cilki
 * @since 5.0.0
 */
public class GenerateExe extends Exelet implements ExeI {

	private static final Logger log = LoggerFactory.getLogger(GenerateExe.class);

	public GenerateExe(Connector connector) {
		super(connector);
	}

	public void rq_generate(Message m) {

		// check permissions
		if (!ServerProfileStore.getViewer(connector.getCvid()).getPermissions()
				.getFlag(Perm.server.generator.generate_jar)) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder()
							.setReport(GenReport.newBuilder().setResult(false).setComment("Insufficient permissions")))
					.build());
			return;
		}

		byte[] res = null;
		Generator g = new Generator();
		try {
			if (m.getRqGenerate().getSendToCid() != 0) {
				g.generate(m.getRqGenerate().getInternalConfig(), m.getRqGenerate().getSendToCid());
			} else {
				g.generate(m.getRqGenerate().getInternalConfig());
			}

			res = g.getResult();

			RS_Generate.Builder rs = RS_Generate.newBuilder().setInstaller(ByteString.copyFrom(res))
					.setReport(g.getReport());

			if (m.getRqGenerate().getSendToCid() != 0) {
				ConnectionStore.get(m.getRqGenerate().getSendToCid())
						.write(Message.newBuilder().setRsGenerate(rs).build());
				connector.write(Message.newBuilder().setId(m.getId())
						.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
			} else {
				connector.write(Message.newBuilder().setId(m.getId()).setRsGenerate(rs).build());
			}
		} catch (Exception e) {
			connector.write(Message.newBuilder().setId(m.getId())
					.setRsGenerate(RS_Generate.newBuilder().setReport(g.getReport())).build());
		}

	}

}
