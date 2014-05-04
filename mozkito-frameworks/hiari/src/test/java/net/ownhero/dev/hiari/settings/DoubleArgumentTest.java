/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package net.ownhero.dev.hiari.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.DoubleArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

/**
 * The Class DoubleArgumentTest.
 */
public class DoubleArgumentTest {
	
	/**
	 * Test in valid properties.
	 */
	@Test
	public void testInValidProperties() {
		try {
			System.setProperty("testInValidProperties", "hubba");
			final Settings settings = new Settings();
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testInValidProperties",
			                                                   "test description", null, Requirement.required);
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
	 * Test not required given.
	 */
	@Test
	public void testNotRequiredGiven() {
		try {
			System.setProperty("testNotRequiredGiven", "1.34567");
			final Settings settings = new Settings();
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testNotRequiredGiven",
			                                                   "test description", null, Requirement.optional);
			final DoubleArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredGiven", arg.getName());
			assertFalse(arg.required());
			assertEquals(1.34567, arg.getValue().doubleValue(), 0);
			
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
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testNotRequiredNotGiven",
			                                                   "test description", null, Requirement.optional);
			final DoubleArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotGiven", arg.getName());
			assertFalse(arg.required());
			assertEquals(null, arg.getValue());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test required properties.
	 */
	@Test
	public void testRequiredProperties() {
		try {
			final Settings settings = new Settings();
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testRequiredProperties",
			                                                   "test description", null, Requirement.required);
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
	 * Test valid default.
	 */
	@Test
	public void testValidDefault() {
		try {
			final Settings settings = new Settings();
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testValidDefault",
			                                                   "test description", 1.6574356, Requirement.optional);
			final DoubleArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidDefault", arg.getName());
			assertFalse(arg.required());
			assertEquals(1.6574356, arg.getValue().doubleValue(), 0);
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	/**
	 * Test valid properties.
	 */
	@Test
	public void testValidProperties() {
		try {
			System.setProperty("testValidProperties", "1.34567");
			final Settings settings = new Settings();
			final Options options = new DoubleArgument.Options(settings.getRoot(), "testValidProperties",
			                                                   "test description", null, Requirement.required);
			final DoubleArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidProperties", arg.getName());
			assertTrue(arg.required());
			assertEquals(1.34567, arg.getValue().doubleValue(), 0);
			
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
