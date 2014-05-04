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
import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

/**
 * The Class BooleanArgumentTest.
 */
public class BooleanArgumentTest {
	
	/**
	 * Test get value_ default false_ not required.
	 */
	@Test
	public void testGetValue_DefaultFalse_NotRequired() {
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_DefaultFalse_NotRequired",
			                                                                    "this is only a test argument", false,
			                                                                    Requirement.optional);
			
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArg_DefaultFalse_NotRequired", arg.getName());
			assertEquals(false, arg.getValue());
			assertEquals(false, arg.required());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test get value_ default false_ required.
	 */
	@Test
	public void testGetValue_DefaultFalse_Required() {
		
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_DefaultFalse_Required",
			                                                                    "this is only a test argument", false,
			                                                                    Requirement.required);
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArg_DefaultFalse_Required", arg.getName());
			assertEquals(true, arg.required());
			assertEquals(false, arg.getValue());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test get value_ default true_ not required.
	 */
	@Test
	public void testGetValue_DefaultTrue_NotRequired() {
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_DefaultTrue_NotRequired",
			                                                                    "this is only a test argument", true,
			                                                                    Requirement.optional);
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArg_DefaultTrue_NotRequired", arg.getName());
			assertEquals(false, arg.required());
			assertEquals(true, arg.getValue());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test get value_ default true_ required.
	 */
	@Test
	public void testGetValue_DefaultTrue_Required() {
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_DefaultTrue_Required",
			                                                                    "this is only a test argument", true,
			                                                                    Requirement.required);
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArg_DefaultTrue_Required", arg.getName());
			assertEquals(true, arg.required());
			assertEquals(true, arg.getValue());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test get value_ no default_ not required.
	 */
	@Test
	public void testGetValue_NoDefault_NotRequired() {
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_NoDefault_NotRequired",
			                                                                    "this is only a test argument", null,
			                                                                    Requirement.optional);
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArg_NoDefault_NotRequired", arg.getName());
			assertEquals(false, arg.required());
			assertEquals(null, arg.getValue());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test get value_ no default_ required.
	 */
	@Test
	public void testGetValue_NoDefault_Required() {
		
		try {
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(),
			                                                                    "testArg_NoDefault_Required",
			                                                                    "this is only a test argument", null,
			                                                                    Requirement.required);
			ArgumentFactory.create(options);
			fail();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			//
		} finally {
			//
		}
	}
	
	/**
	 * Test set value.
	 */
	@Test
	public void testSetValue() {
		try {
			System.setProperty("testArgSetValue", "false");
			final Settings settings = new Settings();
			final BooleanArgument.Options options = new BooleanArgument.Options(settings.getRoot(), "testArgSetValue",
			                                                                    "this is only a test argument", true,
			                                                                    Requirement.required);
			final BooleanArgument arg = ArgumentFactory.create(options);
			assertEquals("this is only a test argument", arg.getDescription());
			assertEquals("testArgSetValue", arg.getName());
			assertEquals(true, arg.required());
			assertEquals(false, arg.getValue());
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
}
