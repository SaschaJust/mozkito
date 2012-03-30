/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class OutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class OutputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new output hook.
	 *
	 * @param thread the thread
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
	
	/**
	 * Output.
	 */
	public abstract void output();
}
