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
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;

import org.junit.Test;

public class BooleanArgumentTest {
	
	@Test
	public void testGetValue_DefaultFalse_NotRequired() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "fAlse", new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
		assertEquals(false, arg.required());
	}
	
	@Test
	public void testGetValue_DefaultFalse_Required() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "fAlse", new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_NotRequired() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "TRuE", new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.required());
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_DefaultTrue_Required() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", "trUe", new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_NotRequired() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Optional());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.required());
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testGetValue_NoDefault_Required() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Required());
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.required());
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testSetValue() {
		final AndamaSettings settings = new AndamaSettings();
		final BooleanArgument arg = new BooleanArgument(settings.getRootArgumentSet(), "testArg",
		                                                "this is only a test argument", null, new Required());
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
		
		arg.setStringValue("false");
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(false, arg.getValue());
		arg.setStringValue(null);
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
		arg.setStringValue("tRuE");
		try {
			settings.parseArguments();
		} catch (final Shutdown e) {
			fail();
		}
		assertEquals(true, arg.getValue());
	}
}
