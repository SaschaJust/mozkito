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

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class InputFileArgumentTest.
 */
public class InputFileArgumentTest {
	
	/** The file. */
	private File file;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
		if (this.file.exists()) {
			this.file.delete();
		}
	}
	
	/**
	 * Test not required exists.
	 */
	@Test
	public void testNotRequiredExists() {
		try {
			System.setProperty("testNotRequiredExists", this.file.getAbsolutePath());
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(settings.getRoot(),
			                                                                        "testNotRequiredExists",
			                                                                        "test argument", null,
			                                                                        Requirement.optional);
			final InputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredExists", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required not exists default available.
	 */
	@Test
	public void testNotRequiredNotExistsDefaultAvailable() {
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(
			                                                                        settings.getRoot(),
			                                                                        "testNotRequiredNotExistsDefaultAvailable",
			                                                                        "test argument", this.file,
			                                                                        Requirement.optional);
			final InputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotExistsDefaultAvailable", arg.getName());
			assertFalse(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test not required not given.
	 */
	@Test
	public void testNotRequiredNotGiven() {
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(settings.getRoot(),
			                                                                        "testNotRequiredNotGiven",
			                                                                        "test argument", null,
			                                                                        Requirement.optional);
			final InputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotGiven", arg.getName());
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
	 * Test required exists.
	 */
	@Test
	public void testRequiredExists() {
		try {
			System.setProperty("testRequiredExists", this.file.getAbsolutePath());
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(settings.getRoot(),
			                                                                        "testRequiredExists",
			                                                                        "test argument", null,
			                                                                        Requirement.required);
			final InputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testRequiredExists", arg.getName());
			assertTrue(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test required given not exists.
	 */
	@Test
	public void testRequiredGivenNotExists() {
		try {
			System.setProperty("testRequiredGivenNotExists", "/tmp/kimkimkim.just.just");
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(settings.getRoot(),
			                                                                        "testRequiredGivenNotExists",
			                                                                        "test argument", null,
			                                                                        Requirement.required);
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
	 * Test required not exists default available.
	 */
	@Test
	public void testRequiredNotExistsDefaultAvailable() {
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(
			                                                                        settings.getRoot(),
			                                                                        "testRequiredNotExistsDefaultAvailable",
			                                                                        "test argument", this.file,
			                                                                        Requirement.required);
			final InputFileArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testRequiredNotExistsDefaultAvailable", arg.getName());
			assertTrue(arg.required());
			
			final File value = arg.getValue();
			assert (value != null);
			assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test required not given.
	 */
	@Test
	public void testRequiredNotGiven() {
		try {
			final Settings settings = new Settings();
			final InputFileArgument.Options options = new InputFileArgument.Options(settings.getRoot(),
			                                                                        "testRequiredNotGiven",
			                                                                        "test argument", null,
			                                                                        Requirement.required);
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
	
}
