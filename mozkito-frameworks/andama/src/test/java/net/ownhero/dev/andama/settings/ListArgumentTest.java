/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.settings.arguments.ListArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;

import org.junit.After;
import org.junit.Test;

public class ListArgumentTest {
	
	private static String listString = "one,two";
	
	private static String name       = "listArg";
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidPropertiesDelimiter() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Required(), "@");
		System.setProperty(name, listString);
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(1, arg.getValue().size());
		assertTrue(arg.getValue().contains("one,two"));
	}
	
	@Test
	public void testNotRequiredGiven() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", listString,
		                                          new Required());
		assertEquals(name, arg.getName());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testNotRequiredNotGiven() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Optional());
		settings.parse();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		new ListArgument(settings.getRootArgumentSet(), name, "test description", null, new Required());
		boolean failed = false;
		try {
			settings.parse();
			failed = true;
		} catch (final SettingsParseError e) {
			
		} catch (final AssertionError e) {
			
		}
		
		if (failed) {
			fail();
		}
	}
	
	@Test
	public void testValidDefault() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", listString,
		                                          new Required());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testValidProperties() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Required());
		System.setProperty(name, listString);
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testValidPropertiesDelimiter() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final ListArgument arg = new ListArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Required(), "@");
		System.setProperty(name, "one@two");
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
}
