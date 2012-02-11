/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class PreProcessHook<K, V> extends Hook<K, V> {
	
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
	 * 
	 */
	public abstract void preProcess();
}
