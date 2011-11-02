/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;

import org.apache.maven.surefire.shade.org.codehaus.plexus.util.StringUtils;

import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author just
 * 
 */
public class PPASource extends AndamaSource<RCSTransaction> {
	
	private Iterator<RCSTransaction> iterator;
	
	public PPASource(AndamaGroup threadGroup, AndamaSettings settings, final PersistenceUtil persistenceUtil,
			final String startWith, final HashSet<String> transactionLimit) {
		super(threadGroup, settings, false);
		
		if (Logger.logDebug()) {
			Logger.debug("transactionLimit: "
					+ StringUtils.join(transactionLimit.toArray(new String[transactionLimit.size()]), ","));
		}
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
				if (Logger.logDebug()) {
					Logger.debug(criteria.toString());
				}

				if ((transactionLimit != null) && (!transactionLimit.isEmpty())) {
					if (Logger.logDebug()) {
						Logger.debug("Added transaction input criteria limit: "
								+ StringUtils.join(transactionLimit.toArray(new String[transactionLimit.size()]), ","));
					}
					criteria.in("id", transactionLimit);
				}
				List<RCSTransaction> list = persistenceUtil.load(criteria);
				if (Logger.logDebug()) {
					Logger.debug("Loaded " + list.size() + " transactions as tool chain input.");
				}
				iterator = list.iterator();
				
				if (startWith != null) {
					while (iterator.hasNext() && !(iterator.next().getId().equals(startWith))) {
						// drop
					}
				}
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					RCSTransaction transaction = iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + transaction);
					}
					
					providePartialOutputData(transaction);
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
}
