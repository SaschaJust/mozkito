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
