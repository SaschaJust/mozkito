/**
 * 
 */
package net.ownhero.dev.andama.observer;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.threads.AndamaThreadable;

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
	
	public Collection<AndamaThreadable<?, ?>> getThreads(final long chainID) {
		return new LinkedList<AndamaThreadable<?, ?>>();
	}
	
	@SuppressWarnings ("unused")
	private Properties loadSettings(final File file) {
		return null;
	}
	
	@SuppressWarnings ("unused")
	private void registerChain(final AndamaChain chain) {
		
	}
	
	@SuppressWarnings ("unused")
	private void spawnChain(final Class<? extends AndamaChain> clazz,
	                        final Properties settings) {
		
	}
	
	@SuppressWarnings ("unused")
	private void unregisterChain(final AndamaChain chain) {
		
	}
	
}
