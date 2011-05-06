/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.model.RCSFile2Bugs;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class Files2BugsReader extends RepoSuiteSourceThread<RCSFile2Bugs> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public Files2BugsReader(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, ScoringPersister.class.getSimpleName(), settings);
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
			
			List<RCSFile2Bugs> list = RCSFile2Bugs.getBugCounts();
			
			for (RCSFile2Bugs file2Bugs : list) {
				if (Logger.logDebug()) {
					Logger.debug("Providing " + file2Bugs + ".");
				}
				
				write(file2Bugs);
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
