/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DefaultOutputHook<K, V> extends OutputHook<K, V> {
	
	/**
	 * @param thread
	 */
	public DefaultOutputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.OutputHook#output()
	 */
	@Override
	public void output() {
		try {
			
			if (Logger.logDebug()) {
				Logger.debug("Providing output data: " + getThread().getOutputData());
			}
			
			getThread().write(getThread().getOutputData());
			setCompleted();
		} catch (InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
