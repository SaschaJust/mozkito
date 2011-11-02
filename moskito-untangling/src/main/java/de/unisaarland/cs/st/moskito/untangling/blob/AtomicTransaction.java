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
package de.unisaarland.cs.st.moskito.untangling.blob;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class BlobTransaction.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class AtomicTransaction implements Comparable<AtomicTransaction> {
	
	
	/** The transaction. */
	private final RCSTransaction            transaction;
	
	/** The operations. */
	private final Collection<JavaChangeOperation> operations;
	
	/**
	 * Instantiates a new blob transaction.
	 * 
	 * @param transaction
	 *            the transaction
	 * @param operations
	 *            the operations
	 */
	public AtomicTransaction(final RCSTransaction transaction, final Collection<JavaChangeOperation> operations) {
		this.transaction = transaction;
		this.operations = operations;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final AtomicTransaction other) {
		return transaction.compareTo(other.getTransaction()) * -1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AtomicTransaction other = (AtomicTransaction) obj;
		if (transaction == null) {
			if (other.transaction != null) {
				return false;
			}
		} else if (!transaction.equals(other.transaction)) {
			return false;
		}
		return true;
	}
	
	public Set<JavaChangeOperation> getChangeOperation(final Class<? extends JavaElement> clazz) {
		Set<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		
		for(JavaChangeOperation op : this.getOperations()){
			JavaElement element = op.getChangedElementLocation().getElement();
			if (element.getClass().equals(clazz)) {
				result.add(op);
			}
		}
		return result;
	}
	
	/**
	 * Gets the operations.
	 * 
	 * @return the operations
	 */
	public Collection<JavaChangeOperation> getOperations() {
		return operations;
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	public RCSTransaction getTransaction() {
		return transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((transaction == null)
				? 0
						: transaction.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getTransaction().getId();
	}
	
}
