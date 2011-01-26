/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.Iterator;
import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.utils.Condition;
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
	private List<LogEntry>   nextEntries  = null;
	private boolean          seenEnd      = false;
	
	public LogIterator(final Repository repository, final String startRevision, final String endRevision,
			final int cacheSize) {
		Condition.notNull(repository);
		Condition.notEquals(cacheSize, 0);
		
		if (startRevision == null) {
			this.startRevision = repository.getFirstRevisionId();
		} else if (startRevision.equals("HEAD")) {
			this.startRevision = repository.getHEADRevisionId();
		} else {
			this.startRevision = startRevision;
		}
		
		if ((endRevision == null) || (endRevision.equals("HEAD"))) {
			this.endRevision = repository.getHEADRevisionId();
		} else {
			this.endRevision = endRevision;
		}
		
		this.repository = repository;
		this.cacheSize = cacheSize;

		String relativeTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize / 2 - 1);
		this.currentEntries = repository.log(this.startRevision, relativeTransactionId);
		
		if (!relativeTransactionId.equals(endRevision)) {
			String nextStartTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize / 2);
			String nextEndTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize - 1);
			this.nextEntries = repository.log(nextStartTransactionId, nextEndTransactionId);
		}
		if (Logger.logDebug()) {
			Logger.debug("LogIterator: endRevision=" + this.endRevision);
		}
		
		Condition
		.notNull(this.currentEntries,
		"The current entries should never be null at this point. This would imply that there is nothing to do.");
		Condition
		.check(!this.currentEntries.isEmpty(),
		"The current entries should never be empty at this point. This would imply that there is nothing to do.");
	}
	
	public boolean done() {
		return this.done;
	}
	
	@Override
	public boolean hasNext() {
		return ((this.currentEntries.size() > 0) && !this.done);
	}
	
	@Override
	public LogEntry next() {
		if (this.done) {
			return null;
		} else {
			LogEntry entry = this.currentEntries.get(this.currentIndex);
			this.currentIndex++;
			
			if (entry.getRevision().equals(this.endRevision) || entry.getRevision().equals(this.repository.getEndRevision())) {
				this.done = true;
			} else {
				
				if (this.currentIndex >= this.currentEntries.size()) {
					if ((this.nextEntries != null) && this.nextEntries.isEmpty()) {
						this.done = true;
						return null;
					} else {
						this.currentEntries = this.nextEntries;
						this.nextEntries = null;
						this.currentIndex = 0;
						update();
					}
				}
			}
			if (Logger.logDebug()) {
				Logger.debug("LogIterator.next(): " + entry.getRevision());
			}
			return entry;
		}
	}
	
	@Override
	public void remove() {
		next();
	}
	
	public synchronized void update() {
		if (Logger.logDebug()) {
			Logger.debug("Fetching next " + this.cacheSize / 2 + " logs.");
		}
		
		String nextStart = this.repository.getRelativeTransactionId(this.currentEntries.get(0).getRevision(), this.cacheSize / 2);
		String nextEnd = this.repository.getRelativeTransactionId(this.currentEntries.get(0).getRevision(), this.cacheSize - 1);
		
		if (Logger.logDebug()) {
			Logger.debug("LogIterator: nextStart=" + nextStart);
			Logger.debug("LogIterator: nextEnd=" + nextEnd);
		}
		
		if (!this.seenEnd) {
			if (nextStart.equals(this.endRevision) || nextEnd.equals(this.endRevision)) {
				this.seenEnd = true;
			}
			this.nextEntries = this.repository.log(nextStart, nextEnd);
		}
	}
	
}
