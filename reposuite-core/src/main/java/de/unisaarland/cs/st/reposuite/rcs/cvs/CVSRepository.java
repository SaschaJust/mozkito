/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.cvs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.AnnotationEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import difflib.Delta;

/**
 * @author just
 * 
 */
public class CVSRepository extends Repository {
	
	@Override
	public List<AnnotationEntry> annotate(final String filePath, final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public File checkoutPath(final String relativeRepoPath, final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public Collection<Delta> diff(final String filePath, final String baseRevision, final String revisedRevision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String gatherToolInformation() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getFirstRevisionId() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getFormerPathName(final String revision, final String pathName) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getLastRevisionId() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getRelativeTransactionId(final String transactionId, final long index) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public long getTransactionCount() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getTransactionId(final long index) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public List<LogEntry> log(final String fromRevision, final String toRevision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public Iterator<LogEntry> log(final String fromRevision, final String toRevision, final int cacheSize) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
	}
	
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision)
	        throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI, UnsupportedProtocolType {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
		
	}
	
	@Override
	public void setup(final URI address, final String startRevision, final String endRevision, final String username,
	        final String password) throws MalformedURLException, InvalidProtocolType, InvalidRepositoryURI,
	        UnsupportedProtocolType {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + RepositorySettings.reportThis);
		}
		throw new RuntimeException();
		
	}
	
}
