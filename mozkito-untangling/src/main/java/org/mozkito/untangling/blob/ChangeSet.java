/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.untangling.blob;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.versions.collections.TransactionSet;
import org.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class BlobTransaction.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeSet implements Comparable<ChangeSet> {
	
	/** The transaction. */
	private final RCSTransaction                  rCSTransaction;
	
	/** The operations. */
	private final Collection<JavaChangeOperation> operations;
	
	/**
	 * Instantiates a new blob transaction.
	 * 
	 * @param rCSTransaction
	 *            the transaction
	 * @param operations
	 *            the operations
	 */
	public ChangeSet(final RCSTransaction rCSTransaction, final Collection<JavaChangeOperation> operations) {
		this.rCSTransaction = rCSTransaction;
		this.operations = operations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final ChangeSet other) {
		
		final Comparator<? super RCSTransaction> comparator = new TransactionSet(TransactionSetOrder.ASC).comparator();
		return comparator.compare(other.getTransaction(), this.rCSTransaction);
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
		final ChangeSet other = (ChangeSet) obj;
		if (this.rCSTransaction == null) {
			if (other.rCSTransaction != null) {
				return false;
			}
		} else if (!this.rCSTransaction.equals(other.rCSTransaction)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the change operation.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the change operation
	 */
	public Set<JavaChangeOperation> getChangeOperation(final Class<? extends JavaElement> clazz) {
		final Set<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		
		for (final JavaChangeOperation op : getOperations()) {
			final JavaElement element = op.getChangedElementLocation().getElement();
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
		return this.operations;
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	public RCSTransaction getTransaction() {
		return this.rCSTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.rCSTransaction == null)
		                                                          ? 0
		                                                          : this.rCSTransaction.hashCode());
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
