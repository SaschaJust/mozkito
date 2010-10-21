package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.FileUtils;

public class DirectoryArgumentTest {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	private File        dir;
	private String      dirName;
	
	@Before
	public void setUp() throws Exception {
		dirName = tmpDir.getAbsolutePath() + System.getProperty("file.deparator") + "directoryargumenttestdir";
	}
	
	@After
	public void tearDown() throws Exception {
		if (dir != null) {
			dir.delete();
		}
	}
	
	@Test
	public void testRequiredExistsCreate() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir");
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", dir.getAbsolutePath(),
		        true, true);
		try {
			settings.parseArguments();
			arg.getValue().delete();
		} catch (RuntimeException e) {
			fail();
		}
		
	}
	
	@Test
	public void testRequiredExistsNoCreate() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir");
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", dir.getAbsolutePath(),
		        true, false);
		try {
			settings.parseArguments();
			arg.getValue().delete();
		} catch (RuntimeException e) {
			fail();
		}
		
	}
	
	@Test
	public void testRequiredNotExistsCreate() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", dirName, true, true);
		try {
			settings.parseArguments();
			arg.getValue().delete();
		} catch (RuntimeException e) {
			fail();
		}
		
	}
	
	@Test
	public void testRequiredNotExistsNoCreate() {
		RepoSuiteSettings settings = new RepoSuiteSettings();
		new DirectoryArgument(settings, "testArg", "test argument", dirName, true, false);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		
	}
}
