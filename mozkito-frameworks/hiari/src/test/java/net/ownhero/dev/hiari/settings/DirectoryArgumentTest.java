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
package net.ownhero.dev.hiari.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DirectoryArgumentTest {
	
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	private File        dir;
	
	@Before
	public void setUp() throws Exception {
		//
	}
	
	@After
	public void tearDown() throws Exception {
		if (this.dir != null) {
			FileUtils.deleteDirectory(this.dir);
		}
	}
	
	@Test
	public void testRequiredExistsCreate() {
		try {
			final Settings settings = new Settings();
			this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
			
			final DirectoryArgument.Options options = new DirectoryArgument.Options(settings.getRoot(),
			                                                                        "testArgRequiredExistsCreate",
			                                                                        "test argument", this.dir,
			                                                                        Requirement.required, true);
			final DirectoryArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testArgRequiredExistsCreate", arg.getName());
			assertTrue(arg.required());
			
			final File argFile = arg.getValue();
			assertEquals(this.dir, argFile);
			FileUtils.deleteDirectory(argFile);
		} catch (final ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final IOException e) {
			//
		} finally {
			//
		}
		
	}
	
	@Test
	public void testRequiredExistsNoCreate() {
		try {
			final Settings settings = new Settings();
			this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
			
			final DirectoryArgument.Options options = new DirectoryArgument.Options(settings.getRoot(),
			                                                                        "testArgRequiredExistsNoCreate",
			                                                                        "test argument", this.dir,
			                                                                        Requirement.optional, true);
			final DirectoryArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testArgRequiredExistsNoCreate", arg.getName());
			assertFalse(arg.required());
			
			final File argFile = arg.getValue();
			assertEquals(this.dir, argFile);
			FileUtils.deleteDirectory(argFile);
		} catch (final ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final IOException e) {
			//
		} finally {
			//
		}
		
	}
	
	@Test
	public void testRequiredNotExistsCreate() {
		
		try {
			this.dir = FileUtils.createDir(tmpDir, "directoryargumenttestdir", FileShutdownAction.DELETE);
			final String dirName = this.dir.getAbsolutePath();
			
			System.setProperty("testArgRequiredNotExistsCreate", dirName);
			
			final Settings settings = new Settings();
			if ((this.dir != null) && this.dir.exists()) {
				FileUtils.deleteDirectory(this.dir);
			}
			final DirectoryArgument.Options options = new DirectoryArgument.Options(settings.getRoot(),
			                                                                        "testArgRequiredNotExistsCreate",
			                                                                        "test argument", null,
			                                                                        Requirement.required, true);
			final DirectoryArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testArgRequiredNotExistsCreate", arg.getName());
			assertTrue(arg.required());
			
			final File argFile = arg.getValue();
			assertEquals(dirName, argFile.getAbsolutePath());
			FileUtils.deleteDirectory(argFile);
		} catch (final ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final IOException e) {
			//
		} finally {
			//
		}
	}
	
	@Test
	public void testRequiredNotExistsNoCreate() {
		try {
			final Settings settings = new Settings();
			if ((this.dir != null) && this.dir.exists()) {
				FileUtils.deleteDirectory(this.dir);
			}
			final DirectoryArgument.Options options = new DirectoryArgument.Options(settings.getRoot(),
			                                                                        "testArgRequiredNotExistsNoCreate",
			                                                                        "test argument", this.dir,
			                                                                        Requirement.required, false);
			ArgumentFactory.create(options);
			fail();
		} catch (final ArgumentRegistrationException e) {
			//
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final IOException e) {
			//
		} finally {
			//
		}
	}
}
