/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.untangling.msr2013.impact;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.Tuple;

/**
 * The Class Partition.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Partition implements Comparable<Partition> {
	
	/** The cl id. */
	private final String             clID;
	
	/** The partition number. */
	private final int                partitionNumber;
	
	/** The operation ids. */
	private final Set<Long>          operationIds = new HashSet<>();
	
	/** The operations. */
	private final Map<Long, Boolean> operations   = new HashMap<>();
	
	/** The file ids. */
	private final Set<Long>          fileIds      = new HashSet<>();
	
	/** The bug i ds. */
	private final Set<String>        bugIDs       = new HashSet<>();
	
	/** The mixed. */
	private final boolean            mixed;
	
	/**
	 * Instantiates a new partition.
	 * 
	 * @param clID
	 *            the cl id
	 * @param partitionNumber
	 *            the partition number
	 * @param operationIds
	 *            the operation ids
	 * @param bugsIDs
	 *            the bugs i ds
	 * @param mixed
	 *            the mixed
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public Partition(final String clID, final int partitionNumber, final Collection<Long> operationIds,
	        final Collection<String> bugsIDs, final boolean mixed, final Map<Long, Tuple<Long, Boolean>> cos2Files) {
		this.clID = clID;
		this.partitionNumber = partitionNumber;
		this.operationIds.addAll(operationIds);
		this.bugIDs.addAll(bugsIDs);
		this.mixed = mixed;
		
		for (final Long opId : getOperationIds()) {
			if (!cos2Files.containsKey(opId)) {
				throw UnrecoverableError.format("cos2Files must contain ALL partition change operation IDs. Did not contain %s. Map has size %s.",
				                                opId, String.valueOf(cos2Files.size()));
			}
			this.operations.put(opId, cos2Files.get(opId).getSecond());
			this.fileIds.add(cos2Files.get(opId).getFirst());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Partition o) {
		if (getNumberOfMethodDefs() < o.getNumberOfMethodDefs()) {
			return -1;
		} else if (getNumberOfMethodDefs() > o.getNumberOfMethodDefs()) {
			return 1;
		}
		return 0;
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
		final Partition other = (Partition) obj;
		if (this.clID == null) {
			if (other.clID != null) {
				return false;
			}
		} else if (!this.clID.equals(other.clID)) {
			return false;
		}
		if (this.partitionNumber != other.partitionNumber) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the bug ids.
	 * 
	 * @return the bug ids
	 */
	public Set<String> getBugIds() {
		return this.bugIDs;
	}
	
	/**
	 * Gets the cL id.
	 * 
	 * @return the cL id
	 */
	public String getCLId() {
		return this.clID;
	}
	
	/**
	 * Gets the file ids.
	 * 
	 * @return the file ids
	 */
	public Set<Long> getFileIds() {
		return this.fileIds;
	}
	
	/**
	 * Gets the number of method defs.
	 * 
	 * @return the number of method defs
	 */
	public int getNumberOfMethodDefs() {
		int result = 0;
		for (final Boolean methDef : this.operations.values()) {
			if (methDef) {
				++result;
			}
		}
		return result;
	}
	
	/**
	 * Gets the operation ids.
	 * 
	 * @return the operation ids
	 */
	public Set<Long> getOperationIds() {
		return this.operationIds;
	}
	
	/**
	 * Gets the partition number.
	 * 
	 * @return the partition number
	 */
	public int getpartitionNumber() {
		return this.partitionNumber;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.clID == null)
		                                                ? 0
		                                                : this.clID.hashCode());
		result = (prime * result) + this.partitionNumber;
		return result;
	}
	
	/**
	 * Checks if is mixed.
	 * 
	 * @return true, if is mixed
	 */
	public boolean isMixed() {
		return this.mixed;
	}
}
