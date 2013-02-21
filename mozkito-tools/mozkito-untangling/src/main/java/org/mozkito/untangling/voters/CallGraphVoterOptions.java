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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.settings.RepositoryOptions;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.ChangeSet;

import serp.util.Strings;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class CallGraphVoterOptions
        extends
        ArgumentSetOptions<CallGraphVoterOptions.Factory, ArgumentSet<CallGraphVoterOptions.Factory, CallGraphVoterOptions>> {
	
	/**
	 * The Class Factory.
	 */
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<CallGraphVoter> {
		
		/** The eclipse dir. */
		private final File     eclipseDir;
		
		/** The cache dir. */
		private final File     cacheDir;
		
		/** The eclipse arguments. */
		private final String[] eclipseArguments;
		
		/**
		 * Instantiates a new factory.
		 * 
		 * @param eclipseDir
		 *            the eclipse dir
		 * @param eclipseArguments
		 *            the eclipse arguments
		 * @param cacheDir
		 *            the cache dir
		 */
		protected Factory(final File eclipseDir, final String[] eclipseArguments, final File cacheDir) {
			this.eclipseDir = eclipseDir;
			this.cacheDir = cacheDir;
			this.eclipseArguments = eclipseArguments;
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#createVoter(org.mozkito.versions.model
		 * .ChangeSet)
		 */
		@Override
		public CallGraphVoter createVoter(final ChangeSet changeset) {
			return new CallGraphVoter(this.eclipseDir, this.eclipseArguments, changeset, this.cacheDir);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#getVoterName()
		 */
		@Override
		public String getVoterName() {
			// PRECONDITIONS
			
			try {
				return CallGraphVoter.class.getSimpleName();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The callgraph eclipse options. */
	private net.ownhero.dev.hiari.settings.DirectoryArgument.Options callgraphEclipseOptions;
	
	/** The call graph cache dir options. */
	private net.ownhero.dev.hiari.settings.DirectoryArgument.Options callGraphCacheDirOptions;
	
	/** The repository options. */
	private final RepositoryOptions                                  repositoryOptions;
	
	/** The negative filename list argument. */
	private net.ownhero.dev.hiari.settings.ListArgument.Options      negativeFilenameListArgument;
	
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
	public CallGraphVoterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final RepositoryOptions repositoryOptions) {
		super(argumentSet, "callGraphVoter", "CallGraphVoter options.", requirements);
		this.repositoryOptions = repositoryOptions;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public Factory init() {
		// PRECONDITIONS
		final File callgraphEclipse = getSettings().getArgument(this.callgraphEclipseOptions).getValue();
		final File callGraphCacheDir = getSettings().getArgument(this.callGraphCacheDirOptions).getValue();
		final Repository repository = getSettings().getArgumentSet(this.repositoryOptions).getValue();
		
		final DatabaseOptions databaseOptions = this.repositoryOptions.getDatabaseOptions();
		final ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArgs = getSettings().getArgumentSet(databaseOptions);
		
		final List<String> negativeFilenameList = getSettings().getArgument(this.negativeFilenameListArgument)
		                                                       .getValue();
		
		final List<String> eclipseArgs = new LinkedList<String>();
		eclipseArgs.add("-vmargs");
		eclipseArgs.add("-Dppa");
		eclipseArgs.add("-Drepository.uri=" + repository.getUri().toASCIIString());
		eclipseArgs.add("-Ddabase.host=" + databaseArgs.getArgument(databaseOptions.getDatabaseHost()).getValue());
		eclipseArgs.add("-Ddabase.user=" + databaseArgs.getArgument(databaseOptions.getDatabaseUser()).getValue());
		eclipseArgs.add("-Ddatabase.middleware="
		        + databaseArgs.getArgument(databaseOptions.getDatabaseMiddleware()).getValue());
		eclipseArgs.add("-Ddatabase.name=" + databaseArgs.getArgument(databaseOptions.getDatabaseName()).getValue());
		eclipseArgs.add("-Ddatabase.password="
		        + databaseArgs.getArgument(databaseOptions.getDatabasePassword()).getValue());
		eclipseArgs.add("-Ddatabase.type="
		        + databaseArgs.getArgument(databaseOptions.getDatabaseType()).getValue().toString());
		eclipseArgs.add("-Ddatabase.unit=" + databaseArgs.getArgument(databaseOptions.getDatabaseUnit()).getValue());
		if (!negativeFilenameList.isEmpty()) {
			eclipseArgs.add("-DnegativeFileFilter="
			        + Strings.join(negativeFilenameList.toArray(new String[negativeFilenameList.size()]), ","));
		}
		return new Factory(callgraphEclipse, eclipseArgs.toArray(new String[eclipseArgs.size()]), callGraphCacheDir);
		
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
		this.callgraphEclipseOptions = new DirectoryArgument.Options(
		                                                             argumentSet,
		                                                             "eclipseHome",
		                                                             "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
		                                                             null, Requirement.required, false);
		map.put(this.callgraphEclipseOptions.getName(), this.callgraphEclipseOptions);
		
		this.callGraphCacheDirOptions = new DirectoryArgument.Options(
		                                                              argumentSet,
		                                                              "cacheDir",
		                                                              "Cache directory containing call graphs using the naming converntion <changeSetId>.cg",
		                                                              null, Requirement.required, false);
		map.put(this.callGraphCacheDirOptions.getName(), this.callGraphCacheDirOptions);
		
		this.negativeFilenameListArgument = new ListArgument.Options(
		                                                             argumentSet,
		                                                             "negativeFileFilter",
		                                                             "Ignore source files whose file name ends of one of these strings. (entries are separated using ',')",
		                                                             new ArrayList<String>(0), Requirement.optional);
		map.put(this.negativeFilenameListArgument.getName(), this.negativeFilenameListArgument);
		return map;
	}
}
