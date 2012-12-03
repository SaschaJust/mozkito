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
package org.mozkito.genealogies.layer;

import java.util.Collection;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import org.mozkito.codeanalysis.model.JavaChangeOperation;

/**
 * The Class ChangeGenealogyLayerNode.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class ChangeGenealogyLayerNode implements Iterable<JavaChangeOperation> {
	
	/** The partition. */
	private final Collection<JavaChangeOperation> partition;
	
	/** The earliest timestamp. */
	private DateTime                              earliestTimestamp = null;
	
	/** The latest timestamp. */
	private DateTime                              latestTimestamp   = null;
	
	/**
	 * Instantiates a new change genealogy layer node.
	 * 
	 * @param partition
	 *            the partition
	 */
	public ChangeGenealogyLayerNode(final Collection<JavaChangeOperation> partition) {
		this.partition = partition;
	}
	
	/**
	 * Contains.
	 * 
	 * @param dependent
	 *            the dependent
	 * @return true, if successful
	 */
	public boolean contains(final JavaChangeOperation dependent) {
		return this.partition.contains(dependent);
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
		final ChangeGenealogyLayerNode other = (ChangeGenealogyLayerNode) obj;
		if (this.partition == null) {
			if (other.partition != null) {
				return false;
			}
		} else if (!this.partition.equals(other.partition)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the earliest timestamp.
	 * 
	 * @return the earliest timestamp
	 */
	public DateTime getEarliestTimestamp() {
		if (this.earliestTimestamp == null) {
			final DateTimeComparator timeComparator = DateTimeComparator.getInstance();
			for (final JavaChangeOperation op : this) {
				final DateTime time = op.getRevision().getTransaction().getTimestamp();
				if ((this.earliestTimestamp == null) || (timeComparator.compare(time, this.earliestTimestamp) < 0)) {
					this.earliestTimestamp = time;
				}
			}
		}
		return this.earliestTimestamp;
	}
	
	/**
	 * Gets the latest timestamp.
	 * 
	 * @return the latest timestamp
	 */
	public DateTime getLatestTimestamp() {
		if (this.latestTimestamp == null) {
			final DateTimeComparator timeComparator = DateTimeComparator.getInstance();
			for (final JavaChangeOperation op : this) {
				final DateTime time = op.getRevision().getTransaction().getTimestamp();
				if ((this.latestTimestamp == null) || (timeComparator.compare(time, this.latestTimestamp) > 0)) {
					this.latestTimestamp = time;
				}
			}
		}
		return this.latestTimestamp;
	}
	
	/**
	 * Gets the node id.
	 * 
	 * @return the node id
	 */
	public abstract String getNodeId();
	
	/**
	 * Gets the partition.
	 * 
	 * @return the partition
	 */
	public Collection<JavaChangeOperation> getPartition() {
		return this.partition;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.partition == null)
		                                                     ? 0
		                                                     : this.partition.hashCode());
		return result;
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return this.partition.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<JavaChangeOperation> iterator() {
		return this.partition.iterator();
	}
	
	/**
	 * Size.
	 * 
	 * @return the double
	 */
	public double size() {
		return this.partition.size();
	}
}
