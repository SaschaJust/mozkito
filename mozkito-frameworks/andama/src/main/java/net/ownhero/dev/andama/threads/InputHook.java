/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * The Class InputHook.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class InputHook<K, V> extends Hook<K, V> {
	
	/**
	 * Instantiates a new input hook.
	 *
	 * @param thread the thread
	 */
	public InputHook(final Node<K, V> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.Hook#execute()
	 */
	@Override
	public final void execute() throws InterruptedException {
		input();
	}
	
	/**
	 * Input.
	 */
	public abstract void input();
	
}
