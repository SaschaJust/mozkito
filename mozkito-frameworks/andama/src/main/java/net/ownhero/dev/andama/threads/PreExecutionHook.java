/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class PreExecutionHook<K, V> extends AndamaHook<K, V> {
	
	public PreExecutionHook(final AndamaThread<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		preExecution();
	}
	
	/**
	 * 
	 */
	public abstract void preExecution();
}
