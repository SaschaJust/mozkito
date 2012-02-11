/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.util.Collection;

import net.ownhero.dev.andama.settings.Settings;

/**
 * @author just
 * 
 */
public class ExampleThread1 extends Transformer<Collection<String>, Integer> {
	
	public ExampleThread1(final Group threadGroup, final Settings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
}
