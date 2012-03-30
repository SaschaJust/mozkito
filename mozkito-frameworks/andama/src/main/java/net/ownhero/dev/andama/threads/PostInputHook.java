/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PostInputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PostInputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new post input hook.
	 *
	 * @param thread the thread
	 */
	public PostInputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		postInput();
	}
	
	/**
	 * Post input.
	 */
	public abstract void postInput();
}
