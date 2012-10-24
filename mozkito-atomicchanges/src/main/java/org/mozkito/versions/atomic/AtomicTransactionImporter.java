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
package org.mozkito.versions.atomic;

import java.util.Collection;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class AtomicTransactionImporter {
	
	public static synchronized boolean markTransactionAsAtomic(final RCSTransaction transaction,
	                                                           final PersistenceUtil persistenceUtil) {
		transaction.setAtomic(true);
		persistenceUtil.saveOrUpdate(transaction);
		return true;
	}
	
	public static synchronized boolean markTransactionIdAsAtomic(final String transactionId,
	                                                             final PersistenceUtil persistenceUtil) {
		final RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
		if (transaction == null) {
			return false;
		}
		transaction.setAtomic(true);
		persistenceUtil.saveOrUpdate(transaction);
		return true;
	}
	
	@NoneNull
	public static synchronized boolean markTransactionIdsAsAtomic(final Collection<String> transactionIds,
	                                                              final PersistenceUtil persistenceUtil) {
		persistenceUtil.beginTransaction();
		for (final String id : transactionIds) {
			final RCSTransaction transaction = persistenceUtil.loadById(id, RCSTransaction.class);
			if ((transaction == null) || (!markTransactionAsAtomic(transaction, persistenceUtil))) {
				persistenceUtil.rollbackTransaction();
				return false;
			}
		}
		persistenceUtil.commitTransaction();
		return true;
	}
	
	public static synchronized boolean markTransactionsAsAtomic(final Collection<RCSTransaction> transactions,
	                                                            final PersistenceUtil persistenceUtil) {
		persistenceUtil.beginTransaction();
		for (final RCSTransaction t : transactions) {
			if (!markTransactionAsAtomic(t, persistenceUtil)) {
				persistenceUtil.rollbackTransaction();
				return false;
			}
		}
		persistenceUtil.commitTransaction();
		return true;
	}
}
