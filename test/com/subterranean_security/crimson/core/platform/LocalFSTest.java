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
package com.subterranean_security.crimson.core.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zeroturnaround.zip.commons.FileUtils;

import com.subterranean_security.crimson.universal.util.JarUtil;

public class LocalFSTest {

	private static LocalFS fs;

	private static final File res = new File("test/res");
	private static File temp;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	static {
		temp = new File(System.getProperty("java.io.tmpdir") + "/tmp");
		temp.mkdirs();
		try {
			FileUtils.deleteDirectory(temp);
			JarUtil.extract(new FileInputStream(res.getAbsolutePath() + "/test.jar"), temp.getAbsolutePath());
			JarUtil.extract(new FileInputStream(res.getAbsolutePath() + "/test.zip"), temp.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fs = new LocalFS(temp.getAbsolutePath());
	}

	@Test
	public void testDown() {
		assertTrue(fs.setPath(temp.getAbsolutePath()));
		assertFalse(fs.down("logback.xml"));
		assertFalse(fs.down("test5"));
		assertTrue(fs.down("test1"));
	}

	@Test
	public void testSetPath() {
		assertTrue(fs.setPath(temp.getAbsolutePath() + "/test1/test/test/test/test"));
		assertEquals(temp.getAbsolutePath() + "/test1/test/test/test/test", fs.pwd());
		assertTrue(fs.setPath(temp.getAbsolutePath() + "/test1"));
	}

	@Test
	public void testUp() {
		assertTrue(fs.setPath("/"));
		assertFalse(fs.up());
	}

	@Test
	public void testList() throws IOException {
		assertTrue(fs.setPath(temp.getAbsolutePath()));
		assertEquals(8, fs.list().size());
	}

}
