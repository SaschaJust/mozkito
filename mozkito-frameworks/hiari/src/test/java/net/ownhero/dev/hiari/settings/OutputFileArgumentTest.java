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

import net.ownhero.dev.hiari.settings.OutputFileArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;

/**
 * The Class OutputFileArgumentTest.
 */
public class OutputFileArgumentTest {
	
	/**
	 * Test not required exists no overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testNotRequiredExistsNoOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			System.setProperty("testNotRequiredExistsNoOverwrite", file.getAbsolutePath());
			final Settings settings = new Settings();
			final Options options = new OutputFileArgument.Options(settings.getRoot(),
			                                                       "testNotRequiredExistsNoOverwrite", "test argument",
			                                                       null, Requirement.optional, false);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredExistsNoOverwrite", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value == null);
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required exists overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testNotRequiredExistsOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final long lastModified = file.lastModified();
			System.setProperty("testNotRequiredExistsOverwrite", file.getAbsolutePath());
			Thread.sleep(1000);
			final Settings settings = new Settings();
			final Options options = new OutputFileArgument.Options(settings.getRoot(),
			                                                       "testNotRequiredExistsOverwrite", "test argument",
			                                                       null, Requirement.optional, true);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredExistsOverwrite", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			
			assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
			assertTrue(value.lastModified() > lastModified);
			value.delete();
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException
		        | InterruptedException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required not exists no overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testNotRequiredNotExistsNoOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final long lastModified = file.lastModified();
			System.setProperty("testNotRequiredNotExistsNoOverwrite", file.getAbsolutePath());
			if (file.exists()) {
				file.delete();
			}
			Thread.sleep(1000);
			final Settings settings = new Settings();
			System.err.println(file.exists());
			final Options options = new OutputFileArgument.Options(settings.getRoot(),
			                                                       "testNotRequiredNotExistsNoOverwrite",
			                                                       "test argument", null, Requirement.optional, false);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotExistsNoOverwrite", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			
			assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
			assertTrue(value.lastModified() > lastModified);
			value.delete();
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException
		        | InterruptedException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required not exists no overwrite required.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testNotRequiredNotExistsNoOverwriteREQUIRED() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final long lastModified = file.lastModified();
			System.setProperty("testNotRequiredNotExistsNoOverwriteREQUIRED", file.getAbsolutePath());
			if (file.exists()) {
				file.delete();
			}
			Thread.sleep(1000);
			final Settings settings = new Settings();
			System.err.println(file.exists());
			final Options options = new OutputFileArgument.Options(settings.getRoot(),
			                                                       "testNotRequiredNotExistsNoOverwriteREQUIRED",
			                                                       "test argument", null, Requirement.required, false);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotExistsNoOverwriteREQUIRED", arg.getName());
			assertTrue(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			
			assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
			assertTrue(value.lastModified() > lastModified);
			value.delete();
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException
		        | InterruptedException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required not exists overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testNotRequiredNotExistsOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final long lastModified = file.lastModified();
			if (file.exists()) {
				file.delete();
			}
			System.setProperty("testNotRequiredNotExistsOverwrite", file.getAbsolutePath());
			Thread.sleep(1000);
			final Settings settings = new Settings();
			final Options options = new OutputFileArgument.Options(settings.getRoot(),
			                                                       "testNotRequiredNotExistsOverwrite",
			                                                       "test argument", null, Requirement.optional, true);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotExistsOverwrite", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			
			assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
			assertTrue(value.lastModified() > lastModified);
			value.delete();
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException
		        | InterruptedException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test required exists no overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testRequiredExistsNoOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			
			System.setProperty("testRequiredExistsNoOverwrite", file.getAbsolutePath());
			final Settings settings = new Settings();
			final Options options = new OutputFileArgument.Options(settings.getRoot(), "testRequiredExistsNoOverwrite",
			                                                       "test argument", null, Requirement.required, false);
			ArgumentFactory.create(options);
			fail();
		} catch (SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			//
		} finally {
			//
		}
	}
	
	/**
	 * Test required exists overwrite.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testRequiredExistsOverwrite() throws IOException {
		try {
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final long lastModified = file.lastModified();
			System.setProperty("testRequiredExistsOverwrite", file.getAbsolutePath());
			Thread.sleep(1000);
			final Settings settings = new Settings();
			final Options options = new OutputFileArgument.Options(settings.getRoot(), "testRequiredExistsOverwrite",
			                                                       "test argument", null, Requirement.optional, true);
			final OutputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testRequiredExistsOverwrite", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			
			assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
			assertTrue(value.lastModified() > lastModified);
			value.delete();
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException
		        | InterruptedException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
