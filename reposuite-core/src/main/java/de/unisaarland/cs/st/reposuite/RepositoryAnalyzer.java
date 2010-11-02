/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends Thread {
	
	public static String getHandle() {
		return RepositoryAnalyzer.class.getSimpleName();
	}
	
	private final RepositoryReader reader;
	private Repository             repository;
	private final List<LogEntry>   entries = new LinkedList<LogEntry>();
	private final Queue<LogEntry>  queue   = new LinkedBlockingQueue<LogEntry>();
	
	public RepositoryAnalyzer(final RepositoryReader reader) {
		this.reader = reader;
		repository = reader.getRepository();
	}
	
	public synchronized LogEntry getNext() {
		if (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		if (queue.isEmpty()) {
			return null;
		} else {
			return queue.poll();
		}
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	@Override
	public void run() {
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		repository = reader.getRepository();
		
		LogEntry entry;
		LogIterator iterator = reader.getIterator();
		
		while (iterator.hasNext()) {
			entry = iterator.next();
			if (Logger.logInfo()) {
				Logger.info("Adding " + entry + " to analysis.");
			}
			queue.add(entry);
			entries.add(entry);
			wake();
		}
		
		repository.consistencyCheck(entries);
		//,((Boolean) this.settings.getSetting("headless").getValue() == false));
		
	}
	
	private synchronized void wake() {
		notifyAll();
	}
}
