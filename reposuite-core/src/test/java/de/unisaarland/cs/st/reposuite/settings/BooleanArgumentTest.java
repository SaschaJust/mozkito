package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BooleanArgumentTest {
	
	@Test
	public void testGetValue_DefaultFalse_NotRequired() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "fAlse", false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.getValue());
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testGetValue_DefaultFalse_Required() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "fAlse", true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(false, arg.getValue());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testGetValue_DefaultTrue_NotRequired() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "TRuE", false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.getValue());
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testGetValue_DefaultTrue_Required() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", "trUe", true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(true, arg.getValue());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testGetValue_NoDefault_NotRequired() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, false);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(null, arg.getValue());
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testGetValue_NoDefault_Required() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, true);
		assertEquals("this is only a test argument", arg.getDescription());
		assertEquals("testArg", arg.getName());
		assertEquals(null, arg.getValue());
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testSetRequired() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, false);
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
		arg.setRequired(true);
		assertEquals(true, arg.isRequired());
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		arg.setRequired(false);
		assertEquals(false, arg.isRequired());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
	
	@Test
	public void testSetValue() {
		RepositorySettings settings = new RepositorySettings();
		BooleanArgument arg = new BooleanArgument(settings, "testArg", "this is only a test argument", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		arg.setStringValue("false");
		assertEquals(false, arg.getValue());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
		arg.setStringValue(null);
		assertEquals(null, arg.getValue());
		try {
			settings.parseArguments();
			fail();
		} catch (RuntimeException e) {
			
		}
		arg.setStringValue("tRuE");
		assertEquals(true, arg.getValue());
		try {
			settings.parseArguments();
		} catch (RuntimeException e) {
			fail();
		}
	}
}
