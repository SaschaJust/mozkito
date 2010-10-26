/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.cvs;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.Delta;

/**
 * @author just
 * 
 */
public class CVSRepository extends Repository {
	
	private static final RepositoryType REPOSITORY_TYPE = RepositoryType.CVS;
	
	@Override
	public List<AnnotationEntry> annotate(String filePath, String revision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public File checkoutPath(String relativeRepoPath, String revision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public Collection<Delta> diff(String filePath, String baseRevision, String revisedRevision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public Map<String, ChangeType> getChangedPaths(String revision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public String getFirstRevisionId() {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public String getLastRevisionId() {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public List<LogEntry> log(String fromRevision, String toRevision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public void setup(URI address) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
	}
	
	@Override
	public void setup(URI address, String username, String password) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
	}
	
}
