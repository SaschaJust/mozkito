/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * @author just
 * 
 */
public class ExampleThread1 extends AndamaTransformer<Collection<String>, Integer> {
	
	public ExampleThread1(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
}
