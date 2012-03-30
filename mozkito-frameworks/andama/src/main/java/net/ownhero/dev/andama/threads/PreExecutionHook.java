/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PreExecutionHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PreExecutionHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new pre execution hook.
	 *
	 * @param thread the thread
	 */
	public PreExecutionHook(final Node<K, V> thread) {
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
	 * Pre execution.
	 */
	public abstract void preExecution();
}
