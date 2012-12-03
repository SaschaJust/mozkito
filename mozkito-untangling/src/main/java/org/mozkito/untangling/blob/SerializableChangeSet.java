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
package org.mozkito.untangling.blob;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;


/**
 * The Class SerializableChangeSet.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class SerializableChangeSet implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5508693461764140540L;
	
	/** The transaction id. */
	private final String      transactionId;
	
	/** The operation ids. */
	private final Set<Long>   operationIds     = new HashSet<>();
	
	/**
	 * Instantiates a new serializable change set.
	 *
	 * @param changeSet the change set
	 */
	public SerializableChangeSet(final ChangeSet changeSet) {
		this.transactionId = changeSet.getTransaction().getId();
		for (final JavaChangeOperation operation : changeSet.getOperations()) {
			this.operationIds.add(operation.getId());
		}
	}
	
	/**
	 * Unserialize.
	 *
	 * @param persistenceUtil the persistence util
	 * @return the change set
	 */
	public ChangeSet unserialize(final PersistenceUtil persistenceUtil) {
		final RCSTransaction rCSTransaction = persistenceUtil.loadById(this.transactionId, RCSTransaction.class);
		final Set<JavaChangeOperation> operations = new HashSet<>();
		for (final Long operationId : this.operationIds) {
			operations.add(persistenceUtil.loadById(operationId, JavaChangeOperation.class));
		}
		return new ChangeSet(rCSTransaction, operations);
	}
	
}
