package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;
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
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class ChangeOperationReader extends AndamaSource<OperationCollection> {
	
	private Iterator<RCSTransaction> iterator;
	
	public ChangeOperationReader(AndamaGroup threadGroup, AndamaSettings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
				TreeSet<RCSTransaction> list = new TreeSet<RCSTransaction>();
				
				for (RCSTransaction transaction : persistenceUtil.load(criteria)) {
					if (transaction.getBranch().equals(RCSBranch.getMasterBranch())) {
						list.add(transaction);
					}
				}
				
				RCSTransaction last = null;
				Iterator<RCSTransaction> it = list.iterator();
				
				while (it.hasNext()) {
					RCSTransaction transaction = it.next();
					if (last != null) {
						if (last.compareTo(transaction) != -1) {
							System.err.println("ERROR: " + last + " vs " + transaction + " WRONG ORDER.");
						}
					}
				}
				
				if (Logger.logInfo()) {
					Logger.info("Added " + list.size()
							+ " RCSTransactions that were found in MASTER branch to build the change genealogy.");
				}
				
				iterator = list.iterator();
			}
		};
		
		new ProcessHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					RCSTransaction transaction = iterator.next();
					Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(
							persistenceUtil, transaction);
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + transaction);
					}
					
					providePartialOutputData(new OperationCollection(changeOperations));
					if(!iterator.hasNext()){
						setCompleted();
					}
				}
			}
		};
	}
	
}
