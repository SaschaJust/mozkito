/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class ProcessHook<K, V> extends AndamaHook<K, V> {
	
	/**
	 * @param thread
	 */
	public ProcessHook(final AndamaThread<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		getThread().setOutputData(null);
		process();
	}
	
	/**
	 * 
	 */
	public abstract void process();
	
	/**
	 * @param data
	 */
	public final void provideOutputData(final V data) {
		if (AndamaSource.class.isAssignableFrom(getThread().getClass())) {
			if (Logger.logWarn()) {
				Logger.warn("You are calling `provideOutputData` within a ProcessHook of an AndamaSource node. This will cause the node to provide no more data. You might wanna use `providePartialOutputData` or `provideOutputData(data, false)` instead.");
			}
		}
		provideOutputData(data, true);
	}
	
	/**
	 * @param data
	 */
	public final void providePartialOutputData(final V data) {
		provideOutputData(data, false);
	}
	
	/**
	 * @param data
	 * @param fetchNext
	 */
	public final void provideOutputData(final V data,
	                                    final boolean fetchNext) {
		if (fetchNext) {
			getThread().setOutputData(data);
			setCompleted();
		} else {
			getThread().setOutputData(data);
			unsetCompleted();
		}
	}
	
	/**
	 * 
	 */
	public final void skipOutputData() {
		getThread().setSkipData(true);
	}
	
	/**
	 * @param data
	 */
	public final void skipOutputData(final V data) {
		getThread().setOutputData(data);
		getThread().setSkipData(true);
	}
	
}
