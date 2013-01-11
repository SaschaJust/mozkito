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

import org.mozkito.versions.collections.ChangeSetSet;
import org.mozkito.versions.collections.ChangeSetSet.TransactionSetOrder;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class RCSPersistenceUtil.
 */
public class RCSPersistenceUtil {
	
	/**
	 * Gets the transactions.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param rCSBranch
	 *            the branch
	 * @param order
	 *            the order
	 * @return the transactions
	 * 
	 */
	@NoneNull
	public static ChangeSetSet getChangeSet(final PersistenceUtil persistenceUtil,
	                                             final Branch rCSBranch,
	                                             final TransactionSetOrder order) {
		
		final Criteria<ChangeSet> criteria = persistenceUtil.createCriteria(ChangeSet.class);
		final MapJoin<ChangeSet, String, Long> branchRoot = criteria.getRoot().joinMap("branchIndices");
		final CriteriaQuery<ChangeSet> query = criteria.getQuery();
		query.where(criteria.getBuilder().equal(branchRoot.key(), rCSBranch.getName()));
		criteria.setQuery(query);
		
		final ChangeSetSet result = new ChangeSetSet(order);
		result.addAll(persistenceUtil.load(criteria));
		return result;
	}
}
