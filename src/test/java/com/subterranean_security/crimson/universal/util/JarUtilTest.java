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
package com.subterranean_security.crimson.universal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zeroturnaround.zip.commons.FileUtils;

import com.subterranean_security.crimson.core.util.FileUtil;

public class JarUtilTest {

	private static final File res = new File("test/res");
	private static File temp;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws IOException {
		temp = new File(System.getProperty("java.io.tmpdir") + "/tmp");
		temp.mkdirs();
		FileUtils.deleteDirectory(temp);
	}

	@Test
	public void testGetManifestValueStringFile() throws IOException {
		assertEquals("3464", JarUtil.getManifestValue("Build-Number", new File(res.getAbsolutePath() + "/test.jar")));
		assertEquals("INSTALLER", JarUtil.getManifestValue("Instance", new File(res.getAbsolutePath() + "/test.jar")));

		thrown.expect(IOException.class);
		JarUtil.getManifestValue("Nonexistant", new File(res.getAbsolutePath() + "/test.jar"));

		thrown.expect(IOException.class);
		thrown.expectMessage("Manifest not found");
		JarUtil.getManifestValue("Build-Number", new File(res.getAbsolutePath() + "/test.zip"));
	}

	@Test
	public void testClassExists() {
		assertFalse(JarUtil.classExists(""));
		assertTrue(JarUtil.classExists("com.subterranean_security.crimson.universal.Universal"));
	}

	@Test
	public void testExtractInputStreamString() throws FileNotFoundException, IOException {
		JarUtil.extract(new FileInputStream(res.getAbsolutePath() + "/test.zip"), temp.getAbsolutePath());

		assertEquals("8d1b69dd9bdc9df4a8073c7a8193c7af", FileUtil.hash(temp.getAbsolutePath() + "/test5", "MD5"));
		assertEquals("247585e773d4ba881b225e135893a35e",
				FileUtil.hash(temp.getAbsolutePath() + "/test1/random.bin", "MD5"));
		assertTrue(
				new File(temp.getAbsolutePath() + "/test1/test/test/test/test/test/test/test/test/test/test/test/test")
						.exists());
	}

}
