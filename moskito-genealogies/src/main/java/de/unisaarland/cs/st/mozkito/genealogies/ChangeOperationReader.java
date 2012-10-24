/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/

package de.unisaarland.cs.st.mozkito.genealogies;

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
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.RCSPersistenceUtil;
import de.unisaarland.cs.st.mozkito.settings.DatabaseOptions;
import de.unisaarland.cs.st.mozkito.versions.BranchFactory;
import de.unisaarland.cs.st.mozkito.versions.collections.TransactionSet;
import de.unisaarland.cs.st.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.mozkito.versions.model.RCSBranch;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

public class ChangeOperationReader implements Iterator<Collection<JavaChangeOperation>> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<ChangeOperationReader, ArgumentSet<ChangeOperationReader, Options>> {
		
		private StringArgument.Options                                 branchNameOptions;
		private final DatabaseOptions                                  databaseOptions;
		private net.ownhero.dev.hiari.settings.BooleanArgument.Options skipTestsOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
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
			                                                    RCSBranch.MASTER_BRANCH_NAME, Requirement.required);
			
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
	
	private final Iterator<RCSTransaction> iterator;
	private final BranchFactory            branchFactory;
	private final PersistenceUtil          persistenceUtil;
	private final boolean                  ignoreTests;
	private final int                      numTransaction;
	private int                            tCounter = 0;
	
	private ChangeOperationReader(final PersistenceUtil persistenceUtil, final String branchName,
	        final boolean ignoreTests) {
		this.persistenceUtil = persistenceUtil;
		this.branchFactory = new BranchFactory(persistenceUtil);
		this.ignoreTests = ignoreTests;
		final RCSBranch masterBranch = persistenceUtil.loadById(branchName, RCSBranch.class);
		
		if (masterBranch == null) {
			final List<String> branchNames = new LinkedList<>();
			for (final RCSBranch branch : persistenceUtil.load(persistenceUtil.createCriteria(RCSBranch.class))) {
				branchNames.add(branch.getName());
			}
			if (Logger.logError()) {
				Logger.error("Could not find a branch with name %s. Cannot create genealogy graph. Temrinating. Possible branch names are: %s.",
				             branchName, JavaUtils.collectionToString(branchNames));
			}
			throw new Shutdown();
		}
		
		final TransactionSet masterTransactions = RCSPersistenceUtil.getTransactions(this.branchFactory.getPersistenceUtil(),
		                                                                             masterBranch,
		                                                                             TransactionSetOrder.ASC);
		if (Logger.logInfo()) {
			Logger.info("Added " + masterTransactions.size()
			                    + " RCSTransactions that were found in branch %s to build the change genealogy.",
			            this.branchFactory.getMasterBranch().getName());
		}
		this.numTransaction = masterTransactions.size();
		this.iterator = masterTransactions.iterator();
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
			final RCSTransaction transaction = this.iterator.next();
			if (Logger.logInfo()) {
				Logger.info("Processing transaction %s (%s/%s).", transaction.getId(), String.valueOf(this.tCounter),
				            String.valueOf(this.numTransaction));
			}
			Collection<JavaChangeOperation> changeOperations = new ArrayList<JavaChangeOperation>(0);
			
			if (this.ignoreTests) {
				changeOperations = PPAPersistenceUtil.getChangeOperationNoTest(this.persistenceUtil, transaction);
			} else {
				changeOperations = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, transaction);
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
