/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryVoidSink extends RepoSuiteSinkThread<RCSTransaction> {
	
	/**
	 * @param threadGroup
	 * @param name
	 */
	public RepositoryVoidSink(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryVoidSink.class.getSimpleName(), settings);
	}
	
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		RCSTransaction currentTransaction;
		try {
			while (!isShutdown() && ((currentTransaction = this.inputStorage.read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Taking " + currentTransaction + " from input connector and forgetting it.");
				}
				
			}
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
