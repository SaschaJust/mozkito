/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class PreInputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class PreInputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new pre input hook.
	 *
	 * @param thread the thread
	 */
	public PreInputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		preInput();
	}
	
	/**
	 * Pre input.
	 */
	public abstract void preInput();
}
