/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.cvs;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.Delta;

/**
 * @author just
 * 
 */
public class CVSRepository extends Repository {
	
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
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
	public String getFormerPathName(final String revision, final String pathName) {
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
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
		return null;
	}
	
	@Override
	public void setup(final URI address) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
	}
	
	@Override
	public void setup(final URI address, final String username, final String password) {
		if (RepoSuiteSettings.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepoSuiteSettings.reportThis);
		}
	}
	
}
