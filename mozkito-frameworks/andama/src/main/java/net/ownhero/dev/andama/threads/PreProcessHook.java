/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PreProcessHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PreProcessHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new pre process hook.
	 *
	 * @param thread the thread
	 */
	public PreProcessHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		preProcess();
	}
	
	/**
	 * Pre process.
	 */
	public abstract void preProcess();
}
