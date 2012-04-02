/**
 * 
 */
package net.ownhero.dev.andama.observer;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.threads.INode;

/**
 * The Class Observer.
 * 
 * @author just
 */
public class Observer {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Gets the progress.
	 * 
	 * @param chainID
	 *            the chain id
	 * @return the progress
	 */
	public double getProgress(final long chainID) {
		return 0d;
	}
	
	/**
	 * Gets the running time.
	 * 
	 * @param chainID
	 *            the chain id
	 * @return the running time
	 */
	public Date getRunningTime(final long chainID) {
		return null;
	}
	
	/**
	 * Gets the state.
	 * 
	 * @param chaindID
	 *            the chaind id
	 * @return the state
	 */
	public Thread.State getState(final long chaindID) {
		return null;
	}
	
	/**
	 * Gets the threads.
	 * 
	 * @param chainID
	 *            the chain id
	 * @return the threads
	 */
	public Collection<INode<?, ?>> getThreads(final long chainID) {
		return new LinkedList<INode<?, ?>>();
	}
	
	/**
	 * Load settings.
	 * 
	 * @param file
	 *            the file
	 * @return the properties
	 */
	@SuppressWarnings ("unused")
	private Properties loadSettings(final File file) {
		return null;
	}
	
	/**
	 * Register chain.
	 * 
	 * @param chain
	 *            the chain
	 */
	@SuppressWarnings ("unused")
	private void registerChain(final Chain<?> chain) {
		// stub
	}
	
	/**
	 * Spawn chain.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param settings
	 *            the settings
	 */
	@SuppressWarnings ("unused")
	private void spawnChain(final Class<? extends Chain<?>> clazz,
	                        final Properties settings) {
		// stub
	}
	
	/**
	 * Unregister chain.
	 * 
	 * @param chain
	 *            the chain
	 */
	@SuppressWarnings ("unused")
	private void unregisterChain(final Chain<?> chain) {
		// stub
	}
	
}
