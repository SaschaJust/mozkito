/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.apache.maven.surefire.shade.org.codehaus.plexus.util.StringUtils;

import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPASource extends Source<RCSTransaction> {
	
	private Iterator<RCSTransaction> iterator;
	
	public PPASource(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final String startWith, final HashSet<String> transactionLimit) {
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
				final TreeSet<RCSTransaction> list = new TreeSet<RCSTransaction>();
				list.addAll(persistenceUtil.load(criteria));
				if (Logger.logDebug()) {
					Logger.debug("Loaded " + list.size() + " transactions as tool chain input.");
				}
				PPASource.this.iterator = list.iterator();
				
				if (startWith != null) {
					while (PPASource.this.iterator.hasNext()
					        && !(PPASource.this.iterator.next().getId().equals(startWith))) {
						// drop
					}
				}
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (PPASource.this.iterator.hasNext()) {
					final RCSTransaction transaction = PPASource.this.iterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + transaction);
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
