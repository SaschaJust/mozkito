/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.messages.EventBus;

/**
 * The Interface IHook.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface IHook<K, V> {
	
	/**
	 * Completed.
	 * 
	 * @return true, if successful
	 */
	public boolean completed();
	
	/**
	 * Execute.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void execute() throws InterruptedException;
	
	/**
	 * Gets the event bus.
	 * 
	 * @return the event bus
	 */
	EventBus getEventBus();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getHandle();
	
	/**
	 * should be implemented as 'final'.
	 * 
	 * @return the thread
	 */
	public Node<K, V> getThread();
}
