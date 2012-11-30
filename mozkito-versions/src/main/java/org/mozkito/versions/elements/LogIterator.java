/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.versions.elements;

import java.util.Iterator;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.versions.Repository;

/**
 * The Class LogIterator.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class LogIterator implements Iterator<LogEntry> {
	
	/** The log iterator. */
	private final Iterator<LogEntry> logIterator;
	
	/**
	 * Instantiates a new log iterator.
	 *
	 * @param repository the repository
	 * @param startRevision the start revision
	 * @param endRevision the end revision
	 */
	public LogIterator(@NotNull final Repository repository, final String startRevision, final String endRevision) {
		this.logIterator = repository.log(startRevision, endRevision).iterator();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.logIterator.hasNext();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public LogEntry next() {
		return this.logIterator.next();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		
	}
	
}
