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
package de.unisaarland.cs.st.moskito.settings;

import java.net.URI;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.MaskedStringArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.andama.settings.URIArgument;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.RepositoryFactory;
import de.unisaarland.cs.st.moskito.rcs.RepositoryType;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositoryArguments extends AndamaArgumentSet<Repository> {
	
	private final StringArgument endRevision;
	private final StringArgument passArg;
	private final URIArgument    repoDirArg;
	private final EnumArgument   repoTypeArg;
	private final StringArgument startRevision;
	private final StringArgument userArg;
	private final AndamaSettings settings;
	
	PersistenceUtil              persistenceUtil;
	private BranchFactory        branchFactory;
	
	/**
	 * Is an argument set that contains all arguments necessary for the repositories.
	 * 
	 * @param settings
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments(final RepositorySettings settings, final boolean isRequired) {
		super();
		this.repoDirArg = new URIArgument(settings, "repository.uri", "URI where the rcs repository is located", null,
		                                  isRequired);
		this.repoTypeArg = new EnumArgument(settings, "repository.type", "Type of the repository. Possible values: "
		        + JavaUtils.enumToString(RepositoryType.SUBVERSION), null, isRequired,
		                                    JavaUtils.enumToArray(RepositoryType.SUBVERSION));
		this.userArg = new MaskedStringArgument(settings, "repository.user", "Username to access repository", null,
		                                        false);
		this.passArg = new MaskedStringArgument(settings, "repository.password", "Password to access repository", null,
		                                        false);
		this.startRevision = new StringArgument(settings, "repository.transaction.start", "Revision to start with",
		                                        null, false);
		this.endRevision = new StringArgument(settings, "repository.transaction.stop", "Revision to stop at", "HEAD",
		                                      false);
		this.settings = settings;
	}
	
	public BranchFactory getBranchFactory() {
		if (this.branchFactory == null) {
			this.branchFactory = new BranchFactory(this.persistenceUtil);
		}
		return this.branchFactory;
	}
	
	public StringArgument getPassArg() {
		return this.passArg;
	}
	
	public URIArgument getRepoDirArg() {
		return this.repoDirArg;
	}
	
	public EnumArgument getRepoTypeArg() {
		return this.repoTypeArg;
	}
	
	public StringArgument getStartRevision() {
		return this.startRevision;
	}
	
	public StringArgument getUserArg() {
		return this.userArg;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Repository getValue() {
		final URI repositoryURI = getRepoDirArg().getValue();
		String username = getUserArg().getValue();
		String password = getPassArg().getValue();
		final String startRevision = getStartRevision().getValue();
		final String endRevision = this.endRevision.getValue();
		
		if (JavaUtils.AnyNull(repositoryURI, getRepoTypeArg().getValue())) { return null; }
		
		final RepositoryType rcsType = RepositoryType.valueOf(getRepoTypeArg().getValue());
		
		if (((username == null) && (password != null)) || ((username != null) && (password == null))) {
			if (Logger.logWarn()) {
				Logger.warn("You provided username or password only. Ignoring set options.");
			}
			username = null;
			password = null;
		}
		
		try {
			final Class<? extends Repository> repositoryClass = RepositoryFactory.getRepositoryHandler(rcsType);
			final Repository repository = repositoryClass.newInstance();
			
			if (this.persistenceUtil == null) {
				if (Logger.logWarn()) {
					Logger.warn("PersistenceUtil is null, but should be set prior to calling getValue in "
					        + this.getClass().getSimpleName() + ".");
				}
			}
			
			if ((username == null) && (password == null)) {
				repository.setup(repositoryURI, startRevision, endRevision, getBranchFactory());
			} else {
				repository.setup(repositoryURI, startRevision, endRevision, username, password, getBranchFactory());
			}
			
			this.settings.addToolInformation(repository.getHandle(), repository.gatherToolInformation());
			
			return repository;
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
}
