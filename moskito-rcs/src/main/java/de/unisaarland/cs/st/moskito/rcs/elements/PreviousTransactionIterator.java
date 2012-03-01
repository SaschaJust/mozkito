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

import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class PreviousTransactionItareator.
 * 
 * @author kim
 */
public class PreviousTransactionIterator implements Iterator<RCSTransaction>, Iterable<RCSTransaction> {
	
	private final RCSTransaction        root;
	private RCSTransaction              current;
	private final Set<RCSTransaction>   branchLine = new HashSet<RCSTransaction>();
	private PreviousTransactionIterator delegate   = null;
	
	/**
	 * Instantiates a new iterator iterating across all transactions that were visible before this transaction.
	 * 
	 * @param root
	 *            the root
	 */
	public PreviousTransactionIterator(final RCSTransaction root) {
		this.root = root;
		this.current = this.root;
		
		RCSTransaction inBranch = this.root;
		while (inBranch != null) {
			inBranch = inBranch.getBranchParent();
			this.branchLine.add(inBranch);
		}
		
		if (this.current.getMergeParent() != null) {
			this.delegate = new PreviousTransactionIterator(this.current.getMergeParent());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.delegate != null) {
			return this.delegate.hasNext();
		}
		return this.current.getBranchParent() == null
		                                             ? false
		                                             : true;
	}
	
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
		this.current = this.current.getBranchParent();
		return this.current;
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
