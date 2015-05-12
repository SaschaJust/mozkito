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
package org.mozkito.versions.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class TransactionIterator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class RevDepIterator implements Iterator<String>, Iterable<String> {
	
	/** The root. */
	private final String                root;
	
	/** The current. */
	private String                      current;
	
	/** The delegate. */
	private final Queue<RevDepIterator> delegates       = new LinkedList<RevDepIterator>();
	
	/** The rev graph. */
	private final RevDependencyGraph    revGraph;
	
	/** The before delegates. */
	private Set<String>                 beforeDelegates = null;
	
	/**
	 * Instantiates a new iterator iterating across all transactions that were visible before this transaction.
	 * 
	 * @param root
	 *            the root
	 * @param revGraph
	 *            the rev graph
	 */
	@NoneNull
	public RevDepIterator(final String root, final RevDependencyGraph revGraph) {
		this.root = root;
		this.current = this.root;
		this.revGraph = revGraph;
		this.beforeDelegates = new HashSet<String>();
	}
	
	/**
	 * Instantiates a new git transaction iterator.
	 * 
	 * @param root
	 *            the root
	 * @param revGraph
	 *            the rev graph
	 * @param beforeDelegates
	 *            the before delegates
	 */
	public RevDepIterator(final String root, final RevDependencyGraph revGraph, final Set<String> beforeDelegates) {
		this.root = root;
		this.current = this.root;
		this.revGraph = revGraph;
		this.beforeDelegates = new HashSet<String>();
		this.beforeDelegates.addAll(beforeDelegates);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.current != null) {
			return true;
		} else if (!this.delegates.isEmpty()) {
			return this.delegates.peek().hasNext();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<String> iterator() {
		// PRECONDITIONS
		
		try {
			return this;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		
		while (!this.delegates.isEmpty()) {
			if (!this.delegates.peek().hasNext()) {
				this.delegates.poll();
				continue;
			} else {
				final String delegateNext = this.delegates.peek().next();
				if (Logger.logAlways()) {
					Logger.always("RevDepIterator: Delegate predecessor of %s --> %s", this.current, delegateNext);
				}
				if (skip(delegateNext)) {
					this.delegates.poll();
					continue;
				} else {
					return delegateNext;
				}
			}
		}
		
		final String branchParent = this.revGraph.getBranchParent(this.current);
		final List<String> mergeParents = this.revGraph.getMergeParents(this.current);
		this.beforeDelegates.remove(this.current);
		this.beforeDelegates.add(branchParent);
		if (!mergeParents.isEmpty()) {
			
			final List<String> reverseMergeParent = new LinkedList<String>(mergeParents);
			Collections.reverse(reverseMergeParent);
			final Set<String> stopAt = new HashSet<String>();
			stopAt.addAll(this.beforeDelegates);
			final List<RevDepIterator> newDelegates = new LinkedList<>();
			for (final String mergeParent : reverseMergeParent) {
				newDelegates.add(new RevDepIterator(mergeParent, this.revGraph, new HashSet<String>(stopAt)));
				stopAt.add(mergeParent);
			}
			for (final RevDepIterator delegate : newDelegates) {
				this.delegates.add(delegate);
			}
		}
		
		if (Logger.logAlways()) {
			Logger.always(getClass().getSimpleName() + ".next() with root " + this.root + ": " + this.current);
		}
		final String result = this.current;
		this.current = branchParent;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		return;
	}
	
	/**
	 * Skip.
	 * 
	 * @param hash
	 *            the hash
	 * @return true, if successful
	 */
	private boolean skip(final String hash) {
		Condition.notNull(hash, "Field '%s' in '%s'.", "hash", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		for (final String s : this.beforeDelegates) {
			if (this.revGraph.existsPath(hash, s)) {
				return true;
			}
		}
		return false;
	}
	
}
