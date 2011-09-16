/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Hook<K, V> {
	
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
	public AndamaThread<K, V> getThread();
	
}
