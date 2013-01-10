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
package org.mozkito.versions.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionSet is a sorted set of transactions. All transactions in such a set must share at least one
 * common branch. Transactions not sharing a commong branch are not comparable.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionSet implements SortedSet<ChangeSet>, Comparator<ChangeSet> {
	
	/**
	 * The Enum TransactionSetOrder.
	 * 
	 * @author Kim Herzig <herzig@mozkito.org>
	 */
	public enum TransactionSetOrder {
		
		/** The ASC. */
		ASC,
		/** The DESC. */
		DESC
	}
	
	/** The order. */
	private final TransactionSetOrder     order;
	
	/** The branch intersection. */
	private final Set<String>             branchIntersection = new HashSet<String>();
	
	/** The tree set. */
	private final TreeSet<ChangeSet> treeSet;
	
	/**
	 * Instantiates a new transaction set.
	 * 
	 * @param order
	 *            the order
	 */
	public TransactionSet(final TransactionSetOrder order) {
		this.order = order;
		this.treeSet = new TreeSet<ChangeSet>((Comparator<ChangeSet>) this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	@NoneNull
	public boolean add(final ChangeSet e) {
		// PRECONDITIONS
		
		try {
			
			if (Logger.logTrace()) {
				Logger.trace("Adding " + e + " into TransactionSet.");
			}
			
			final Set<String> branchNames = e.getBranchNames();
			if (this.branchIntersection.isEmpty()) {
				this.branchIntersection.addAll(branchNames);
			} else {
				@SuppressWarnings ("unchecked")
				final Collection<String> intersection = CollectionUtils.intersection(this.branchIntersection,
				                                                                     branchNames);
				if (intersection.isEmpty()) {
					throw new IllegalArgumentException("The Transaction " + e.getId()
					        + " is not comparable to existing elements. " + "Transaction within the same "
					        + "TransactionSet must share at " + "least one commong branch. "
					        + "Exception according to Java specification on non-comparable objects within collections.");
				}
				this.branchIntersection.clear();
				this.branchIntersection.addAll(intersection);
			}
			return this.treeSet.add(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends ChangeSet> c) {
		// PRECONDITIONS
		boolean changed = false;
		try {
			for (final ChangeSet t : c) {
				changed |= add(t);
			}
			return changed;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		// PRECONDITIONS
		
		try {
			this.treeSet.clear();
			this.branchIntersection.clear();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#comparator()
	 */
	@Override
	public Comparator<? super ChangeSet> comparator() {
		// PRECONDITIONS
		
		try {
			return this;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final ChangeSet o1,
	                   final ChangeSet o2) {
		// PRECONDITIONS
		
		try {
			@SuppressWarnings ("unchecked")
			final Collection<String> intersection = CollectionUtils.intersection(o1.getBranchNames(),
			                                                                     o2.getBranchNames());
			if (intersection.isEmpty()) {
				throw new IllegalArgumentException("The Transaction " + o1.getId() + " and " + o2.getId()
				        + " are not comparable. Both transactions must share at least one RCSBranch. "
				        + "Exception according to Java specification on non-comparable objects within collections.");
			}
			final String branchName = intersection.iterator().next();
			
			if (this.order.equals(TransactionSetOrder.ASC)) {
				return o1.getBranchIndices().get(branchName).compareTo(o2.getBranchIndices().get(branchName));
			}
			return o2.getBranchIndices().get(branchName).compareTo(o1.getBranchIndices().get(branchName));
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		// PRECONDITIONS
		
		try {
			return this.treeSet.contains(o);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		// PRECONDITIONS
		
		try {
			return this.treeSet.containsAll(c);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#first()
	 */
	@Override
	public ChangeSet first() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.first();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#headSet(java.lang.Object)
	 */
	@Override
	public SortedSet<ChangeSet> headSet(final ChangeSet arg0) {
		// PRECONDITIONS
		
		try {
			final TransactionSet result = new TransactionSet(this.order);
			result.addAll(this.treeSet.headSet(arg0));
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<ChangeSet> iterator() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.iterator();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#last()
	 */
	@Override
	public ChangeSet last() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.last();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public boolean remove(final Object o) {
		// PRECONDITIONS
		
		try {
			if (!this.treeSet.remove(o)) {
				return false;
			}
			// Now we have to recompute the branch intersection
			final Iterator<ChangeSet> iter = this.treeSet.iterator();
			Collection<String> intersection = new HashSet<String>();
			while (iter.hasNext()) {
				intersection = CollectionUtils.intersection(intersection, iter.next().getBranchNames());
			}
			this.branchIntersection.clear();
			this.branchIntersection.addAll(intersection);
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public boolean removeAll(final Collection<?> c) {
		// PRECONDITIONS
		
		try {
			if (!this.treeSet.removeAll(c)) {
				return false;
			}
			// Now we have to recompute the branch intersection
			final Iterator<ChangeSet> iter = this.treeSet.iterator();
			Collection<String> intersection = new HashSet<String>();
			while (iter.hasNext()) {
				intersection = CollectionUtils.intersection(intersection, iter.next().getBranchNames());
			}
			this.branchIntersection.clear();
			this.branchIntersection.addAll(intersection);
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public boolean retainAll(final Collection<?> c) {
		// PRECONDITIONS
		
		try {
			if (!this.treeSet.retainAll(c)) {
				return false;
			}
			// Now we have to recompute the branch intersection
			final Iterator<ChangeSet> iter = this.treeSet.iterator();
			Collection<String> intersection = new HashSet<String>();
			while (iter.hasNext()) {
				intersection = CollectionUtils.intersection(intersection, iter.next().getBranchNames());
			}
			this.branchIntersection.clear();
			this.branchIntersection.addAll(intersection);
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
	 */
	@Override
	public SortedSet<ChangeSet> subSet(final ChangeSet arg0,
	                                        final ChangeSet arg1) {
		// PRECONDITIONS
		
		try {
			final TransactionSet result = new TransactionSet(this.order);
			result.addAll(this.treeSet.subSet(arg0, arg1));
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.SortedSet#tailSet(java.lang.Object)
	 */
	@Override
	public SortedSet<ChangeSet> tailSet(final ChangeSet arg0) {
		// PRECONDITIONS
		
		try {
			final TransactionSet result = new TransactionSet(this.order);
			result.addAll(this.treeSet.tailSet(arg0));
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		// PRECONDITIONS
		
		try {
			return this.treeSet.toArray();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T> T[] toArray(final T[] a) {
		// PRECONDITIONS
		
		try {
			return (T[]) this.treeSet.toArray(new ChangeSet[this.treeSet.size()]);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
