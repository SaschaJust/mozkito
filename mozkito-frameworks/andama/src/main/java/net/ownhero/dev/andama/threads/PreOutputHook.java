/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PreOutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PreOutputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new pre output hook.
	 *
	 * @param thread the thread
	 */
	public PreOutputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		preOutput();
	}
	
	/**
	 * Pre output.
	 */
	public abstract void preOutput();
}
