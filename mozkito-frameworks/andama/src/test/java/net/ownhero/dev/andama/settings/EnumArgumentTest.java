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

import org.junit.After;
import org.junit.Test;

public class EnumArgumentTest {
	
	private static enum TestEnum {
		ONE, TWO;
		
		public static String[] getValues() {
			TestEnum[] values = values();
			String[] result = new String[values.length];
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
		AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings, name, "test description", "hubba", true, TestEnum.getValues());
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testInValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings, name, "test description", "hubba", true, TestEnum.getValues());
		System.setProperty(name, "hubba");
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNotRequiredGiven() {
		AndamaSettings settings = new AndamaSettings();
		EnumArgument arg = new EnumArgument(settings, name, "test description", "ONE", false, TestEnum.getValues());
		assertEquals(name, arg.getName());
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(TestEnum.ONE.toString(), arg.getValue());
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		EnumArgument arg = new EnumArgument(settings, name, "test description", null, false, TestEnum.getValues());
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() {
		AndamaSettings settings = new AndamaSettings();
		new EnumArgument(settings, name, "test description", null, true, TestEnum.getValues());
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testValidDefault() {
		AndamaSettings settings = new AndamaSettings();
		EnumArgument arg = new EnumArgument(settings, name, "test description", "TWO", true, TestEnum.getValues());
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO.toString(), arg.getValue());
	}
	
	@Test
	public void testValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		EnumArgument arg = new EnumArgument(settings, name, "test description", null, true, TestEnum.getValues());
		System.setProperty(name, "TWO");
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(TestEnum.TWO.toString(), arg.getValue());
	}
	
}
