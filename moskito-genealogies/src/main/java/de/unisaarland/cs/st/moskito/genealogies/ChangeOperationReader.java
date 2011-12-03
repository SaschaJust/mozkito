package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class ChangeOperationReader extends AndamaSource<OperationCollection> {
	
	private Iterator<RCSTransaction> iterator;
	
	public ChangeOperationReader(AndamaGroup threadGroup, AndamaSettings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
				List<RCSTransaction> list = persistenceUtil.load(criteria);
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
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + transaction);
					}
					
					providePartialOutputData(new OperationCollection(changeOperations));
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
	
}
