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
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;

import org.junit.After;
import org.junit.Test;

public class EnumArgumentTest {
	
	private static enum TestEnum {
		ONE, TWO;
		
	};
	
	private static String name = "enumArg";
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidProperties() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name, "test description", TestEnum.ONE,
		                           new Required());
		System.setProperty(name, "hubba");
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testNotRequiredGiven() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final EnumArgument<TestEnum> arg = new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name,
		                                                              "test description", TestEnum.ONE, new Optional());
		assertEquals(name, arg.getName());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.ONE, arg.getValue());
	}
	
	@Test
	public void testNotRequiredNotGiven() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final EnumArgument<TestEnum> arg = new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name,
		                                                              "test description", null, new Optional(),
		                                                              TestEnum.values());
		try {
			settings.parse();
		} catch (final SettingsParseError e) {
			fail();
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name, "test description", null, new Required(),
		                           TestEnum.values());
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testValidDefault() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final EnumArgument<TestEnum> arg = new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name,
		                                                              "test description", TestEnum.TWO, new Required(),
		                                                              TestEnum.values());
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO, arg.getValue());
	}
	
	@Test
	public void testValidProperties() throws ArgumentRegistrationException {
		final Settings settings = new Settings();
		final EnumArgument<TestEnum> arg = new EnumArgument<TestEnum>(settings.getRootArgumentSet(), name,
		                                                              "test description", null, new Required(),
		                                                              TestEnum.values());
		System.setProperty(name, "TWO");
		try {
			settings.parse();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO, arg.getValue());
	}
	
}
