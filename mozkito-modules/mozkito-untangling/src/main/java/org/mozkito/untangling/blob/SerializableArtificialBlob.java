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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class SerializableArtificialBlob.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SerializableArtificialBlob implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long                serialVersionUID = 9034567768975977141L;
	
	/** The change sets. */
	private final Set<SerializableChangeSet> changeSets       = new HashSet<>();
	
	/**
	 * Instantiates a new serializable artificial blob.
	 * 
	 * @param blob
	 *            the blob
	 */
	public SerializableArtificialBlob(final ArtificialBlob blob) {
		for (final ChangeOperationSet changeSet : blob.getAtomicTransactions()) {
			this.changeSets.add(new SerializableChangeSet(changeSet));
		}
	}
	
	/**
	 * Unserialize.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the artificial blob
	 */
	public ArtificialBlob unserialize(final PersistenceUtil persistenceUtil) {
		final Set<ChangeOperationSet> unserChangeSets = new HashSet<>();
		for (final SerializableChangeSet sChangeSet : this.changeSets) {
			unserChangeSets.add(sChangeSet.unserialize(persistenceUtil));
		}
		return new ArtificialBlob(unserChangeSets);
	}
	
}
