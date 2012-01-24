package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.utils.OperationCollection;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.comparators.RCSTransactionOriginalIDComparator;
import de.unisaarland.cs.st.moskito.rcs.comparators.RCSTransactionTopologicalComparator;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class ChangeOperationReader extends AndamaSource<OperationCollection> {
	
	private Iterator<RCSTransaction> iterator;
	
	public ChangeOperationReader(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
				TreeSet<RCSTransaction> list_topo = new TreeSet<RCSTransaction>(
				                                                                new RCSTransactionTopologicalComparator());
				TreeSet<RCSTransaction> list_orgid = new TreeSet<RCSTransaction>(
				                                                                 new RCSTransactionOriginalIDComparator());
				LinkedList<RCSTransaction> topo_sorted_list = new LinkedList<RCSTransaction>();
				LinkedList<RCSTransaction> orgid_sorted_list = new LinkedList<RCSTransaction>();
				
				for (RCSTransaction transaction : persistenceUtil.load(criteria)) {
					if (transaction.getBranch().equals(RCSBranch.getMasterBranch())) {
						list_topo.add(transaction);
						list_orgid.add(transaction);
						topo_sorted_list.add(transaction);
						orgid_sorted_list.add(transaction);
					}
				}
				
				Collections.sort(topo_sorted_list, new RCSTransactionTopologicalComparator());
				Collections.sort(orgid_sorted_list, new RCSTransactionOriginalIDComparator());
				
				RCSTransaction last = null;
				Iterator<RCSTransaction> it_topo = list_topo.iterator();
				Iterator<RCSTransaction> it_orgid = list_topo.iterator();
				ListIterator<RCSTransaction> topo_sorted_it = topo_sorted_list.listIterator();
				ListIterator<RCSTransaction> orgid_sorted_it = topo_sorted_list.listIterator();
				
				while (it_topo.hasNext()) {
					RCSTransaction transaction_topo = it_topo.next();
					RCSTransaction transaction_orgid = it_orgid.next();
					RCSTransaction transaction_topolist = topo_sorted_it.next();
					RCSTransaction transaction_orgidlist = orgid_sorted_it.next();
					
					if (last != null) {
						System.err.println("Comparing current transaction " + transaction_topo
						        + " with previous transaction " + last);
						if (last.compareTo(transaction_topo) != -1) {
							System.err.println("ERROR: " + last + " vs " + transaction_topo + " WRONG ORDER (-1).");
						}
						if (transaction_topo.compareTo(last) != 1) {
							System.err.println("ERROR: " + transaction_topo + " vs " + last + " WRONG ORDER (1).");
						}
					}
					
					System.err.println("########### " + transaction_topo + " ## " + transaction_orgid + " ## "
					        + transaction_topolist + " ## " + transaction_orgidlist);
					if (!transaction_topo.equals(transaction_orgid) || !transaction_topo.equals(transaction_topolist)
					        || !transaction_topo.equals(transaction_orgidlist)) {
						System.err.println("ERROR: Got different order (TOPO vs ORGID). TOPO: " + transaction_topo
						        + " VS ORGID: " + transaction_orgid);
					}
					last = transaction_topo;
				}
				
				if (Logger.logInfo()) {
					Logger.info("Added " + list_topo.size()
					        + " RCSTransactions that were found in MASTER branch to build the change genealogy.");
				}
				
				ChangeOperationReader.this.iterator = list_topo.iterator();
			}
		};
		
		new ProcessHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void process() {
				if (ChangeOperationReader.this.iterator.hasNext()) {
					RCSTransaction transaction = ChangeOperationReader.this.iterator.next();
					Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
					                                                                                         transaction);
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + transaction);
					}
					
					providePartialOutputData(new OperationCollection(changeOperations));
					if (!ChangeOperationReader.this.iterator.hasNext()) {
						setCompleted();
					}
				}
			}
		};
	}
}
