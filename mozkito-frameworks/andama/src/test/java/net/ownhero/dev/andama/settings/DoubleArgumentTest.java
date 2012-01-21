package net.ownhero.dev.andama.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.Shutdown;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DoubleArgumentTest {
	
	private static String name = "doubleArg";
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(name);
	}
	
	@Test
	public void testInValidDefault() {
		AndamaSettings settings = new AndamaSettings();
		new DoubleArgument(settings, name, "test description", "hubba", true);
		try{
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testInValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		new DoubleArgument(settings, name, "test description", null, true);
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
		DoubleArgument arg = new DoubleArgument(settings, name, "test description", "2.5", false);
		assertEquals(name, arg.getName());
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(),0);
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		DoubleArgument arg = new DoubleArgument(settings, name, "test description", null, false);
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() {
		AndamaSettings settings = new AndamaSettings();
		new DoubleArgument(settings, name, "test description", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testValidDefault() {
		AndamaSettings settings = new AndamaSettings();
		DoubleArgument arg = new DoubleArgument(settings, name, "test description", "2.5", true);
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(), 0);
	}
	
	@Test
	public void testValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		DoubleArgument arg = new DoubleArgument(settings, name, "test description", null, true);
		System.setProperty(name, "2.5");
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(2.5, arg.getValue(), 0);
	}
	
	
}
