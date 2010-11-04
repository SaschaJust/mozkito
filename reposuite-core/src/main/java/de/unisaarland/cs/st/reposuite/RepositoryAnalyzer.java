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
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends RepositoryThread {
	
	public static String getHandle() {
		return RepositoryAnalyzer.class.getSimpleName();
	}
	
	private final RepositoryReader  reader;
	private Repository              repository;
	private final List<LogEntry>    entries  = new LinkedList<LogEntry>();
	private final Queue<LogEntry>   queue    = new LinkedBlockingQueue<LogEntry>();
	private final RepoSuiteSettings settings;
	private final boolean           shutdown = false;
	private boolean                 analyze;
	
	public RepositoryAnalyzer(final ThreadGroup threadGroup, final RepositoryReader reader,
			final RepoSuiteSettings settings) {
		super(threadGroup, getHandle());
		
		this.reader = reader;
		this.repository = reader.getRepository();
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
		if (!this.shutdown) {
			this.analyze = (this.settings.getSetting("repository.analyze") != null)
			        && (this.settings.getSetting("repository.analyze").getValue() != null)
			        && (Boolean) this.settings.getSetting("repository.analyze").getValue();
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			this.repository = this.reader.getRepository();
			
			LogEntry entry;
			LogIterator iterator = this.reader.getIterator();
			
			while (!this.shutdown && iterator.hasNext()) {
				entry = iterator.next();
				
				if (this.analyze) {
					if (Logger.logDebug()) {
						Logger.debug("Adding " + entry + " to analysis.");
					}
					this.entries.add(entry);
				}
				
				this.queue.add(entry);
				
				wake();
			}
			
			if (!this.shutdown) {
				this.repository.consistencyCheck(this.entries, ((Boolean) this.settings.getSetting("headless")
						.getValue() == false));
			}
		}
	}
}
