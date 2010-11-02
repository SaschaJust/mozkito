/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Iterator;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends Thread {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RepositoryReader.class.getSimpleName();
	}
	
	private Iterator<LogEntry> logIterator;
	
	private final Repository   repository;
	
	public RepositoryReader(final Repository repository) {
		this.repository = repository;
	}
	
	public synchronized LogEntry getNext() {
		if (this.logIterator == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		if (this.logIterator.hasNext()) {
			return this.logIterator.next();
		} else {
			return null;
		}
	}
	
	public Repository getRepository() {
		return this.repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		if (Logger.logInfo()) {
			Logger.info("Requesting logs from " + this.repository);
		}
		
		this.repository.getTransactionCount();
		this.logIterator = this.repository.log(this.repository.getFirstRevisionId(),
		        this.repository.getLastRevisionId(), 1000);
		
		if (Logger.logInfo()) {
			Logger.info("Created iterator.");
		}
		wake();
		
		new RCSFileManager();
	}
	
	private synchronized void wake() {
		notifyAll();
	}
}
