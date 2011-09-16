/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DefaultOutputHook<K, V> extends OutputHook<K, V> {
	
	/**
	 * @param thread
	 */
	public DefaultOutputHook(final AndamaThread<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.OutputHook#output()
	 */
	@Override
	public void output() {
		try {
			getThread().write(getThread().getOutputData());
		} catch (InterruptedException e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
