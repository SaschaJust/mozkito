/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.List;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RepositoryFactory;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends Thread {
	
	@Override
	public void run() {
		try {
			Repository repository = RepositoryFactory.getRepositoryHandler(RepositoryType.SUBVERSION).newInstance();
			List<LogEntry> logs = repository.log(repository.getFirstRevisionId(), "HEAD");
			RCSTransaction previousRcsTransaction = null;
			
			for (LogEntry entry : logs) {
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
				        entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
				previousRcsTransaction = rcsTransaction;
			}
		} catch (Exception e) {
			
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
}
