/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class PostInputHook<K, V> extends Hook<K, V> {
	
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
	 * 
	 */
	public abstract void postInput();
}
