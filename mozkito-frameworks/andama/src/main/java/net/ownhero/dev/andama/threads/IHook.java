/**
 * 
 */
package net.ownhero.dev.andama.threads;

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
	
	public String getHandle();
	
	/**
	 * should be implemented as 'final'
	 * 
	 * @return
	 */
	public Node<K, V> getThread();
	
}
