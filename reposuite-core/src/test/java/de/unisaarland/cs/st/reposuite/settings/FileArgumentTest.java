package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils.FileShutdownAction;

public class FileArgumentTest {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	private File        dir;
	private File        file;
	
	@Before
	public void setUp() {
		this.dir = FileUtils.createDir(tmpDir, "fileargumenttestdir", FileShutdownAction.DELETE);
		this.file = new File(tmpDir.getAbsolutePath() + FileUtils.fileSeparator + "fileargumenttestfile");
	}
	
	@Test
	public void setValue() {
		
	}
	
	@After
	public void tearDown() {
		this.dir.delete();
		this.file.delete();
	}
	
	@Test
	public void testDirectory() {
		if (this.dir == null) {
			fail();
		}
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(), true, true, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(), true, false, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(), true, true, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testExistsNoOverwrite() {
		try {
			if (!this.file.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			fail();
		}
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.file.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testExistsOverwrite() {
		try {
			if (!this.file.createNewFile()) {
				fail();
			}
		} catch (IOException e) {
			fail();
		}
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.file.getAbsolutePath(), true, true, false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefautNotRequired() {
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", null, false, false, false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefautRequired() {
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", null, true, false, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNotExistsMustExist() {
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.file.getAbsolutePath(), true, false, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNotExistsMustNotExist() {
		RepositorySettings settings = new RepositorySettings();
		new FileArgument(settings, "testArg", "test argument", this.file.getAbsolutePath(), true, false, false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
}
