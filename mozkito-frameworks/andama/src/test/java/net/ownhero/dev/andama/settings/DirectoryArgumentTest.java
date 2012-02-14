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
package net.ownhero.dev.andama.settings;

import static org.junit.Assert.fail;

import java.io.File;

import net.ownhero.dev.andama.settings.arguments.DirectoryArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		final Settings settings = new Settings();
		this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
		try {
			final DirectoryArgument arg = new DirectoryArgument(settings.getRootArgumentSet(), "testArg",
			                                                    "test argument", this.dir.getAbsolutePath(),
			                                                    new Required(), true);
			settings.parse();
			arg.getValue().delete();
		} catch (final Exception e) {
			fail();
		}
		
	}
	
	@Test
	public void testRequiredExistsNoCreate() {
		final Settings settings = new Settings();
		this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
		try {
			final DirectoryArgument arg = new DirectoryArgument(settings.getRootArgumentSet(), "testArg",
			                                                    "test argument", this.dir.getAbsolutePath(),
			                                                    new Optional(), true);
			settings.parse();
			arg.getValue().delete();
		} catch (final Exception e) {
			fail();
		}
		
	}
	
	@Test
	public void testRequiredNotExistsCreate() {
		final Settings settings = new Settings();
		try {
			final DirectoryArgument arg = new DirectoryArgument(settings.getRootArgumentSet(), "testArg",
			                                                    "test argument", this.dirName, new Required(), true);
			settings.parse();
			arg.getValue().delete();
		} catch (final Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testRequiredNotExistsNoCreate() {
		final Settings settings = new Settings();
		try {
			new DirectoryArgument(settings.getRootArgumentSet(), "testArg", "test argument", this.dirName,
			                      new Optional(), false);
			settings.parse();
			fail();
		} catch (final Exception e) {
			
		}
		
	}
}
