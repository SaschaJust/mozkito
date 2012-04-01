/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class DefaultOutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class DefaultOutputHook<K, V> extends OutputHook<K, V> {
	
	/**
	 * Instantiates a new default output hook.
	 *
	 * @param thread the thread
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
		} catch (final InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
