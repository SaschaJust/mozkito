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
 * @author just
 * 
 */
public class Observer {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public double getProgress(final long chainID) {
		return 0d;
	}
	
	public Date getRunningTime(final long chainID) {
		return null;
	}
	
	public Thread.State getState(final long chaindID) {
		return null;
	}
	
	public Collection<INode<?, ?>> getThreads(final long chainID) {
		return new LinkedList<INode<?, ?>>();
	}
	
	@SuppressWarnings ("unused")
	private Properties loadSettings(final File file) {
		return null;
	}
	
	@SuppressWarnings ("unused")
	private void registerChain(final Chain chain) {
		
	}
	
	@SuppressWarnings ("unused")
	private void spawnChain(final Class<? extends Chain> clazz,
	                        final Properties settings) {
		
	}
	
	@SuppressWarnings ("unused")
	private void unregisterChain(final Chain chain) {
		
	}
	
}
