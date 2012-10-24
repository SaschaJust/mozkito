/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.mozkito.versions.cvs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.mozkito.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.mozkito.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.BranchFactory;
import de.unisaarland.cs.st.mozkito.versions.IRevDependencyGraph;
import de.unisaarland.cs.st.mozkito.versions.Repository;
import de.unisaarland.cs.st.mozkito.versions.elements.AnnotationEntry;
import de.unisaarland.cs.st.mozkito.versions.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.versions.elements.LogEntry;
import difflib.Delta;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CVSRepository extends Repository {
	
	@Override
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String gatherToolInformation() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getFirstRevisionId() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getFormerPathName(final String revision,
	                                final String pathName) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getHEADRevisionId() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getRelativeTransactionId(final String transactionId,
	                                       final long index) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public IRevDependencyGraph getRevDependencyGraph() {
		// PRECONDITIONS
		
		try {
			throw new UnrecoverableError("Support hasn't been implemented yet. " + Settings.getReportThis());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public IRevDependencyGraph getRevDependencyGraph(final PersistenceUtil persistenceUtil) {
		// PRECONDITIONS
		
		try {
			throw new UnrecoverableError("Support hasn't been implemented yet. " + Settings.getReportThis());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public long getTransactionCount() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public String getTransactionId(final long index) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public File getWokingCopyLocation() {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public List<LogEntry> log(final String fromRevision,
	                          final String toRevision) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public Iterator<LogEntry> log(final String fromRevision,
	                              final String toRevision,
	                              final int cacheSize) {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
	}
	
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws MalformedURLException,
	                                                       InvalidProtocolType,
	                                                       InvalidRepositoryURI,
	                                                       UnsupportedProtocolType {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
		
	}
	
	@Override
	public void setup(@NotNull final URI address,
	                  @NotNull final String username,
	                  @NotNull final String password,
	                  @NotNull final BranchFactory branchFactory,
	                  final File tmpDir,
	                  @NotNull final String mainBranchName) throws MalformedURLException,
	                                                       InvalidProtocolType,
	                                                       InvalidRepositoryURI,
	                                                       UnsupportedProtocolType {
		if (Logger.logError()) {
			Logger.error("CVS support hasn't been implemented yet. " + Settings.getReportThis());
		}
		throw new RuntimeException();
		
	}
	
}
