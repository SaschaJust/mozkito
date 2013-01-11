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

package org.mozkito.genealogies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.RCSPersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.collections.ChangeSetSet;
import org.mozkito.versions.collections.ChangeSetSet.TransactionSetOrder;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeOperationReader.
 */
public class ChangeOperationReader implements Iterator<Collection<JavaChangeOperation>> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<ChangeOperationReader, ArgumentSet<ChangeOperationReader, Options>> {
		
		/** The branch name options. */
		private StringArgument.Options                                 branchNameOptions;
		
		/** The database options. */
		private final DatabaseOptions                                  databaseOptions;
		
		/** The skip tests options. */
		private net.ownhero.dev.hiari.settings.BooleanArgument.Options skipTestsOptions;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 * @param databaseOptions
		 *            the database options
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
		        final DatabaseOptions databaseOptions) {
			super(argumentSet, "genealogyReader", "ChangeOperationReader options.", requirements);
			this.databaseOptions = databaseOptions;
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ChangeOperationReader init() {
			final String branchName = getSettings().getArgument(this.branchNameOptions).getValue();
			final Boolean skipTests = getSettings().getArgument(this.skipTestsOptions).getValue();
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			return new ChangeOperationReader(persistenceUtil, branchName, skipTests);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.branchNameOptions = new StringArgument.Options(
			                                                    argumentSet,
			                                                    "branch",
			                                                    "Create tha change genealogy that corresponds to the given branch. Genealogies accross branches are not supported yet.",
			                                                    Branch.MASTER_BRANCH_NAME, Requirement.required);
			
			this.skipTestsOptions = new BooleanArgument.Options(
			                                                    argumentSet,
			                                                    "ignoreTests",
			                                                    "Set to false if test cases shall be contained by resulting change genealogy.",
			                                                    true, Requirement.required);
			map.put(this.skipTestsOptions.getName(), this.skipTestsOptions);
			map.put(this.branchNameOptions.getName(), this.branchNameOptions);
			return map;
		}
	}
	
	/** The iterator. */
	private final Iterator<ChangeSet> iterator;
	
	/** The branch factory. */
	private final BranchFactory       branchFactory;
	
	/** The persistence util. */
	private final PersistenceUtil     persistenceUtil;
	
	/** The ignore tests. */
	private final boolean             ignoreTests;
	
	/** The num transaction. */
	private final int                 numTransaction;
	
	/** The t counter. */
	private int                       tCounter = 0;
	
	/**
	 * Instantiates a new change operation reader.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @param branchName
	 *            the branch name
	 * @param ignoreTests
	 *            the ignore tests
	 */
	private ChangeOperationReader(final PersistenceUtil persistenceUtil, final String branchName,
	        final boolean ignoreTests) {
		this.persistenceUtil = persistenceUtil;
		this.branchFactory = new BranchFactory(persistenceUtil);
		this.ignoreTests = ignoreTests;
		final Branch masterBranch = persistenceUtil.loadById(branchName, Branch.class);
		
		if (masterBranch == null) {
			final List<String> branchNames = new LinkedList<>();
			for (final Branch rCSBranch : persistenceUtil.load(persistenceUtil.createCriteria(Branch.class))) {
				branchNames.add(rCSBranch.getName());
			}
			if (Logger.logError()) {
				Logger.error("Could not find a branch with name %s. Cannot create genealogy graph. Temrinating. Possible branch names are: %s.",
				             branchName, JavaUtils.collectionToString(branchNames));
			}
			throw new Shutdown();
		}
		
		final ChangeSetSet masterChangeSets = RCSPersistenceUtil.getChangeSet(this.branchFactory.getPersistenceUtil(),
		                                                                             masterBranch,
		                                                                             TransactionSetOrder.ASC);
		if (Logger.logInfo()) {
			Logger.info("Added " + masterChangeSets.size()
			                    + " ChangeSet that were found in branch %s to build the change genealogy.",
			            this.branchFactory.getMasterBranch().getName());
		}
		this.numTransaction = masterChangeSets.size();
		this.iterator = masterChangeSets.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// PRECONDITIONS
		
		try {
			return this.iterator.hasNext();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Collection<JavaChangeOperation> next() {
		// PRECONDITIONS
		++this.tCounter;
		try {
			final ChangeSet changeSet = this.iterator.next();
			if (Logger.logInfo()) {
				Logger.info("Processing transaction %s (%s/%s).", changeSet.getId(), String.valueOf(this.tCounter),
				            String.valueOf(this.numTransaction));
			}
			Collection<JavaChangeOperation> changeOperations = new ArrayList<JavaChangeOperation>(0);
			
			if (this.ignoreTests) {
				changeOperations = PPAPersistenceUtil.getChangeOperationNoTest(this.persistenceUtil, changeSet);
			} else {
				changeOperations = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, changeSet);
			}
			return changeOperations;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// PRECONDITIONS
		
		try {
			this.iterator.remove();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
