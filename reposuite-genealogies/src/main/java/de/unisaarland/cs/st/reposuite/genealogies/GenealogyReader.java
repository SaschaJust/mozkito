package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;


public class GenealogyReader extends AndamaSource<Collection<JavaChangeOperation>> {
	
	private Iterator<RCSTransaction> iterator;
	
	public GenealogyReader(AndamaGroup threadGroup, AndamaSettings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<Collection<JavaChangeOperation>, Collection<JavaChangeOperation>>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
				List<RCSTransaction> list = persistenceUtil.load(criteria);
				iterator = list.iterator();
			}
		};
		
		new ProcessHook<Collection<JavaChangeOperation>, Collection<JavaChangeOperation>>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					RCSTransaction transaction = iterator.next();
					Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(
							persistenceUtil, transaction);
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + transaction);
					}
					
					providePartialOutputData(changeOperations);
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
	
}
