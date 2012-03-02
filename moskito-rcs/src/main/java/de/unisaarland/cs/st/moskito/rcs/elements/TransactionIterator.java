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
package de.unisaarland.cs.st.moskito.rcs.elements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class TransactionIterator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TransactionIterator implements Iterator<RCSTransaction>, Iterable<RCSTransaction> {
	
	/** The root. */
	private final RCSTransaction      root;
	
	/** The current. */
	private RCSTransaction            current;
	
	/** The branch line. */
	private final Set<RCSTransaction> branchLine = new HashSet<RCSTransaction>();
	
	/** The delegate. */
	private TransactionIterator       delegate   = null;
	
	/**
	 * Instantiates a new iterator iterating across all transactions that were visible before this transaction.
	 * 
	 * @param root
	 *            the root
	 */
	@NoneNull
	public TransactionIterator(final RCSTransaction root) {
		this.root = root;
		this.current = this.root;
		
		RCSTransaction inBranch = this.root;
		while (inBranch != null) {
			inBranch = inBranch.getBranchParent();
			this.branchLine.add(inBranch);
		}
	}
	
	/**
	 * Instantiates a new transaction iterator.
	 * 
	 * @param root
	 *            the root
	 * @param stopAt
	 *            the stop at
	 */
	@NoneNull
	private TransactionIterator(final RCSTransaction root, final Set<RCSTransaction> stopAt) {
		this.root = root;
		this.current = this.root;
		
		this.branchLine.addAll(stopAt);
		RCSTransaction inBranch = this.root;
		while (inBranch != null) {
			inBranch = inBranch.getBranchParent();
			this.branchLine.add(inBranch);
		}
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
	public Iterator<RCSTransaction> iterator() {
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
	public RCSTransaction next() {
		if (this.delegate != null) {
			final RCSTransaction next = this.delegate.next();
			if (this.branchLine.contains(next)) {
				this.delegate = null;
			} else {
				return next;
			}
		}
		final RCSTransaction result = this.current;
		if (this.current.getMergeParent() != null) {
			this.delegate = new TransactionIterator(this.current.getMergeParent(), this.branchLine);
		}
		this.current = this.current.getBranchParent();
		if (Logger.logDebug()) {
			Logger.debug(getClass().getSimpleName() + ".next() with root " + this.root.getId() + ": " + result.getId());
		}
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
	
}
