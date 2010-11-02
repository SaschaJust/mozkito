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
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends Thread {
	
	public static String getHandle() {
		return RepositoryAnalyzer.class.getSimpleName();
	}
	
	private final RepositoryReader  reader;
	private Repository              repository;
	private final List<LogEntry>    entries = new LinkedList<LogEntry>();
	private final Queue<LogEntry>   queue   = new LinkedBlockingQueue<LogEntry>();
	private final RepoSuiteSettings settings;
	
	public RepositoryAnalyzer(final RepositoryReader reader, final RepoSuiteSettings settings) {
		this.reader = reader;
		this.settings = settings;
	}
	
	public synchronized LogEntry getNext() {
		if (this.queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		if (this.queue.isEmpty()) {
			return null;
		} else {
			return this.queue.poll();
		}
	}
	
	public Repository getRepository() {
		return this.repository;
	}
	
	@Override
	public void run() {
		this.repository = this.reader.getRepository();
		
		LogEntry entry;
		
		while ((entry = this.reader.getNext()) != null) {
			
			this.queue.add(entry);
			this.entries.add(entry);
			notify();
		}
		
		this.repository.consistencyCheck(null, ((Boolean) this.settings.getSetting("headless").getValue() == false));
		
	}
}
