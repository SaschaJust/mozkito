/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.untangling.blob;

import java.util.List;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;


public class BlobTransaction implements Comparable<BlobTransaction> {
	
	
	private final RCSTransaction            transaction;
	
	private final List<JavaChangeOperation> operations;
	
	public BlobTransaction(final RCSTransaction transaction, final List<JavaChangeOperation> operations){
		this.transaction = transaction;
		this.operations = operations;
	}
	
	
	@Override
	public int compareTo(final BlobTransaction other) {
		return transaction.compareTo(other.getTransaction()) * -1;
	}
	
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
		BlobTransaction other = (BlobTransaction) obj;
		if (transaction == null) {
			if (other.transaction != null) {
				return false;
			}
		} else if (!transaction.equals(other.transaction)) {
			return false;
		}
		return true;
	}
	
	public List<JavaChangeOperation> getOperations() {
		return operations;
	}
	
	public RCSTransaction getTransaction() {
		return transaction;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transaction == null)
				? 0
				: transaction.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return getTransaction().getId();
	}

}
