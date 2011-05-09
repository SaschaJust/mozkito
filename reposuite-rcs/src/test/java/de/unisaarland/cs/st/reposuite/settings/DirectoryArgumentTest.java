package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

public class DirectoryArgumentTest {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	private File        dir;
	private String      dirName;
	
	@Before
	public void setUp() throws Exception {
		this.dirName = tmpDir.getAbsolutePath() + FileUtils.fileSeparator + "directoryargumenttestdir";
	}
	
	@After
	public void tearDown() throws Exception {
		if (this.dir != null) {
			this.dir.delete();
		}
	}
	
	@Test
	public void testRequiredExistsCreate() {
		RepositorySettings settings = new RepositorySettings();
		this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(),
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
		RepositorySettings settings = new RepositorySettings();
		this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", this.dir.getAbsolutePath(),
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
		RepositorySettings settings = new RepositorySettings();
		DirectoryArgument arg = new DirectoryArgument(settings, "testArg", "test argument", this.dirName, true, true);
		try {
			settings.parseArguments();
			arg.getValue().delete();
		} catch (RuntimeException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testRequiredNotExistsNoCreate() {
		RepositorySettings settings = new RepositorySettings();
		new DirectoryArgument(settings, "testArg", "test argument", this.dirName, true, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		
	}
}
