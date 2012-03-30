/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PostProcessHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PostProcessHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new post process hook.
	 *
	 * @param thread the thread
	 */
	public PostProcessHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		postProcess();
	}
	
	/**
	 * Post process.
	 */
	public abstract void postProcess();
}
