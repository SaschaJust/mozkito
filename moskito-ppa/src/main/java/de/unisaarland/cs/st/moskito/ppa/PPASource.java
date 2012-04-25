/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.RCSPersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPASource extends Source<RCSTransaction> {
	
	private Iterator<RCSBranch>      branchIterator   = null;
	private Iterator<RCSTransaction> tIterator        = null;
	private Set<String>              transactionLimit = null;
	
	public PPASource(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final HashSet<String> transactionLimit) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				
				if ((PPASource.this.transactionLimit != null) && (!PPASource.this.transactionLimit.isEmpty())) {
					PPASource.this.transactionLimit = transactionLimit;
				} else {
					final List<RCSBranch> branches = new LinkedList<RCSBranch>();
					final BranchFactory branchFactory = new BranchFactory(persistenceUtil);
					branches.add(branchFactory.getMasterBranch());
					
					final Criteria<RCSBranch> criteria = persistenceUtil.createCriteria(RCSBranch.class);
					for (final RCSBranch branch : persistenceUtil.load(criteria)) {
						if (!branch.isMasterBranch()) {
							branches.add(branch);
						}
					}
					PPASource.this.branchIterator = branches.iterator();
				}
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				
				if (PPASource.this.branchIterator == null) {
					// test cases
					if (PPASource.this.tIterator == null) {
						// initialize
						final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
						criteria.in("id", PPASource.this.transactionLimit);
						PPASource.this.tIterator = persistenceUtil.load(criteria).iterator();
					}
					if (PPASource.this.tIterator.hasNext()) {
						providePartialOutputData(PPASource.this.tIterator.next());
						if (!PPASource.this.tIterator.hasNext()) {
							setCompleted();
						}
					} else {
						provideOutputData(null, true);
						setCompleted();
					}
				} else {
					// normal behavior
					if ((PPASource.this.tIterator == null) || (!PPASource.this.tIterator.hasNext())) {
						// load new transactions
						if (PPASource.this.branchIterator.hasNext()) {
							final RCSBranch next = PPASource.this.branchIterator.next();
							PPASource.this.tIterator = RCSPersistenceUtil.getTransactions(persistenceUtil, next,
							                                                              TransactionSetOrder.ASC)
							                                             .iterator();
							if (Logger.logInfo()) {
								Logger.info("Processing RCSBRanch " + next.toString());
							}
						} else {
							provideOutputData(null, true);
							setCompleted();
						}
					}
					
					if (PPASource.this.tIterator.hasNext()) {
						
						final RCSTransaction transaction = PPASource.this.tIterator.next();
						
						// test if seen already
						boolean skip = false;
						for (final RCSRevision revision : transaction.getRevisions()) {
							final Criteria<JavaChangeOperation> skipCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
							                                                                  .eq("revision", revision);
							if (!persistenceUtil.load(skipCriteria).isEmpty()) {
								skip = true;
								if (Logger.logDebug()) {
									Logger.debug("Skipping RCSTransaction %s. This transaction was analyzed and persisted already.",
									             transaction.getId());
								}
								break;
							}
						}
						if (skip) {
							skipOutputData();
						} else {
							providePartialOutputData(transaction);
						}
						if ((!PPASource.this.tIterator.hasNext()) && (!PPASource.this.branchIterator.hasNext())) {
							setCompleted();
						}
					} else {
						if (!PPASource.this.branchIterator.hasNext()) {
							setCompleted();
						} else {
							skipOutputData();
						}
					}
					
				}
				
			}
		};
	}
}
