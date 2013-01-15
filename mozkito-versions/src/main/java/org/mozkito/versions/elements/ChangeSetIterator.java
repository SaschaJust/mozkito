/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
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
package org.mozkito.versions.elements;

import java.util.Iterator;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeSetIterator.
 */
public class ChangeSetIterator implements Iterable<ChangeSet>, Iterator<ChangeSet> {
	
	private final RevDepIterator  revDepIterator;
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * Instantiates a new change set iterator.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param revDepIterator
	 *            the rev dep iterator
	 */
	public ChangeSetIterator(final PersistenceUtil persistenceUtil, final RevDepIterator revDepIterator) {
		this.persistenceUtil = persistenceUtil;
		this.revDepIterator = revDepIterator;
	}
	
	@Override
	public boolean hasNext() {
		return this.revDepIterator.hasNext();
	}
	
	@Override
	public Iterator<ChangeSet> iterator() {
		return this;
	}
	
	@Override
	public ChangeSet next() {
		final String nextChangeSetHash = this.revDepIterator.next();
		return this.persistenceUtil.loadById(nextChangeSetHash, ChangeSet.class);
	}
	
	@Override
	public void remove() {
		this.revDepIterator.remove();
	}
	
}
