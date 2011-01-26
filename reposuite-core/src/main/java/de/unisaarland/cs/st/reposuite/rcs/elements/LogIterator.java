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
	private List<LogEntry>   nextEntries;
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
		//		this.cacheSize = cacheSize;
		//FIXME replace by cache value again
		this.cacheSize = 100000;
		
		String relativeTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize / 2 - 1);
		currentEntries = repository.log(this.startRevision, relativeTransactionId);
		
		//FIXME check if necessary
		String nextStartTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize / 2);
		String nextEndTransactionId = repository.getRelativeTransactionId(this.startRevision, this.cacheSize - 1);
		nextEntries = repository.log(nextStartTransactionId, nextEndTransactionId);
		
		if (Logger.logDebug()) {
			Logger.debug("LogIterator: endRevision=" + this.endRevision);
		}
		
		Condition.notNull(currentEntries);
		Condition.check(!currentEntries.isEmpty());
	}
	
	public boolean done() {
		return done;
	}
	
	@Override
	public boolean hasNext() {
		return ((currentEntries.size() > 0) && !done);
	}
	
	@Override
	public LogEntry next() {
		if (done) {
			return null;
		} else {
			LogEntry entry = currentEntries.get(currentIndex);
			currentIndex++;
			
			if (entry.getRevision().equals(endRevision) || entry.getRevision().equals(repository.getEndRevision())) {
				done = true;
			} else {
				
				if (currentIndex >= currentEntries.size()) {
					if ((nextEntries != null) && nextEntries.isEmpty()) {
						done = true;
						return null;
					} else {
						currentEntries = nextEntries;
						nextEntries = null;
						currentIndex = 0;
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
			Logger.debug("Fetching next " + cacheSize / 2 + " logs.");
		}
		
		String nextStart = repository.getRelativeTransactionId(currentEntries.get(0).getRevision(), cacheSize / 2);
		String nextEnd = repository.getRelativeTransactionId(currentEntries.get(0).getRevision(), cacheSize - 1);
		
		if (Logger.logDebug()) {
			Logger.debug("LogIterator: nextStart=" + nextStart);
			Logger.debug("LogIterator: nextEnd=" + nextEnd);
		}
		
		if (!seenEnd) {
			if (nextStart.equals(endRevision) || nextEnd.equals(endRevision)) {
				seenEnd = true;
			}
			nextEntries = repository.log(nextStart, nextEnd);
		}
	}
	
}
