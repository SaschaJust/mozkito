/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends Thread {
	
	public static String getHandle() {
		return RepositoryAnalyzer.class.getSimpleName();
	}
	
	@Override
	public void run() {
		try {
			
			RepoSuiteSettings settings = new RepoSuiteSettings();
			RepositoryArguments repoSettings = settings.setRepositoryArg(true);
			settings.parseArguments();
			
			Repository repository = repoSettings.getValue();
			
			if (RepoSuiteSettings.logInfo()) {
				Logger.info("Requesting logs from " + repository);
			}
			
			List<LogEntry> logs = repository.log(repository.getFirstRevisionId(), "HEAD");
			RCSTransaction previousRcsTransaction = null;
			RCSFileManager fileManager = new RCSFileManager();
			
			if (RepoSuiteSettings.logInfo()) {
				Logger.info("Parsing " + logs.size() + " transactions."
				        + (logs.size() > 1000 ? " This might take a while." : ""));
			}
			
			for (LogEntry entry : logs) {
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
				        entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
				Map<String, ChangeType> changedPaths = repository.getChangedPaths(entry.getRevision());
				for (String fileName : changedPaths.keySet()) {
					RCSFile file;
					
					if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
						file = fileManager.getFile(repository.getFormerPathName(rcsTransaction.getId(), fileName));
						assert (file != null);
						file.assignTransaction(rcsTransaction, fileName);
						
					} else {
						file = fileManager.getFile(fileName);
						
						if (file == null) {
							file = new RCSFile(fileName, rcsTransaction);
							fileManager.addFile(file);
						}
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
