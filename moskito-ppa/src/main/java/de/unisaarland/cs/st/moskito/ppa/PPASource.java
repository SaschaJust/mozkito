/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.HashSet;
import java.util.Iterator;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.apache.maven.surefire.shade.org.codehaus.plexus.util.StringUtils;

import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPASource extends Source<RCSTransaction> {
	
	private Iterator<RCSTransaction> iterator;
	
	public PPASource(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final HashSet<String> transactionLimit) {
		super(threadGroup, settings, false);
		
		if (Logger.logDebug()) {
			Logger.debug("transactionLimit: "
			        + StringUtils.join(transactionLimit.toArray(new String[transactionLimit.size()]), ","));
		}
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				
				final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				
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
				
				PPASource.this.iterator = persistenceUtil.load(criteria).iterator();
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (PPASource.this.iterator.hasNext()) {
					final RCSTransaction transaction = PPASource.this.iterator.next();
					
					final Criteria<JavaChangeOperation> skipCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
					                                                                  .in("revision",
					                                                                      transaction.getRevisions());
					if (!persistenceUtil.load(skipCriteria).isEmpty()) {
						skipOutputData();
					} else {
						
						if (Logger.logDebug()) {
							Logger.debug("Providing " + transaction);
						}
						
						providePartialOutputData(transaction);
					}
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
}
