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
package org.mozkito.versions.atomic;

import java.util.Collection;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class AtomicTransactionImporter.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class AtomicTransactionImporter {
	
	/**
	 * Mark transaction id as atomic.
	 * 
	 * @param changeSetId
	 *            the transaction id
	 * @param persistenceUtil
	 *            the persistence util
	 * @return true, if successful
	 */
	public static synchronized boolean markChangeSetIdAsAtomic(final String changeSetId,
	                                                           final PersistenceUtil persistenceUtil) {
		final ChangeSet changeSet = persistenceUtil.loadById(changeSetId, ChangeSet.class);
		if (changeSet == null) {
			return false;
		}
		changeSet.setAtomic(true);
		persistenceUtil.saveOrUpdate(changeSet);
		return true;
	}
	
	/**
	 * Mark transaction ids as atomic.
	 * 
	 * @param changeSetIds
	 *            the transaction ids
	 * @param persistenceUtil
	 *            the persistence util
	 * @return true, if successful
	 */
	@NoneNull
	public static synchronized boolean markChangeSetIdsAsAtomic(final Collection<String> changeSetIds,
	                                                            final PersistenceUtil persistenceUtil) {
		persistenceUtil.beginTransaction();
		for (final String id : changeSetIds) {
			final ChangeSet changeSet = persistenceUtil.loadById(id, ChangeSet.class);
			if ((changeSet == null) || (!markTransactionAsAtomic(changeSet, persistenceUtil))) {
				persistenceUtil.rollbackTransaction();
				return false;
			}
		}
		persistenceUtil.commitTransaction();
		return true;
	}
	
	/**
	 * Mark transaction as atomic.
	 * 
	 * @param changeSet
	 *            the r cs transaction
	 * @param persistenceUtil
	 *            the persistence util
	 * @return true, if successful
	 */
	public static synchronized boolean markTransactionAsAtomic(final ChangeSet changeSet,
	                                                           final PersistenceUtil persistenceUtil) {
		changeSet.setAtomic(true);
		persistenceUtil.saveOrUpdate(changeSet);
		return true;
	}
	
	/**
	 * Mark transactions as atomic.
	 * 
	 * @param changeSets
	 *            the r cs transactions
	 * @param persistenceUtil
	 *            the persistence util
	 * @return true, if successful
	 */
	public static synchronized boolean markTransactionsAsAtomic(final Collection<ChangeSet> changeSets,
	                                                            final PersistenceUtil persistenceUtil) {
		persistenceUtil.beginTransaction();
		for (final ChangeSet t : changeSets) {
			if (!markTransactionAsAtomic(t, persistenceUtil)) {
				persistenceUtil.rollbackTransaction();
				return false;
			}
		}
		persistenceUtil.commitTransaction();
		return true;
	}
}
