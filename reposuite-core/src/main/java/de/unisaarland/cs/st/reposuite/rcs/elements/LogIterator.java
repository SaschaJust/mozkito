/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.Iterator;
import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.Repository;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LogIterator implements Iterator<LogEntry> {
	
	private final String     endRevision;
	private final Repository repository;
	private final String     startRevision;
	private List<LogEntry>   entries;
	private int              currentIndex = 0;
	private boolean          done         = false;
	private final int        cacheSize;
	
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
		String relativeTransactionId = repository.getRelativeTransactionId(this.startRevision, cacheSize - 1);
		this.entries = repository.log(this.startRevision, relativeTransactionId);
		
		assert (this.entries != null);
		assert (!this.entries.isEmpty());
	}
	
	@Override
	public boolean hasNext() {
		return ((this.entries.size() > 0) && !this.done);
	}
	
	@Override
	public LogEntry next() {
		if (this.done) {
			return null;
		} else {
			LogEntry entry = this.entries.get(this.currentIndex);
			this.currentIndex++;
			
			if (entry.getRevision().equals(this.endRevision)
			        || entry.getRevision().equals(this.repository.getLastRevisionId())) {
				this.done = true;
			} else {
				
				if (this.currentIndex >= this.entries.size()) {
					this.entries = this.repository.log(
					        this.repository.getRelativeTransactionId(entry.getRevision(), 1),
					        this.repository.getRelativeTransactionId(entry.getRevision(), this.cacheSize));
					this.currentIndex = 0;
				}
			}
			
			return entry;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		
	}
	
}
