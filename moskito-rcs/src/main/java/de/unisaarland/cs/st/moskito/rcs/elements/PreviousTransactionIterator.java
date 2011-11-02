/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.rcs.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class PreviousTransactionItareator.
 * 
 * @author kim
 */
public class PreviousTransactionIterator implements Iterator<RCSTransaction> {
	
	/** The root. */
	@SuppressWarnings ("unused")
	private final RCSTransaction                      root;
	
	private final Map<RCSTransaction, Set<RCSBranch>> elementLocks    = new HashMap<RCSTransaction, Set<RCSBranch>>();
	private Set<RCSTransaction>                       currentChildren = new LinkedHashSet<RCSTransaction>();
	private Iterator<RCSTransaction>                  currentListIter = this.currentChildren.iterator();
	private final HashSet<RCSBranch>                  currentBranches = new HashSet<RCSBranch>();
	
	/**
	 * Instantiates a new iterator iterating across all transactions that were
	 * visible before this transaction.
	 * 
	 * @param root
	 *            the root
	 */
	public PreviousTransactionIterator(final RCSTransaction root) {
		this.root = root;
		this.currentChildren.add(root);
		this.currentBranches.add(root.getBranch());
		Set<RCSTransaction> nextChildren = this.nextChildren();
		this.currentChildren = nextChildren;
		this.currentListIter = this.currentChildren.iterator();
	}
	
	private void addLock(final RCSTransaction branchOpeningTransaction) {
		if (!this.elementLocks.containsKey(branchOpeningTransaction)) {
			this.elementLocks.put(branchOpeningTransaction, new HashSet<RCSBranch>());
			for (RCSTransaction tmpChild : branchOpeningTransaction.getChildren()) {
				if (!tmpChild.getBranch().equals(branchOpeningTransaction.getBranch())
				        && (!this.currentBranches.contains(tmpChild.getBranch()))) {
					this.elementLocks.get(branchOpeningTransaction).add(tmpChild.getBranch());
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (!this.currentListIter.hasNext()) {
			Set<RCSTransaction> nextChildren = this.nextChildren();
			this.currentChildren = nextChildren;
			this.currentListIter = this.currentChildren.iterator();
		}
		return this.currentListIter.hasNext();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RCSTransaction next() {
		if (!this.hasNext()) {
			return null;
		}
		RCSTransaction next = this.currentListIter.next();
		if (this.elementLocks.containsKey(next) && (!this.elementLocks.get(next).isEmpty())) {
			return this.next();
		}
		return next;
		
	}
	
	private Set<RCSTransaction> nextChildren() {
		Set<RCSTransaction> nextChildren = new LinkedHashSet<RCSTransaction>();
		
		for (RCSTransaction t : this.currentChildren) {
			
			// if there is a lock on the element, add the element again
			if (this.elementLocks.containsKey(t) && (!this.elementLocks.get(t).isEmpty())) {
				nextChildren.add(t);
				continue;
			}
			
			// if there is no lock
			
			RCSTransaction parent = t.getParent(t.getBranch());
			if (parent == null) {
				continue;
			}
			
			// add the parent within the current branch. If the parent is not
			// within the same branch (this was the begin of the current
			// branch), check if the branch of the parent is processed already.
			
			if (parent.getBranch().equals(t.getBranch())) {
				nextChildren.add(parent);
			} else {
				// if this is not the case, the current branch just opened
				if (!this.currentBranches.contains(parent.getBranch())) {
					nextChildren.add(parent);
					this.currentBranches.add(parent.getBranch());
				}
				// and remove the lock
				if (this.elementLocks.containsKey(parent)) {
					this.elementLocks.get(parent).remove(t.getBranch());
				}
			}
			
			// now process all other parents (not within the same branch, this t
			// is a merge)
			
			Set<RCSTransaction> parents = t.getParents();
			if (parents.size() > 1) {
				// remove already processed parent
				parents.remove(parent);
				// for all other check if these are already pointing to open
				// branches.
				for (RCSTransaction p : parents) {
					if (!this.currentBranches.contains(p.getBranch())) {
						
						if (p.getBranch().getBegin() != null) {
							RCSTransaction branchOpeningTransaction = p.getBranch().getBegin().getParent(p.getBranch());
							if (branchOpeningTransaction != null) {
								// add lock to branchOpeningTransaction
								this.addLock(branchOpeningTransaction);
							}
						}
						if (!this.currentBranches.contains(p.getBranch())) {
							nextChildren.add(p);
							this.currentBranches.add(p.getBranch());
						}
					}
				}
			}
		}
		
		// now we know all next children. BUT we need to open locks for all
		// nextChildren which have multiple children but no lock so far.
		
		for (RCSTransaction nextChild : nextChildren) {
			if ((nextChild.getChildren().size() > 1) && (!this.elementLocks.containsKey(nextChild))) {
				this.addLock(nextChild);
			}
		}
		
		return nextChildren;
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
