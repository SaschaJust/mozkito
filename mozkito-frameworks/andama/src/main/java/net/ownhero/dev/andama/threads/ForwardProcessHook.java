/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.kisa.Logger;

/**
 * @author just
 * 
 */
public class ForwardProcessHook<K> extends ProcessHook<K, K> {
	
	/**
	 * @param thread
	 */
	public ForwardProcessHook(final Node<K, K> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
	 */
	@Override
	public void process() {
		final K data = getThread().getInputData();
		
		if (Logger.logDebug()) {
			Logger.debug("Providing output data: " + data);
		}
		provideOutputData(data);
	}
	
}
