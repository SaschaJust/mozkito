/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryFactory;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
			RCSFileManager fileManager = new RCSFileManager();
			
			for (LogEntry entry : logs) {
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
				        entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
				Map<String, ChangeType> changedPaths = repository.getChangedPaths(entry.getRevision());
				for (String fileName : changedPaths.keySet()) {
					// FIXME this will fail so badly
					// we need oldFileName iff the file has been renamed
					
					RCSFile file = fileManager.getFile(fileName);
					if (file == null) {
						fileManager.addFile(new RCSFile(fileName, rcsTransaction));
					} else if (changedPaths.get(fileName).equals(ChangeType.Modified)) {
						file.assignTransaction(rcsTransaction, fileName);
					}
					
					rcsTransaction.addRevision(new RCSRevision(rcsTransaction, file, changedPaths.get(fileName),
					        previousRcsTransaction));
				}
				
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
