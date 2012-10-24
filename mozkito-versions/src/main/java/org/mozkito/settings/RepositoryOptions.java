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
package org.mozkito.settings;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RepositoryFactory;
import org.mozkito.versions.RepositoryType;
import org.mozkito.versions.model.RCSBranch;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class RepositoryOptions.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class RepositoryOptions extends ArgumentSetOptions<Repository, ArgumentSet<Repository, RepositoryOptions>> {
	
	/** The pass arg. */
	private StringArgument.Options               passArg;
	
	/** The repo dir arg. */
	private URIArgument.Options                  repoDirArg;
	
	/** The repo type arg. */
	private EnumArgument.Options<RepositoryType> repoTypeArg;
	
	/** The user arg. */
	private StringArgument.Options               userArg;
	
	/** The persistence util. */
	PersistenceUtil                              persistenceUtil;
	
	/** The branch factory. */
	private BranchFactory                        branchFactory;
	
	/** The tmp dir arg. */
	private DirectoryArgument.Options            tmpDirArg;
	
	/** The settings. */
	private final ISettings                      settings;
	
	/** The database options. */
	private final DatabaseOptions                databaseOptions;
	
	private Options                              mainBranchArg;
	
	/**
	 * Is an argument set that contains all arguments necessary for the repositories.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirement
	 *            the requirement
	 * @param databaseOptions
	 *            the database options
	 */
	public RepositoryOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement,
	        final DatabaseOptions databaseOptions) {
		super(argumentSet, "repository", "Options used to setup the repository environment.", requirement);
		this.settings = argumentSet.getSettings();
		this.databaseOptions = databaseOptions;
	}
	
	/**
	 * Gets the branch factory.
	 * 
	 * @return the branch factory
	 */
	public BranchFactory getBranchFactory() {
		if (this.branchFactory == null) {
			this.branchFactory = new BranchFactory(this.persistenceUtil);
		}
		return this.branchFactory;
	}
	
	public DatabaseOptions getDatabaseOptions() {
		return this.databaseOptions;
	}
	
	public StringArgument.Options getMainBranchArg() {
		return this.mainBranchArg;
	}
	
	/**
	 * Gets the pass arg.
	 * 
	 * @return the pass arg
	 */
	public StringArgument.Options getPassArg() {
		return this.passArg;
	}
	
	/**
	 * Gets the repo dir arg.
	 * 
	 * @return the repo dir arg
	 */
	public URIArgument.Options getRepoDirArg() {
		return this.repoDirArg;
	}
	
	/**
	 * Gets the repo type arg.
	 * 
	 * @return the repo type arg
	 */
	public EnumArgument.Options<RepositoryType> getRepoTypeArg() {
		return this.repoTypeArg;
	}
	
	/**
	 * Gets the tmp dir arg.
	 * 
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
	
	/**
	 * Gets the user arg.
	 * 
	 * @return the user arg
	 */
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
			final StringArgument mainBranchArgument = getSettings().getArgument(getMainBranchArg());
			
			this.persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			
			RCSBranch.setMasterBranchName(mainBranchArgument.getValue());
			
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
					repository.setup(repositoryURI, getBranchFactory(), tmpDirArgument.getValue(),
					                 mainBranchArgument.getValue());
				} else {
					repository.setup(repositoryURI, username, password, getBranchFactory(), tmpDirArgument.getValue(),
					                 mainBranchArgument.getValue());
				}
				
				this.settings.addInformation(repository.getHandle(), repository.gatherToolInformation());
				
				return repository;
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e);
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
			
			this.mainBranchArg = new StringArgument.Options(
			                                                set,
			                                                "mainBranch",
			                                                "The name of the main branch. Usually `master` or `trunk`.",
			                                                "master", Requirement.required);
			map.put(this.mainBranchArg.getName(), this.mainBranchArg);
			
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the new persistence util
	 */
	public void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
}
