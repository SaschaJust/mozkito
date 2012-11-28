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
package org.mozkito.persistence;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.versions.collections.TransactionSet;
import org.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import org.mozkito.versions.model.RCSBranch;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class RCSPersistenceUtil.
 */
public class RCSPersistenceUtil {
	
	/**
	 * Gets the transactions.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param branch
	 *            the branch
	 * @param order
	 *            the order
	 * @return the transactions
	 * 
	 */
	@NoneNull
	public static TransactionSet getTransactions(final PersistenceUtil persistenceUtil,
	                                             final RCSBranch branch,
	                                             final TransactionSetOrder order) {
		
		final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
		final MapJoin<RCSTransaction, String, Long> branchRoot = criteria.getRoot().joinMap("branchIndices");
		final CriteriaQuery<RCSTransaction> query = criteria.getQuery();
		query.where(criteria.getBuilder().equal(branchRoot.key(), branch.getName()));
		criteria.setQuery(query);
		
		final TransactionSet result = new TransactionSet(order);
		result.addAll(persistenceUtil.load(criteria));
		return result;
	}
}
