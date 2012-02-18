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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.arguments.MaskedStringArgument;
import net.ownhero.dev.andama.settings.arguments.StringArgument;
import net.ownhero.dev.andama.settings.arguments.URIArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Requirement;
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
public class RepositoryArguments extends ArgumentSet<Repository> {
	
	private final StringArgument               endRevision;
	private final MaskedStringArgument         passArg;
	private final URIArgument                  repoDirArg;
	private final EnumArgument<RepositoryType> repoTypeArg;
	private final StringArgument               startRevision;
	private final MaskedStringArgument         userArg;
	
	PersistenceUtil                            persistenceUtil;
	private BranchFactory                      branchFactory;
	
	/**
	 * Is an argument set that contains all arguments necessary for the repositories.
	 * 
	 * @param settings
	 * @param requirement
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments(final ArgumentSet<?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Options used to setup the repository environment.", requirement);
		this.repoDirArg = new URIArgument(this, "repository.uri", "URI where the rcs repository is located", null,
		                                  requirement);
		this.repoTypeArg = new EnumArgument<RepositoryType>(this, "repository.type", "Type of the repository.",
		                                                    RepositoryType.GIT, requirement);
		this.userArg = new MaskedStringArgument(this, "repository.user", "Username to access repository", null,
		                                        new Optional());
		this.passArg = new MaskedStringArgument(this, "repository.password", "Password to access repository", null,
		                                        new Optional());
		this.startRevision = new StringArgument(this, "repository.transaction.start", "Revision to start with", null,
		                                        new Optional());
		this.endRevision = new StringArgument(this, "repository.transaction.stop", "Revision to stop at", "HEAD",
		                                      new Optional());
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
	
	public EnumArgument<RepositoryType> getRepoTypeArg() {
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
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		final URI repositoryURI = getRepoDirArg().getValue();
		String username = getUserArg().getValue();
		String password = getPassArg().getValue();
		final String startRevision = getStartRevision().getValue();
		final String endRevision = this.endRevision.getValue();
		
		if (JavaUtils.AnyNull(repositoryURI, getRepoTypeArg().getValue())) {
			return false;
		}
		
		final RepositoryType rcsType = getRepoTypeArg().getValue();
		
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
			
			getSettings().addToolInformation(repository.getHandle(), repository.gatherToolInformation());
			
			setCachedValue(repository);
			return true;
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
	}
	
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
}
