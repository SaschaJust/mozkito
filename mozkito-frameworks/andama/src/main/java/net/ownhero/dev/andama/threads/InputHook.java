/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class InputHook<K, V> extends AndamaHook<K, V> {
	
	/**
	 * @param thread
	 */
	public InputHook(final AndamaThread<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		input();
	}
	
	/**
	 * 
	 */
	public abstract void input();
	
}
