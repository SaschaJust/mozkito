/**
 * 
 */
package net.ownhero.dev.andama.threads;

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
		getThread().setOutputData(data);
	}
	
	/**
	 * @param data
	 * @param fetchNext
	 */
	public final void provideOutputData(final V data,
	                                    final boolean fetchNext) {
		if (fetchNext) {
			provideOutputData(data);
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
