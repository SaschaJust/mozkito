/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.LoggerArguments;
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
			LoggerArguments logSettings = settings.setLoggerArg(true);
			new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface",
			        "false", false);
			settings.parseArguments();
			
			Repository repository = repoSettings.getValue();
			logSettings.getValue();
			
			if (Logger.logInfo()) {
				Logger.info("Requesting logs from " + repository);
			}
			
			List<LogEntry> logs = repository.log(repository.getFirstRevisionId(), "HEAD");
			RCSTransaction previousRcsTransaction = null;
			RCSFileManager fileManager = new RCSFileManager();
			
			if (Logger.logInfo()) {
				Logger.info("Analyzing repository for corruption.");
			}
			
			repository.consistencyCheck(logs);
			
			if (Logger.logInfo()) {
				Logger.info("Parsing " + logs.size() + " transactions."
				        + (logs.size() > 1000 ? " This might take a while." : ""));
			}
			
			for (LogEntry entry : logs) {
				
				if (Logger.logTrace()) {
					Logger.trace("Analyzing revision: " + entry.getRevision());
				}
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
				        entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
				Map<String, ChangeType> changedPaths = repository.getChangedPaths(entry.getRevision());
				for (String fileName : changedPaths.keySet()) {
					RCSFile file;
					
					if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
						file = fileManager.getFile(repository.getFormerPathName(rcsTransaction.getId(), fileName));
						if (file == null) {
							
							if (Logger.logWarn()) {
								Logger.warn("Found renaming of unknown file. Assuming type `added` instead of `renamed`: "
								        + changedPaths.get(fileName));
							}
							file = fileManager.getFile(fileName);
							
							if (file == null) {
								file = new RCSFile(fileName, rcsTransaction);
								fileManager.addFile(file);
							}
						} else {
							file.assignTransaction(rcsTransaction, fileName);
						}
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
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
}
