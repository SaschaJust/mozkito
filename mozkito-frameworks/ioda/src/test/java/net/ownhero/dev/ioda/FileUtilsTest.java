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
package net.ownhero.dev.ioda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class FileUtilsTest.
 */
public class FileUtilsTest {
	
	/** The chars. */
	private static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/** The name dir. */
	File                  nameDir;
	
	/** The tmp dir. */
	File                  tmpDir;
	
	/**
	 * Exists.
	 */
	@Test
	public void exists() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator + "iodaFileUtilsTestDir");
		if (!this.nameDir.mkdirs()) {
			fail();
		}
		
		try {
			final File newDir = FileUtils.createDir(this.tmpDir, "iodaFileUtilsTestDir", FileShutdownAction.DELETE);
			assertEquals(this.nameDir.getAbsolutePath(), newDir.getAbsolutePath());
			assertEquals(this.nameDir, newDir);
		} catch (final FilePermissionException e) {
			fail();
		}
		
	}
	
	/**
	 * Exists as file.
	 */
	@Test
	public void existsAsFile() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator + "iodaFileUtilsTestDir");
		try {
			if (!this.nameDir.createNewFile()) {
				fail("The file must be created for this test");
			}
		} catch (final IOException e) {
			fail(e.getMessage());
		}
		try {
			final File newDir = FileUtils.createDir(this.tmpDir, "iodaFileUtilsTestDir", FileShutdownAction.DELETE);
			assertEquals(null, newDir);
		} catch (final FilePermissionException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Parent dir no dir.
	 */
	@Test
	public void parentDirNoDir() {
		
		File file = new File(FileUtils.fileSeparator + RandomStringUtils.random(10, FileUtilsTest.chars));
		while (file.exists()) {
			file = new File(FileUtils.fileSeparator + RandomStringUtils.random(10, FileUtilsTest.chars));
		}
		try {
			FileUtils.createDir(file, "iodaFileUtilsTestDir", FileShutdownAction.DELETE);
			fail("The file already exists and thus should not be created again.");
		} catch (final FilePermissionException e) {
			// ignore
		}
	}
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		this.tmpDir = new File(System.getProperty("java.io.tmpdir"));
		this.nameDir = this.tmpDir;
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		this.nameDir.delete();
	}
	
}
