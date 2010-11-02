/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.Iterator;
import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LogIterator implements Iterator<LogEntry> {
	
	private final String     endRevision;
	private final Repository repository;
	private final String     startRevision;
	private List<LogEntry>   currentEntries;
	private int              currentIndex = 0;
	private boolean          done         = false;
	private final int        cacheSize;
	private List<LogEntry>   nextEntries;
	private boolean          updatable;
	
	public LogIterator(final Repository repository, final String startRevision, final String endRevision,
	        final int cacheSize) {
		assert (repository != null);
		assert (cacheSize != 0);
		
		if (startRevision == null) {
			this.startRevision = repository.getFirstRevisionId();
		} else {
			this.startRevision = startRevision;
		}
		
		if (endRevision == null) {
			this.endRevision = repository.getLastRevisionId();
		} else {
			this.endRevision = endRevision;
		}
		
		this.repository = repository;
		this.cacheSize = cacheSize;
		
		String relativeTransactionId = repository.getRelativeTransactionId(this.startRevision, cacheSize / 2 - 1);
		this.currentEntries = repository.log(this.startRevision, relativeTransactionId);
		
		String nextStartTransactionId = repository.getRelativeTransactionId(this.startRevision, cacheSize / 2);
		String nextEndTransactionId = repository.getRelativeTransactionId(this.startRevision, cacheSize - 1);
		this.nextEntries = repository.log(nextStartTransactionId, nextEndTransactionId);
		
		assert (this.currentEntries != null);
		assert (!this.currentEntries.isEmpty());
	}
	
	public synchronized boolean done() {
		return this.done;
	}
	
	@Override
	public boolean hasNext() {
		return ((this.currentEntries.size() > 0) && !this.done);
	}
	
	@Override
	public synchronized LogEntry next() {
		if (this.done) {
			return null;
		} else {
			LogEntry entry = this.currentEntries.get(this.currentIndex);
			this.currentIndex++;
			
			if (entry.getRevision().equals(this.endRevision)
			        || entry.getRevision().equals(this.repository.getLastRevisionId())) {
				this.done = true;
			} else {
				
				if (this.currentIndex >= this.currentEntries.size()) {
					if ((this.nextEntries != null) && this.nextEntries.isEmpty()) {
						this.done = true;
						return null;
					} else {
						if (this.updatable) {
							try {
								wait();
							} catch (InterruptedException e) {
								
								if (Logger.logError()) {
									Logger.error(e.getMessage(), e);
								}
							}
						}
						
						this.currentEntries = this.nextEntries;
						this.nextEntries = null;
						this.currentIndex = 0;
						this.updatable = true;
						notifyAll();
					}
				}
			}
			
			return entry;
		}
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized void update() {
		if (!this.updatable) {
			try {
				wait();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Fetching next " + this.cacheSize / 2 + "logs");
		}
		
		this.nextEntries = this.repository.log(
		        this.repository.getRelativeTransactionId(this.currentEntries.get(0).getRevision(), this.cacheSize / 2),
		        this.repository.getRelativeTransactionId(this.currentEntries.get(0).getRevision(), this.cacheSize - 1));
		
		notifyAll();
		
	}
	
}
