package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.FileUtils.FileShutdownAction;

public class FileUtilsTest {
	
	private static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	File                  nameDir;
	File                  tmpDir;
	
	@Test
	public void exists() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator
		                        + "reposuiteFileUtilsTestDir");
		if (!this.nameDir.mkdirs()) {
			fail();
		}
		File newDir = FileUtils.createDir(this.tmpDir, "reposuiteFileUtilsTestDir", FileShutdownAction.DELETE);
		assertEquals(this.nameDir.getAbsolutePath(), newDir.getAbsolutePath());
		assertEquals(this.nameDir, newDir);
	}
	
	@Test
	public void existsAsFile() {
		this.nameDir = new File(this.tmpDir.getAbsoluteFile() + FileUtils.fileSeparator
		                        + "reposuiteFileUtilsTestDir");
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
