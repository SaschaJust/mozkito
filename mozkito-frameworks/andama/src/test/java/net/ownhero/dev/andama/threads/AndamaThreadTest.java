/**
 * 
 */
package net.ownhero.dev.andama.threads;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.settings.Settings;

import org.junit.Test;

/**
 * @author just
 * 
 */
public class AndamaThreadTest {
	
	@Test
	public void test() {
		final Settings settings = new Settings();
		final ExampleThread1 thread = new ExampleThread1(new Group("me", new Chain<Settings>(settings) {
			
			@Override
			public void setup() {
				
			}
			
		}), settings, false);
		
		try {
			System.err.println(thread.getInputType());
			System.err.println(thread.getOutputType());
		} catch (final Throwable e) {
			fail("Type resolving failed.");
		}
	}
	
}
