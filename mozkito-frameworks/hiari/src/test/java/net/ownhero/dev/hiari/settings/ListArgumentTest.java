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

package net.ownhero.dev.hiari.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import net.ownhero.dev.hiari.settings.ListArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

public class ListArgumentTest {
	
	@Test
	public void testInValidPropertiesDelimiter() {
		try {
			System.setProperty("testInValidPropertiesDelimiter", "one,two");
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testInValidPropertiesDelimiter",
			                                                 "test description", null, Requirement.required, "@");
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testInValidPropertiesDelimiter", arg.getName());
			assertTrue(arg.required());
			assert (arg != null);
			assertEquals(1, arg.getValue().size());
			assertTrue(arg.getValue().contains("one,two"));
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testNotRequiredGiven() {
		try {
			System.setProperty("testNotRequiredGiven", "one,two");
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testNotRequiredGiven",
			                                                 "test description", null, Requirement.optional);
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredGiven", arg.getName());
			assertFalse(arg.required());
			assert (arg != null);
			assertEquals(2, arg.getValue().size());
			assertTrue(arg.getValue().contains("one"));
			assertTrue(arg.getValue().contains("two"));
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		try {
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testNotRequiredNotGiven",
			                                                 "test description", null, Requirement.optional);
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testNotRequiredNotGiven", arg.getName());
			assertFalse(arg.required());
			assert (arg.getValue() == null);
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testRequiredProperties() {
		
		try {
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testRequiredProperties",
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
	
	@Test
	public void testValidDefault() {
		try {
			final Settings settings = new Settings();
			final ArrayList<String> list = new ArrayList<String>(2);
			list.add("one");
			list.add("two");
			final Options options = new ListArgument.Options(settings.getRoot(), "testValidDefault",
			                                                 "test description", list, Requirement.required);
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidDefault", arg.getName());
			assertTrue(arg.required());
			assert (arg != null);
			assertEquals(2, arg.getValue().size());
			assertTrue(arg.getValue().contains("one"));
			assertTrue(arg.getValue().contains("two"));
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testValidOptionalEmpty() {
		try {
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testValidOptionalEmpty",
			                                                 "test description", new ArrayList<String>(0),
			                                                 Requirement.required);
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidOptionalEmpty", arg.getName());
			assertTrue(arg.required());
			assert (arg != null);
			assertEquals(0, arg.getValue().size());
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testValidProperties() {
		try {
			System.setProperty("testValidProperties", "one,two");
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testValidProperties",
			                                                 "test description", null, Requirement.required);
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidProperties", arg.getName());
			assertTrue(arg.required());
			assert (arg != null);
			assertEquals(2, arg.getValue().size());
			assertTrue(arg.getValue().contains("one"));
			assertTrue(arg.getValue().contains("two"));
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
	@Test
	public void testValidPropertiesDelimiter() {
		try {
			System.setProperty("testValidPropertiesDelimiter", "one @ two");
			final Settings settings = new Settings();
			final Options options = new ListArgument.Options(settings.getRoot(), "testValidPropertiesDelimiter",
			                                                 "test description", null, Requirement.required, "@");
			final ListArgument arg = ArgumentFactory.create(options);
			
			assertEquals("testValidPropertiesDelimiter", arg.getName());
			assertTrue(arg.required());
			assert (arg != null);
			assertEquals(2, arg.getValue().size());
			assertTrue(arg.getValue().contains("one"));
			assertTrue(arg.getValue().contains("two"));
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
