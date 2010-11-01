package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

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
		nameDir = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator")
		        + "reposuiteFileUtilsTestDir");
		if (!nameDir.mkdirs()) {
			fail();
		}
		File newDir = FileUtils.createDir(tmpDir, "reposuiteFileUtilsTestDir");
		assertEquals(nameDir.getAbsolutePath(), newDir.getAbsolutePath());
		assertEquals(nameDir, newDir);
	}
	
	@Test
	public void existsAsFile() {
		nameDir = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator")
		        + "reposuiteFileUtilsTestDir");
		try {
			if (!nameDir.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		File newDir = FileUtils.createDir(tmpDir, "reposuiteFileUtilsTestDir");
		assertEquals(null, newDir);
	}
	
	@Test
	public void parentDirNoDir() {
		
		File file = new File(System.getProperty("file.separator") + RandomStringUtils.random(10, chars));
		while (file.exists()) {
			file = new File(System.getProperty("file.separator") + RandomStringUtils.random(10, chars));
		}
		File newDir = FileUtils.createDir(file, "reposuiteFileUtilsTestDir");
		assertEquals(null, newDir);
	}
	
	@Before
	public void setUp() {
		tmpDir = new File(System.getProperty("java.io.tmpdir"));
		nameDir = tmpDir;
	}
	
	@After
	public void tearDown() {
		nameDir.delete();
	}
	
}
