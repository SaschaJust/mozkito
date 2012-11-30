/**
 * 
 */
package org.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.RCSPersistenceUtil;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.collections.TransactionSet;
import org.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.Transaction;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class PPASource extends Source<Transaction> {
	
	private Iterator<Branch>      branchIterator   = null;
	private Iterator<Transaction> tIterator        = null;
	private Set<String>              transactionLimit = null;
	
	public PPASource(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final HashSet<String> transactionLimit) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<Transaction, Transaction>(this) {
			
			@Override
			public void preExecution() {
				
				if ((PPASource.this.transactionLimit != null) && (!PPASource.this.transactionLimit.isEmpty())) {
					PPASource.this.transactionLimit = transactionLimit;
				} else {
					final List<Branch> branches = new LinkedList<Branch>();
					final BranchFactory branchFactory = new BranchFactory(persistenceUtil);
					branches.add(branchFactory.getMasterBranch());
					
					final Criteria<Branch> criteria = persistenceUtil.createCriteria(Branch.class);
					for (final Branch branch : persistenceUtil.load(criteria)) {
						if (!branch.isMasterBranch()) {
							branches.add(branch);
						}
					}
					PPASource.this.branchIterator = branches.iterator();
				}
			}
		};
		
		new ProcessHook<Transaction, Transaction>(this) {
			
			@Override
			public void process() {
				
				if (PPASource.this.transactionLimit != null) {
					// test cases
					if (PPASource.this.tIterator == null) {
						// initialize
						final Criteria<Transaction> criteria = persistenceUtil.createCriteria(Transaction.class);
						criteria.in("id", PPASource.this.transactionLimit);
						PPASource.this.tIterator = persistenceUtil.load(criteria).iterator();
					}
					if (PPASource.this.tIterator.hasNext()) {
						providePartialOutputData(PPASource.this.tIterator.next());
						if (!PPASource.this.tIterator.hasNext()) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						}
					} else {
						if (Logger.logTrace()) {
							Logger.trace("Setting completed.");
						}
						setCompleted();
					}
				} else {
					// normal behavior
					if ((PPASource.this.tIterator == null) || (!PPASource.this.tIterator.hasNext())) {
						// load new transactions
						if (PPASource.this.branchIterator.hasNext()) {
							final Branch next = PPASource.this.branchIterator.next();
							final TransactionSet set = RCSPersistenceUtil.getTransactions(persistenceUtil, next,
							                                                              TransactionSetOrder.ASC);
							PPASource.this.tIterator = set.iterator();
							if (Logger.logInfo()) {
								Logger.info("Processing RCSBRanch %s with %s transactions.", next.toString(),
								            String.valueOf(set.size()));
							}
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						}
					}
					
					if (PPASource.this.tIterator.hasNext()) {
						
						final Transaction transaction = PPASource.this.tIterator.next();
						
						// final int numRevision = transaction.getChangedFiles().size();
						// if (numRevision > 50) {
						// if (Logger.logWarn()) {
						// Logger.warn("Skipping transaction %s that touches more than %d > 50 files.",
						// transaction.getId(), numRevision);
						// }
						// }
						
						// test if seen already
						boolean skip = false;
						for (final Revision revision : transaction.getRevisions()) {
							final Criteria<JavaChangeOperation> skipCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
							                                                                  .eq("revision", revision);
							if (!persistenceUtil.load(skipCriteria).isEmpty()) {
								skip = true;
								if (Logger.logDebug()) {
									Logger.debug("Skipping Transaction %s. This transaction was analyzed and persisted already.",
									             transaction.getId());
								}
								break;
							}
						}
						if (skip) {
							if (Logger.logTrace()) {
								Logger.trace("Skipping output data.");
							}
							skipOutputData();
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Providing partial output data.");
							}
							providePartialOutputData(transaction);
						}
						if ((!PPASource.this.tIterator.hasNext()) && (!PPASource.this.branchIterator.hasNext())) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							
							setCompleted();
						}
					} else {
						if (!PPASource.this.branchIterator.hasNext()) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Skipping output data.");
							}
							skipOutputData();
						}
					}
					
				}
				
			}
		};
	}
}
