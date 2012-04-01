/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PostOutputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PostOutputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new post output hook.
	 *
	 * @param thread the thread
	 */
	public PostOutputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		postOutput();
	}
	
	/**
	 * Post output.
	 */
	public abstract void postOutput();
}
