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
package de.unisaarland.cs.st.moskito.rcs.git;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph;

/**
 * The Class TransactionIterator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GitTransactionIterator implements Iterator<String>, Iterable<String> {
	
	/** The root. */
	private final String              root;
	
	/** The current. */
	private String                    current;
	
	/** The delegate. */
	private GitTransactionIterator    delegate        = null;
	
	private final IRevDependencyGraph revGraph;
	
	private Set<String>               beforeDelegates = null;
	
	/**
	 * Instantiates a new iterator iterating across all transactions that were visible before this transaction.
	 * 
	 * @param root
	 *            the root
	 */
	@NoneNull
	public GitTransactionIterator(final String root, final IRevDependencyGraph revGraph) {
		this.root = root;
		this.current = this.root;
		this.revGraph = revGraph;
		this.beforeDelegates = new HashSet<String>();
	}
	
	public GitTransactionIterator(final String root, final IRevDependencyGraph revGraph,
	        final Set<String> beforeDelegates) {
		this.root = root;
		this.current = this.root;
		this.revGraph = revGraph;
		this.beforeDelegates = new HashSet<String>();
		this.beforeDelegates.addAll(beforeDelegates);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.current != null) {
			return true;
		} else if (this.delegate != null) {
			return this.delegate.hasNext();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
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
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		
		if (this.delegate != null) {
			final String delegateNext = this.delegate.next();
			
			if (skip(delegateNext)) {
				this.delegate = null;
			} else {
				return delegateNext;
			}
		}
		
		final String branchParent = this.revGraph.getBranchParent(this.current);
		final String mergeParent = this.revGraph.getMergeParent(this.current);
		this.beforeDelegates.remove(this.current);
		this.beforeDelegates.add(branchParent);
		if (mergeParent != null) {
			final Set<String> stopAt = new HashSet<String>();
			stopAt.addAll(this.beforeDelegates);
			this.delegate = new GitTransactionIterator(mergeParent, this.revGraph, stopAt);
		}
		
		if (Logger.logDebug()) {
			Logger.debug(getClass().getSimpleName() + ".next() with root " + this.root + ": " + this.current);
		}
		final String result = this.current;
		this.current = branchParent;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		return;
	}
	
	private boolean skip(final String hash) {
		// PRECONDITIONS
		
		try {
			for (final String s : this.beforeDelegates) {
				if (this.revGraph.existsPath(hash, s)) {
					return true;
				}
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
