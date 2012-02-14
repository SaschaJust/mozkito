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
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.settings.arguments.DoubleArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DoubleArgumentTest {
	
	private static String name = "doubleArg";
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidDefault() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		new DoubleArgument(settings.getRootArgumentSet(), name, "test description", "hubba", new Required());
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testInValidProperties() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		new DoubleArgument(settings.getRootArgumentSet(), name, "test description", null, new Required());
		System.setProperty(name, "hubba");
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testNotRequiredGiven() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final DoubleArgument arg = new DoubleArgument(settings.getRootArgumentSet(), name, "test description", "2.5",
		                                              new Optional());
		assertEquals(name, arg.getName());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(), 0);
	}
	
	@Test
	public void testNotRequiredNotGiven() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final DoubleArgument arg = new DoubleArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                              new Optional());
		settings.parse();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		new DoubleArgument(settings.getRootArgumentSet(), name, "test description", null, new Required());
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testValidDefault() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final DoubleArgument arg = new DoubleArgument(settings.getRootArgumentSet(), name, "test description", "2.5",
		                                              new Required());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(), 0);
	}
	
	@Test
	public void testValidProperties() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		final DoubleArgument arg = new DoubleArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                              new Required());
		System.setProperty(name, "2.5");
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(), 0);
	}
	
}
