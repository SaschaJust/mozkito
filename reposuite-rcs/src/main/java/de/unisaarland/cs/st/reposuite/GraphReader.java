/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class GraphReader extends RepoSuiteSourceThread<RCSTransaction> {
	
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public GraphReader(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, RepositoryPersister.class.getSimpleName(), settings);
		this.persistenceUtil = persistenceUtil;
	}
	
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			Criteria<RCSTransaction> criteria = this.persistenceUtil.createCriteria(RCSTransaction.class);
			List<RCSTransaction> list = this.persistenceUtil.load(criteria);
			
			for (RCSTransaction transaction : list) {
				if (Logger.logDebug()) {
					Logger.debug("Providing " + transaction + ".");
				}
				
				write(transaction);
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
