/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class OutputHook<K, V> extends Hook<K, V> {
	
	/**
	 * @param thread
	 */
	public OutputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		output();
	}
	
	public abstract void output();
}
