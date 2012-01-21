package net.ownhero.dev.andama.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.Shutdown;

import org.junit.After;
import org.junit.Test;


public class ListArgumentTest {
	
	private static String listString = "one,two";
	
	private static String name       = "listArg";
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidPropertiesDelimiter() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", null, true, "@");
		System.setProperty(name, listString);
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(1, arg.getValue().size());
		assertTrue(arg.getValue().contains("one,two"));
	}
	
	@Test
	public void testNotRequiredGiven() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", listString, true);
		assertEquals(name, arg.getName());
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", null, false);
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() {
		AndamaSettings settings = new AndamaSettings();
		new ListArgument(settings, name, "test description", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testValidDefault() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", listString, true);
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", null, true);
		System.setProperty(name, listString);
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}
	
	@Test
	public void testValidPropertiesDelimiter() {
		AndamaSettings settings = new AndamaSettings();
		ListArgument arg = new ListArgument(settings, name, "test description", null, true, "@");
		System.setProperty(name, "one@two");
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2, arg.getValue().size());
		assertTrue(arg.getValue().contains("one"));
		assertTrue(arg.getValue().contains("two"));
	}

}
