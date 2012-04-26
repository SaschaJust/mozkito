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
import net.ownhero.dev.hiari.settings.EnumArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Test;

public class EnumArgumentTest {
	
	private static enum TestEnum {
		ONE, TWO;
		
	};
	
	@Test
	public void testInValidProperties() {
		try {
			System.setProperty("testInValidProperties", "Onee");
			final Settings settings = new Settings();
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testInValidProperties",
			                                                                     "test description", TestEnum.ONE,
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
	
	@Test
	public void testNotRequiredGiven() {
		try {
			System.setProperty("testNotRequiredGiven", "one");
			final Settings settings = new Settings();
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testNotRequiredGiven",
			                                                                     "test description", null,
			                                                                     Requirement.optional,
			                                                                     TestEnum.values());
			final EnumArgument<TestEnum> arg = ArgumentFactory.create(options);
			assertEquals("testNotRequiredGiven", arg.getName());
			assertFalse(arg.required());
			assertEquals(TestEnum.ONE, arg.getValue());
			
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
			
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testNotRequiredNotGiven",
			                                                                     "test description", null,
			                                                                     Requirement.optional,
			                                                                     TestEnum.values());
			final EnumArgument<TestEnum> arg = ArgumentFactory.create(options);
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
	
	@Test
	public void testRequiredProperties() {
		try {
			final Settings settings = new Settings();
			
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testRequiredProperties",
			                                                                     "test description", null,
			                                                                     Requirement.required,
			                                                                     TestEnum.values());
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
			
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testValidDefault",
			                                                                     "test description", TestEnum.TWO,
			                                                                     Requirement.required);
			final EnumArgument<TestEnum> arg = ArgumentFactory.create(options);
			assertEquals("testValidDefault", arg.getName());
			assertTrue(arg.required());
			assertEquals(TestEnum.TWO, arg.getValue());
			
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
			System.setProperty("testValidProperties", "Two");
			final Settings settings = new Settings();
			final Options<TestEnum> options = new EnumArgument.Options<TestEnum>(settings.getRoot(),
			                                                                     "testValidProperties",
			                                                                     "test description", null,
			                                                                     Requirement.required,
			                                                                     TestEnum.values());
			final EnumArgument<TestEnum> arg = ArgumentFactory.create(options);
			assertEquals("testValidProperties", arg.getName());
			assertTrue(arg.required());
			assertEquals(TestEnum.TWO, arg.getValue());
			
		} catch (ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
