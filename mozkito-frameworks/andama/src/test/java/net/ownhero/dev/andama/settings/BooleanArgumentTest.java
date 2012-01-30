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
import net.ownhero.dev.andama.exceptions.Shutdown;

import org.junit.Test;

public class BooleanArgumentTest {
	
	@Test
	public void testGetValue_DefaultFalse_NotRequired() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "fAlse", false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
		assertEquals(false, arg.isRequired());
	}
	
	@Test
	public void testGetValue_DefaultFalse_Required() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "fAlse", true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_NotRequired() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "TRuE", false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_Required() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "trUe", true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_NotRequired() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_Required() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testSetRequired() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, false);
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		arg.setRequired(true);
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		arg.setRequired(false);
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testSetValue() {
		AndamaSettings settings = new AndamaSettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		
		arg.setStringValue("false");
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
		arg.setStringValue(null);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
		arg.setStringValue("tRuE");
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
}
