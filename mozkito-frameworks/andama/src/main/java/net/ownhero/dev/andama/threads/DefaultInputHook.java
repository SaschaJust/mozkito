/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class DefaultInputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class DefaultInputHook<K, V> extends InputHook<K, V> {
	
	/**
	 * Instantiates a new default input hook.
	 *
	 * @param thread the thread
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
