/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

/**
 * @author just
 * 
 */
public class MoskitoDerivationTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testFail() {
		fail();
	}
	
	@Test
	@Ignore
	@DatabaseSettings (unit = "rcs")
	public void testIgnore() {
		
	}
	
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testPass() {
		// fail();
	}
}
