/**
 * 
 */
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.messages.EventBus;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface IHook<K, V> {
	
	/**
	 * @return
	 */
	public boolean completed();
	
	public void execute() throws InterruptedException;
	
	/**
	 * Gets the event bus.
	 * 
	 * @return the event bus
	 */
	EventBus getEventBus();
	
	public String getHandle();
	
	/**
	 * should be implemented as 'final'
	 * 
	 * @return
	 */
	public Node<K, V> getThread();
}
