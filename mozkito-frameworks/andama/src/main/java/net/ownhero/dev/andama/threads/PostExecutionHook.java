/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PostExecutionHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PostExecutionHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new post execution hook.
	 *
	 * @param thread the thread
	 */
	public PostExecutionHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		postExecution();
	}
	
	/**
	 * Post execution.
	 */
	public abstract void postExecution();
	
}
