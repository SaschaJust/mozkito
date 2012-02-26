/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DefaultInputHook<K, V> extends InputHook<K, V> {
	
	/**
	 * @param thread
	 */
	public DefaultInputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.InputHook#input()
	 */
	@Override
	public void input() {
		try {
			getThread().readNext();
			
			if (Logger.logDebug()) {
				Logger.debug("Reading input data: " + getThread().getInputData());
			}
			
			setCompleted();
		} catch (final InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
