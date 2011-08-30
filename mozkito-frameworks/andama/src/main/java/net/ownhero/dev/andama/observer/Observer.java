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
import net.ownhero.dev.andama.threads.AndamaThread;

/**
 * @author just
 * 
 */
public class Observer {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public Date getRunningTime(long chainID) {
		return null;
	}
	
	public Thread.State getState(long chaindID) {
		return null;
	}
	
	public Collection<AndamaThread<?, ?>> getThreads(long chainID) {
		return new LinkedList<AndamaThread<?, ?>>();
	}
	
	public double getProgress(long chainID) {
		return 0d;
	}
	
	private void registerChain(AndamaChain chain) {
		
	}
	
	private void unregisterChain(AndamaChain chain) {
		
	}
	
	private void spawnChain(Class<? extends AndamaChain> clazz, Properties settings) {
		
	}
	
	private Properties loadSettings(File file) {
		return null;
	}
	
}
