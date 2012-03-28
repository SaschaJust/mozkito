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
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
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
public class RepositoryOptions extends ArgumentSetOptions<Repository, ArgumentSet<Repository, RepositoryOptions>> {
	
	private StringArgument.Options               passArg;
	private URIArgument.Options                  repoDirArg;
	private EnumArgument.Options<RepositoryType> repoTypeArg;
	private StringArgument.Options               userArg;
	
	PersistenceUtil                              persistenceUtil;
	private BranchFactory                        branchFactory;
	private DirectoryArgument.Options            tmpDirArg;
	private final ISettings                      settings;
	private final DatabaseOptions                databaseOptions;
	
	/**
	 * Is an argument set that contains all arguments necessary for the repositories.
	 * 
	 * @param settings
	 * @param requirement
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public RepositoryOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement,
	        final DatabaseOptions databaseOptions) {
		super(argumentSet, "repository", "Options used to setup the repository environment.", requirement);
		this.settings = argumentSet.getSettings();
		this.databaseOptions = databaseOptions;
	}
	
	public BranchFactory getBranchFactory() {
		if (this.branchFactory == null) {
			this.branchFactory = new BranchFactory(this.persistenceUtil);
		}
		return this.branchFactory;
	}
	
	public StringArgument.Options getPassArg() {
		return this.passArg;
	}
	
	public URIArgument.Options getRepoDirArg() {
		return this.repoDirArg;
	}
	
	public EnumArgument.Options<RepositoryType> getRepoTypeArg() {
		return this.repoTypeArg;
	}
	
	/**
	 * @return the tmpDirArg
	 */
	public final DirectoryArgument.Options getTmpDirArg() {
		// PRECONDITIONS
		
		try {
			return this.tmpDirArg;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.tmpDirArg, "Field '%s' in '%s'.", "tmpDirArg", getClass().getSimpleName());
		}
	}
	
	public StringArgument.Options getUserArg() {
		return this.userArg;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	public Repository init() {
		// PRECONDITIONS
		
		try {
			final URIArgument dirArgument = getSettings().getArgument(getRepoDirArg());
			final StringArgument userArgument = getSettings().getArgument(getUserArg());
			final StringArgument passwordArgument = getSettings().getArgument(getPassArg());
			final EnumArgument<RepositoryType> typeArgument = getSettings().getArgument(getRepoTypeArg());
			final DirectoryArgument tmpDirArgument = getSettings().getArgument(getTmpDirArg());
			this.persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			
			final URI repositoryURI = dirArgument.getValue();
			String username = userArgument.getValue();
			String password = passwordArgument.getValue();
			
			final RepositoryType rcsType = typeArgument.getValue();
			
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
					repository.setup(repositoryURI, getBranchFactory(), tmpDirArgument.getValue());
				} else {
					repository.setup(repositoryURI, username, password, getBranchFactory(), tmpDirArgument.getValue());
				}
				
				this.settings.addInformation(repository.getHandle(), repository.gatherToolInformation());
				
				return repository;
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.repoDirArg = new URIArgument.Options(set, "uri", "URI where the rcs repository is located", null,
			                                          Requirement.required);
			map.put(this.repoDirArg.getName(), this.repoDirArg);
			this.repoTypeArg = new EnumArgument.Options<RepositoryType>(set, "type", "Type of the repository.",
			                                                            RepositoryType.GIT, Requirement.required);
			map.put(this.repoTypeArg.getName(), this.repoTypeArg);
			this.userArg = new StringArgument.Options(set, "user", "Username to access repository", null,
			                                          Requirement.optional);
			map.put(this.userArg.getName(), this.userArg);
			this.passArg = new StringArgument.Options(set, "password", "Password to access repository", null,
			                                          Requirement.optional, true);
			map.put(this.passArg.getName(), this.passArg);
			this.tmpDirArg = new DirectoryArgument.Options(set, "tmpDir",
			                                               "Directory to be used to clone instances of repository.",
			                                               null, Requirement.optional, false);
			map.put(this.tmpDirArg.getName(), this.tmpDirArg);
			
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
}
