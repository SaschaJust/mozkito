/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DefaultInputHook<K, V> extends InputHook<K, V> {
	
	/**
	 * @param thread
	 */
	public DefaultInputHook(final AndamaThread<K, V> thread) {
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
		} catch (InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
