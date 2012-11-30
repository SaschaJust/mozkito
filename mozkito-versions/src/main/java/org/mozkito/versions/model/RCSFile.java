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
/**
 * 
 */
package org.mozkito.versions.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.Annotated;

/**
 * The Class File.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "rcsfile")
public class RCSFile implements Annotated, Serializable {
	
	/**
	 * The Class FileNameTransactionIterator.
	 */
	private class FileNameTransactionIterator implements Iterator<RCSTransaction>, Iterable<RCSTransaction> {
		
		/** The merge points. */
		private final Stack<RCSTransaction> mergePoints = new Stack<>();
		
		/** The current. */
		private RCSTransaction              current;
		
		/** The head visited. */
		private boolean                     headVisited = false;
		
		/**
		 * Instantiates a new file name transaction iterator.
		 *
		 * @param startTransaction the start transaction
		 */
		public FileNameTransactionIterator(final RCSTransaction startTransaction) {
			this.current = startTransaction;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return (!this.headVisited) || (this.current.getBranchParent() != null) || (!this.mergePoints.isEmpty());
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<RCSTransaction> iterator() {
			return this;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public RCSTransaction next() {
			if (!this.headVisited) {
				this.headVisited = true;
				return this.current;
			}
			final RCSTransaction mergeParent = this.current.getMergeParent();
			if (mergeParent != null) {
				if (this.current.getBranchParent() != null) {
					this.mergePoints.push(this.current.getBranchParent());
				}
				this.current = mergeParent;
			} else {
				this.current = this.current.getBranchParent();
				if (this.current == null) {
					// no more branch parents. Check for merge points
					if (!this.mergePoints.isEmpty()) {
						this.current = this.mergePoints.pop();
					}
				}
			}
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
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = 7232712367403624199L;
	
	/** The generated id. */
	private long                generatedId;
	
	/** The changed names. */
	private Map<String, String> changedNames     = new HashMap<String, String>();
	
	/**
	 * used by PersistenceUtil to create a {@link RCSFile} instance.
	 */
	protected RCSFile() {
		
	}
	
	/**
	 * Instantiates a new file.
	 *
	 * @param path the path
	 * @param rCSTransaction the transaction
	 */
	public RCSFile(final String path, final RCSTransaction rCSTransaction) {
		getChangedNames().put(rCSTransaction.getId(), path);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * Assign transaction.
	 * 
	 * @param rCSTransaction
	 *            the transaction
	 * @param pathName
	 *            the path name
	 */
	@Transient
	public void assignTransaction(final RCSTransaction rCSTransaction,
	                              final String pathName) {
		getChangedNames().put(rCSTransaction.getId(), pathName);
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
		final RCSFile other = (RCSFile) obj;
		if (getGeneratedId() != other.getGeneratedId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the changed names.
	 *
	 * @return the changedNames
	 */
	@ElementCollection
	@JoinTable (name = "filenames", joinColumns = { @JoinColumn (name = "fileid", nullable = false) })
	public Map<String, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * Gets the generated id.
	 *
	 * @return the generatedId
	 */
	@Id
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the handle.
	 *
	 * @return the simple class name
	 */
	@Transient
	public String getHandle() {
		return RCSFile.class.getSimpleName();
	}
	
	/**
	 * Gets the latest path.
	 *
	 * @return the latest path
	 */
	@Transient
	public String getLatestPath() {
		return getChangedNames().get(new TreeSet<String>(getChangedNames().keySet()).last());
	}
	
	/**
	 * Gets the path.
	 *
	 * @param rCSTransaction the transaction
	 * @return the path
	 */
	@Transient
	public String getPath(final RCSTransaction rCSTransaction) {
		
		final FileNameTransactionIterator fileNameIter = new FileNameTransactionIterator(rCSTransaction);
		while (fileNameIter.hasNext()) {
			final RCSTransaction current = fileNameIter.next();
			if (getChangedNames().containsKey(current.getId())) {
				return getChangedNames().get(current.getId());
			}
		}
		
		if (Logger.logWarn()) {
			Logger.warn("Could not determine path for File (id=" + getGeneratedId() + ") for transaction "
			        + rCSTransaction.getId() + ". Returning latestPath.");
		}
		return getLatestPath();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (getGeneratedId() ^ (getGeneratedId() >>> 32));
		return result;
	}
	
	/**
	 * Saved.
	 *
	 * @return true, if successful
	 */
	@Transient
	public boolean saved() {
		return getGeneratedId() != 0;
	}
	
	/**
	 * Sets the changed names.
	 *
	 * @param changedNames the changed names
	 */
	protected void setChangedNames(final Map<String, String> changedNames) {
		this.changedNames = changedNames;
	}
	
	/**
	 * Sets the generated id.
	 *
	 * @param generatedId the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "File [id=" + getGeneratedId() + ", changedNames="
		        + JavaUtils.collectionToString(getChangedNames().values()) + "]";
	}
	
}
