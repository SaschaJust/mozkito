/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * This class is a end point for the {@link Core} tool chain in case no database
 * connection is used. The data received from the previous node is void sinked.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryVoidSink extends RepoSuiteSinkThread<RCSTransaction> {
	
	/**
	 * @see RepoSuiteSinkThread
	 * @param threadGroup
	 * @param settings
	 */
	public RepositoryVoidSink(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryVoidSink.class.getSimpleName(), settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		RCSTransaction currentTransaction;
		try {
			while (!isShutdown() && ((currentTransaction = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Taking " + currentTransaction + " from input connector and forgetting it.");
				}
				
			}
			
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
