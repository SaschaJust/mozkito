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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.settings.arguments.BooleanArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;

import org.junit.Test;

public class BooleanArgumentTest {
	
	@Test
	public void testGetValue_DefaultFalse_NotRequired() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "fAlse", new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(false, arg.getValue());
		assertEquals(false, arg.required());
	}
	
	@Test
	public void testGetValue_DefaultFalse_Required() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "fAlse", new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(false, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_NotRequired() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "TRuE", new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.required());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_Required() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "trUe", new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_NotRequired() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.required());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_Required() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testSetValue() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Required());
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
		
		System.setProperty("testArg", "false");
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail(e.getMessage());
		}
		assertEquals(false, arg.getValue());
	}
}
