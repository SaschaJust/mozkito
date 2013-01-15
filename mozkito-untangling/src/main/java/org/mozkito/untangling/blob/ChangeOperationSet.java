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
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.versions.exceptions.NotComparableException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class BlobTransaction.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeOperationSet implements Comparable<ChangeOperationSet> {
	
	/** The transaction. */
	private final ChangeSet                       changeset;
	
	/** The operations. */
	private final Collection<JavaChangeOperation> operations;
	
	/**
	 * Instantiates a new blob transaction.
	 * 
	 * @param changeset
	 *            the transaction
	 * @param operations
	 *            the operations
	 */
	public ChangeOperationSet(final ChangeSet changeset, final Collection<JavaChangeOperation> operations) {
		this.changeset = changeset;
		this.operations = operations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final ChangeOperationSet other) {
		
		final VersionArchive versionArchive = getChangeSet().getVersionArchive();
		try {
			return versionArchive.compareChangeSets(getChangeSet(), other.getChangeSet());
		} catch (final NotComparableException e) {
			throw new UnrecoverableError(e);
		}
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
		final ChangeOperationSet other = (ChangeOperationSet) obj;
		if (this.changeset == null) {
			if (other.changeset != null) {
				return false;
			}
		} else if (!this.changeset.equals(other.changeset)) {
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
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	public ChangeSet getChangeSet() {
		return this.changeset;
	}
	
	/**
	 * Gets the operations.
	 * 
	 * @return the operations
	 */
	public Collection<JavaChangeOperation> getOperations() {
		return this.operations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.changeset == null)
		                                                     ? 0
		                                                     : this.changeset.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getChangeSet().getId();
	}
	
}
