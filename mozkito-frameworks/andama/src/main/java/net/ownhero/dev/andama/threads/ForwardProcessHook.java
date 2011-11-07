/**
 * 
 */
package net.ownhero.dev.andama.threads;

/**
 * @author just
 * 
 */
public class ForwardProcessHook<K> extends ProcessHook<K, K> {
	
	/**
	 * @param thread
	 */
	public ForwardProcessHook(AndamaThread<K, K> thread) {
		super(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
	 */
	@Override
	public void process() {
		K data = getThread().getInputData();
		
		provideOutputData(data);
	}
	
}
