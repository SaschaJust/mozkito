/**
 * 
 */
package net.ownhero.dev.andama.threads;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaSettings;

import org.junit.Test;

/**
 * @author just
 * 
 */
public class AndamaThreadTest {
	
	@Test
	public void test() {
		final AndamaSettings settings = new AndamaSettings();
		final ExampleThread1 thread = new ExampleThread1(new AndamaGroup("me", new AndamaChain(settings) {
			
			@Override
			public void setup() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shutdown() {
				// TODO Auto-generated method stub
				
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
