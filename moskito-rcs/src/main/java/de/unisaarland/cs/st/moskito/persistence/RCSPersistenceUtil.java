package de.unisaarland.cs.st.moskito.persistence;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class RCSPersistenceUtil {
	
	/**
	 * Gets the previous transactions.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param transaction
	 *            the transaction
	 * @param order
	 *            the order
	 * @return the previous transactions
	 * 
	 * @deprecated This method returns not what you would expect. It returns the list of previous transactions ___within
	 *             a branch___. But this differs from a result that would consider the given transaction as HEAD. We
	 *             would have to use the RevDependencyGraph to determine the correct set. Remains to be implemented.
	 * 
	 */
	@Deprecated
	@NoneNull
	public static TransactionSet getPreviousTransactions(final PersistenceUtil persistenceUtil,
	                                                     final RCSTransaction transaction,
	                                                     final TransactionSetOrder order) {
		
		// FIXME this is not correct. This gives you the order in perspective of a branch. To work correct, we have to
		// use the RevDependencyGraph here.
		
		if (transaction.getBranchNames().isEmpty()) {
			return new TransactionSet(order);
		}
		final String branchName = transaction.getBranchNames().iterator().next();
		
		final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
		final MapJoin<RCSTransaction, String, Long> branchRoot = criteria.getRoot().joinMap("branchIndices");
		final CriteriaQuery<RCSTransaction> query = criteria.getQuery();
		
		final Predicate branchNamePredicate = criteria.getBuilder().equal(branchRoot.key(), branchName);
		final Predicate lessThanPredicate = criteria.getBuilder().lessThan(branchRoot.value(),
		                                                                   transaction.getBranchIndices()
		                                                                              .get(branchName));
		query.where(criteria.getBuilder().and(branchNamePredicate, lessThanPredicate));
		
		criteria.setQuery(query);
		
		final TransactionSet result = new TransactionSet(order);
		result.addAll(persistenceUtil.load(criteria));
		return result;
	}
	
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
