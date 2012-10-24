/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.mozkito.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsTest {
	
	private static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	File                  nameDir;
	File                  tmpDir;
	
	@Test
	public void exists() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator + "reposuiteFileUtilsTestDir");
		if (!this.nameDir.mkdirs()) {
			fail();
		}
		File newDir = FileUtils.createDir(this.tmpDir, "reposuiteFileUtilsTestDir", FileShutdownAction.DELETE);
		assertEquals(this.nameDir.getAbsolutePath(), newDir.getAbsolutePath());
		assertEquals(this.nameDir, newDir);
	}
	
	@Test
	public void existsAsFile() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator + "reposuiteFileUtilsTestDir");
		try {
			if (!this.nameDir.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		File newDir = FileUtils.createDir(this.tmpDir, "reposuiteFileUtilsTestDir", FileShutdownAction.DELETE);
		assertEquals(null, newDir);
	}
	
	@Test
	public void parentDirNoDir() {
		
		File file = new File(FileUtils.fileSeparator + RandomStringUtils.random(10, chars));
		while (file.exists()) {
			file = new File(FileUtils.fileSeparator + RandomStringUtils.random(10, chars));
		}
		File newDir = FileUtils.createDir(file, "reposuiteFileUtilsTestDir", FileShutdownAction.DELETE);
		assertEquals(null, newDir);
	}
	
	@Before
	public void setUp() {
		this.tmpDir = new File(System.getProperty("java.io.tmpdir"));
		this.nameDir = this.tmpDir;
	}
	
	@After
	public void tearDown() {
		this.nameDir.delete();
	}
	
}
