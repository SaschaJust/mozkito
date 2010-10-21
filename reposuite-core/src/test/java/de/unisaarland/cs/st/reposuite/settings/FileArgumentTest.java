package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.FileUtils;

public class FileArgumentTest {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	private File        dir;
	private File        file;
	
	@Before
	public void setUp() {
		dir = FileUtils.createDir(tmpDir, "fileargumenttestdir");
		file = new File(tmpDir.getAbsolutePath() + System.getProperty("file.separator") + "fileargumenttestfile");
	}
	
	@Test
	public void setValue() {
		
	}
	
	@After
	public void tearDown() {
		dir.delete();
		file.delete();
	}
	
	@Test
	public void testDirectory() {
		if (dir == null) {
			fail();
		}
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", dir.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", dir.getAbsolutePath(), true, true, false);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", dir.getAbsolutePath(), true, false, true);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", dir.getAbsolutePath(), true, true, true);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testExistsNoOverwrite() {
		try {
			if (!file.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			fail();
		}
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", file.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testExistsOverwrite() {
		try {
			if (!file.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			fail();
		}
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", file.getAbsolutePath(), true, true, false);
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefautNotRequired() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", null, false, false, false);
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefautRequired() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", null, true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testNotExistsMustExist() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", file.getAbsolutePath(), true, false, true);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testNotExistsMustNotExist() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new FileArgument(settings, "testArg", "test argument", file.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
}
