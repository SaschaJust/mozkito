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
import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;

import org.junit.After;
import org.junit.Test;

public class EnumArgumentTest {
	
	private static enum TestEnum {
		ONE, TWO;
		
		public static String[] getValues() {
			final TestEnum[] values = values();
			final String[] result = new String[values.length];
			for (int i = 0; i < values.length; ++i) {
				result[i] = values[i].toString();
			}
			return result;
		}
	};
	
	private static String name = "enumArg";
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidDefault() {
		final AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings.getRootArgumentSet(), name, "test description", "hubba", new Required(),
		                 TestEnum.getValues());
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
	@Test
	public void testInValidProperties() {
		final AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings.getRootArgumentSet(), name, "test description", "hubba", new Required(),
		                 TestEnum.getValues());
		System.setProperty(name, "hubba");
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
	@Test
	public void testNotRequiredGiven() {
		final AndamaSettings settings = new AndamaSettings();
		final EnumArgument arg = new EnumArgument(settings.getRootArgumentSet(), name, "test description", "ONE",
		                                          new Optional(), TestEnum.getValues());
		assertEquals(name, arg.getName());
		try {
			settings.parseArguments();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.ONE.toString(), arg.getValue());
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		final AndamaSettings settings = new AndamaSettings();
		final EnumArgument arg = new EnumArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Optional(), TestEnum.getValues());
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() {
		final AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings.getRootArgumentSet(), name, "test description", null, new Required(),
		                 TestEnum.getValues());
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
	@Test
	public void testValidDefault() {
		final AndamaSettings settings = new AndamaSettings();
		final EnumArgument arg = new EnumArgument(settings.getRootArgumentSet(), name, "test description", "TWO",
		                                          new Required(), TestEnum.getValues());
		try {
			settings.parseArguments();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO.toString(), arg.getValue());
	}
	
	@Test
	public void testValidProperties() {
		final AndamaSettings settings = new AndamaSettings();
		final EnumArgument arg = new EnumArgument(settings.getRootArgumentSet(), name, "test description", null,
		                                          new Required(), TestEnum.getValues());
		System.setProperty(name, "TWO");
		try {
			settings.parseArguments();
		} catch (final Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO.toString(), arg.getValue());
	}
	
}
