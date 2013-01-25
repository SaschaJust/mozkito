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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeSetIterator.
 */
public class ChangeSetIterator implements Iterable<ChangeSet>, Iterator<ChangeSet> {
	
	/**
	 * The Enum ChangeSetOrder.
	 */
	public enum ChangeSetOrder {
		
		/** The asc. */
		ASC,
		/** The desc. */
		DESC;
	}
	
	/**
	 * From rev dep iterator.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param revDepIterator
	 *            the rev dep iterator
	 * @return the change set iterator
	 */
	public static ChangeSetIterator fromRevDepIterator(final PersistenceUtil persistenceUtil,
	                                                   final RevDepIterator revDepIterator) {
		return fromRevDepIterator(persistenceUtil, revDepIterator, ChangeSetOrder.DESC);
	}
	
	/**
	 * From rev dep iterator.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param revDepIterator
	 *            the rev dep iterator
	 * @param order
	 *            the order. DESC means from the last transaction to previous transactions.
	 * @return the change set iterator
	 */
	public static ChangeSetIterator fromRevDepIterator(final PersistenceUtil persistenceUtil,
	                                                   final RevDepIterator revDepIterator,
	                                                   final ChangeSetOrder order) {
		if (order.equals(ChangeSetOrder.DESC)) {
			return new ChangeSetIterator(persistenceUtil, revDepIterator);
		}
		final List<String> reverseList = new LinkedList<String>();
		for (final String s : revDepIterator) {
			reverseList.add(s);
		}
		Collections.reverse(reverseList);
		return new ChangeSetIterator(persistenceUtil, reverseList.iterator());
	}
	
	/** The rev dep iterator. */
	private final Iterator<String> revDepIterator;
	
	/** The persistence util. */
	private final PersistenceUtil  persistenceUtil;
	
	/**
	 * Instantiates a new change set iterator.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param revDepIterator
	 *            the rev dep iterator
	 */
	private ChangeSetIterator(final PersistenceUtil persistenceUtil, final Iterator<String> revDepIterator) {
		this.persistenceUtil = persistenceUtil;
		this.revDepIterator = revDepIterator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.revDepIterator.hasNext();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ChangeSet> iterator() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ChangeSet next() {
		final String nextChangeSetHash = this.revDepIterator.next();
		return this.persistenceUtil.loadById(nextChangeSetHash, ChangeSet.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		this.revDepIterator.remove();
	}
	
}
