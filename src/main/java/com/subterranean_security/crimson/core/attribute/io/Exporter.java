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
package com.subterranean_security.crimson.core.attribute.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Base64;

import com.subterranean_security.crimson.core.attribute.group.AttributeGroup;
import com.subterranean_security.crimson.core.util.SerialUtil;
import com.subterranean_security.crimson.proto.core.Misc.Outcome;

/**
 * @author cilki
 * @since 5.0.0
 */
public class Exporter {
	public static Outcome exportGroup(AttributeGroup am, File output) {

		try (PrintWriter pw = new PrintWriter(output)) {
			pw.println(Base64.getEncoder().encodeToString(SerialUtil.serialize(am)));
		} catch (FileNotFoundException e) {
			return Outcome.newBuilder().setResult(false).setComment(e.getMessage()).build();
		}
		return Outcome.newBuilder().setResult(true).build();
	}
}
