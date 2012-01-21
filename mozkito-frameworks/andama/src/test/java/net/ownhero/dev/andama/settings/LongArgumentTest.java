package net.ownhero.dev.andama.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.ownhero.dev.andama.exceptions.Shutdown;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LongArgumentTest {
	
	private static String name   = "longArg";
	private static Long   myLong = new Long(Integer.MAX_VALUE + 10l);
	
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
		new LongArgument(settings, name, "test description", "2.5", true);
		try{
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testInValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		new LongArgument(settings, name, "test description", null, true);
		System.setProperty(name, "2.5");
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNotRequiredGiven() {
		AndamaSettings settings = new AndamaSettings();
		LongArgument arg = new LongArgument(settings, name, "test description", String.valueOf(myLong), false);
		assertEquals(name, arg.getName());
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(myLong, arg.getValue(), 0);
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		LongArgument arg = new LongArgument(settings, name, "test description", null, false);
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredProperties() {
		AndamaSettings settings = new AndamaSettings();
		new LongArgument(settings, name, "test description", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testValidDefault() {
		AndamaSettings settings = new AndamaSettings();
		LongArgument arg = new LongArgument(settings, name, "test description", String.valueOf(myLong), true);
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(myLong, arg.getValue(), 0);
	}
	
	@Test
	public void testValidProperties() {
		AndamaSettings settings = new AndamaSettings();
		LongArgument arg = new LongArgument(settings, name, "test description", null, true);
		System.setProperty(name, String.valueOf(myLong));
		try {
			settings.parseArguments();
		} catch (Exception e) {
			fail();
		}
		assertEquals(myLong, arg.getValue(), 0);
	}
	
	
}
