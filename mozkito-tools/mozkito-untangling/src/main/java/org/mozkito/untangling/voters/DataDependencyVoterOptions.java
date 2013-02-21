/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.untangling.voters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.settings.RepositoryOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.ChangeSet;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class DataDependencyVoterOptions
        extends
        ArgumentSetOptions<DataDependencyVoterOptions.Factory, ArgumentSet<DataDependencyVoterOptions.Factory, DataDependencyVoterOptions>> {
	
	/**
	 * The Class Factory.
	 */
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<DataDependencyVoter> {
		
		/** The cache dir. */
		private final File       cacheDir;
		
		/** The eclipse dir. */
		private final File       eclipseDir;
		
		/** The repository. */
		private final Repository repository;
		
		/**
		 * Instantiates a new factory.
		 * 
		 * @param eclipseDir
		 *            the eclipse dir
		 * @param repository
		 *            the repository
		 * @param cacheDir
		 *            the cache dir
		 */
		protected Factory(@NotNull final File eclipseDir, @NotNull final Repository repository, final File cacheDir) {
			this.eclipseDir = eclipseDir;
			this.repository = repository;
			this.cacheDir = cacheDir;
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#createVoter(org.mozkito.versions.model
		 * .ChangeSet)
		 */
		@Override
		public DataDependencyVoter createVoter(final ChangeSet changeset) {
			return new DataDependencyVoter(this.eclipseDir, this.repository, changeset, this.cacheDir);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#getVoterName()
		 */
		@Override
		public String getVoterName() {
			// PRECONDITIONS
			
			try {
				return DataDependencyVoter.class.getSimpleName();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The cache dir options. */
	private net.ownhero.dev.hiari.settings.DirectoryArgument.Options cacheDirOptions;
	
	/** The eclipse home options. */
	private net.ownhero.dev.hiari.settings.DirectoryArgument.Options eclipseHomeOptions;
	
	/** The repository options. */
	private final RepositoryOptions                                  repositoryOptions;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param repositoryOptions
	 *            the repository options
	 */
	public DataDependencyVoterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final RepositoryOptions repositoryOptions) {
		super(argumentSet, "dataDepVoter", "DataDependencyVoter options.", requirements);
		this.repositoryOptions = repositoryOptions;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public Factory init() {
		// PRECONDITIONS
		
		final Repository repository = getSettings().getArgumentSet(this.repositoryOptions).getValue();
		final File eclipseDir = getSettings().getArgument(this.eclipseHomeOptions).getValue();
		final File cacheDir = getSettings().getArgument(this.cacheDirOptions).getValue();
		
		return new Factory(eclipseDir, repository, cacheDir);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		// PRECONDITIONS
		final Map<String, IOptions<?, ?>> map = new HashMap<>();
		map.put(this.repositoryOptions.getName(), this.repositoryOptions);
		this.eclipseHomeOptions = new DirectoryArgument.Options(
		                                                        argumentSet,
		                                                        "eclipseHome",
		                                                        "Home directory of the reposuite datadependency applcation (must contain ./eclipse executable).",
		                                                        null, Requirement.required, false);
		map.put(this.eclipseHomeOptions.getName(), this.eclipseHomeOptions);
		this.cacheDirOptions = new DirectoryArgument.Options(
		                                                     argumentSet,
		                                                     "cacheDir",
		                                                     "Cache directory containing datadepency pre-computations using the naming converntion <changeSetId>.dd",
		                                                     null, Requirement.required, false);
		map.put(this.cacheDirOptions.getName(), this.cacheDirOptions);
		
		return map;
	}
}
