/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author just
 * 
 */
public class MoskitoDerivationTest extends MoskitoTest {
	
	@Test
	public void testFail() {
		fail();
	}
	
	@Test
	@Ignore
	public void testIgnore() {
		
	}
	
	@Test
	public void testPass() {
		// fail();
	}
}
