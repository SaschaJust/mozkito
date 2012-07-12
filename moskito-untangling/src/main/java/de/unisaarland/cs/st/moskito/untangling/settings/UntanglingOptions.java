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
package de.unisaarland.cs.st.moskito.untangling.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.LongArgument.Options;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseOptions;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy;
import de.unisaarland.cs.st.moskito.untangling.blob.combine.ChangeCouplingCombineOperator;
import de.unisaarland.cs.st.moskito.untangling.blob.combine.ConsecutiveChangeCombineOperator;
import de.unisaarland.cs.st.moskito.untangling.blob.combine.PackageDistanceCombineOperator;
import de.unisaarland.cs.st.moskito.untangling.voters.CallGraphVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.ChangeCouplingVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.DataDependencyVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.TestImpactVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.TestImpactVoter.Factory;

/**
 * The Class UntanglingOptions.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UntanglingOptions extends
        ArgumentSetOptions<UntanglingControl, ArgumentSet<UntanglingControl, UntanglingOptions>> {
	
	/** The repository options. */
	private RepositoryOptions                                                                             repositoryOptions;
	
	/** The atomic changes options. */
	private ListArgument.Options                                                                          atomicChangesOptions;
	
	/** The use call graph options. */
	private BooleanArgument.Options                                                                       useCallGraphOptions;
	
	/** The use change couplings options. */
	private BooleanArgument.Options                                                                       useChangeCouplingsOptions;
	
	/** The use data dependencies options. */
	private BooleanArgument.Options                                                                       useDataDependenciesOptions;
	
	/** The use test impact options. */
	private BooleanArgument.Options                                                                       useTestImpactOptions;
	
	/** The min blob size options. */
	private LongArgument.Options                                                                          minBlobSizeOptions;
	
	/** The max blob size options. */
	private LongArgument.Options                                                                          maxBlobSizeOptions;
	
	/** The out options. */
	private OutputFileArgument.Options                                                                    outOptions;
	
	/** The dry run options. */
	private BooleanArgument.Options                                                                       dryRunOptions;
	
	/** The n options. */
	private LongArgument.Options                                                                          nOptions;
	
	/** The seed options. */
	private LongArgument.Options                                                                          seedOptions;
	
	/** The collapse mode options. */
	private EnumArgument.Options<UntanglingCollapse>                                                      collapseModeOptions;
	
	/** The score mode options. */
	private EnumArgument.Options<ScoreCombinationMode>                                                    scoreModeOptions;
	
	/** The generator strategy options. */
	private EnumArgument.Options<ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy>                 generatorStrategyOptions;
	
	/** The database options. */
	private DatabaseOptions                                                                               databaseOptions;
	
	/** The change coupling combine options. */
	private de.unisaarland.cs.st.moskito.untangling.blob.combine.ChangeCouplingCombineOperator.Options    changeCouplingCombineOptions;
	
	/** The consecutive combine options. */
	private de.unisaarland.cs.st.moskito.untangling.blob.combine.ConsecutiveChangeCombineOperator.Options consecutiveCombineOptions;
	
	/** The blob window size options. */
	private Options                                                                                       blobWindowSizeOptions;
	
	/** The package distance combine options. */
	private de.unisaarland.cs.st.moskito.untangling.blob.combine.PackageDistanceCombineOperator.Options   packageDistanceCombineOptions;
	
	/** The call graph voter options. */
	private de.unisaarland.cs.st.moskito.untangling.voters.CallGraphVoter.Options                         callGraphVoterOptions;
	
	/** The change coupling voter options. */
	private de.unisaarland.cs.st.moskito.untangling.voters.ChangeCouplingVoter.Options                    changeCouplingVoterOptions;
	
	/** The data dependency voter options. */
	private de.unisaarland.cs.st.moskito.untangling.voters.DataDependencyVoter.Options                    dataDependencyVoterOptions;
	
	/** The test impact voter options. */
	private de.unisaarland.cs.st.moskito.untangling.voters.TestImpactVoter.Options                        testImpactVoterOptions;
	
	/**
	 * Instantiates a new untangling options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public UntanglingOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "untangling", "Options to configure the untangling process details.", requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public UntanglingControl init() {
		// PRECONDITIONS
		
		try {
			final UntanglingControl control = new UntanglingControl();
			
			final ArtificialBlobGeneratorStrategy strategy = getSettings().getArgument(this.generatorStrategyOptions)
			                                                              .getValue();
			switch (strategy) {
				case CONSECUTIVE:
					control.setCombineOperator(getSettings().getArgumentSet(this.consecutiveCombineOptions).getValue());
					break;
				case COUPLINGS:
					control.setCombineOperator(getSettings().getArgumentSet(this.changeCouplingCombineOptions)
					                                        .getValue());
					break;
				default:
					control.setCombineOperator(getSettings().getArgumentSet(this.packageDistanceCombineOptions)
					                                        .getValue());
					break;
			}
			
			final ArgumentSet<de.unisaarland.cs.st.moskito.untangling.voters.CallGraphVoter.Factory, de.unisaarland.cs.st.moskito.untangling.voters.CallGraphVoter.Options> callGraphVoterArg = getSettings().getArgumentSet(this.callGraphVoterOptions);
			if (callGraphVoterArg != null) {
				final CallGraphVoter.Factory callGraphVoter = callGraphVoterArg.getValue();
				if (callGraphVoter != null) {
					control.addConfidenceVoter(callGraphVoter);
				}
			}
			
			final ArgumentSet<de.unisaarland.cs.st.moskito.untangling.voters.ChangeCouplingVoter.Factory, de.unisaarland.cs.st.moskito.untangling.voters.ChangeCouplingVoter.Options> changeCouplingVoterArg = getSettings().getArgumentSet(this.changeCouplingVoterOptions);
			if (changeCouplingVoterArg != null) {
				final ChangeCouplingVoter.Factory changeCouplingVoter = changeCouplingVoterArg.getValue();
				if (changeCouplingVoter != null) {
					control.addConfidenceVoter(changeCouplingVoter);
				}
			}
			
			final ArgumentSet<de.unisaarland.cs.st.moskito.untangling.voters.DataDependencyVoter.Factory, de.unisaarland.cs.st.moskito.untangling.voters.DataDependencyVoter.Options> dataDepVoterArg = getSettings().getArgumentSet(this.dataDependencyVoterOptions);
			if (dataDepVoterArg != null) {
				final DataDependencyVoter.Factory dataDependencyVoter = dataDepVoterArg.getValue();
				if (dataDependencyVoter != null) {
					control.addConfidenceVoter(dataDependencyVoter);
				}
			}
			
			final ArgumentSet<Factory, de.unisaarland.cs.st.moskito.untangling.voters.TestImpactVoter.Options> testImpactVoterArg = getSettings().getArgumentSet(this.testImpactVoterOptions);
			if (testImpactVoterArg != null) {
				final TestImpactVoter.Factory testImpactVoter = testImpactVoterArg.getValue();
				if (testImpactVoter != null) {
					control.addConfidenceVoter(testImpactVoter);
				}
			}
			
			final LongArgument seedArg = getSettings().getArgument(this.seedOptions);
			if (seedArg != null) {
				final Long seed = seedArg.getValue();
				control.setSeed(seed);
			}
			
			final ListArgument atomicChangesArg = getSettings().getArgument(this.atomicChangesOptions);
			if (atomicChangesArg != null) {
				final List<String> atomicTransactionIds = atomicChangesArg.getValue();
				control.setAtomicTransactionIds(atomicTransactionIds);
			}
			
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			control.setPersistenceUtil(persistenceUtil);
			
			final Long blobWindowSize = getSettings().getArgument(this.blobWindowSizeOptions).getValue();
			control.setBlobWindowSize(blobWindowSize);
			
			final Long minBlobSize = getSettings().getArgument(this.minBlobSizeOptions).getValue();
			control.setMinBlobSize(minBlobSize);
			final Long maxBlobSize = getSettings().getArgument(this.maxBlobSizeOptions).getValue();
			control.setMaxBlobSize(maxBlobSize);
			
			final File outFile = getSettings().getArgument(this.outOptions).getValue();
			control.setOutputFile(outFile);
			
			final LongArgument nArg = getSettings().getArgument(this.nOptions);
			if (nArg != null) {
				final Long n = nArg.getValue();
				control.setN(n);
			}
			
			final UntanglingCollapse collapseMode = getSettings().getArgument(this.collapseModeOptions).getValue();
			control.setCollapseMode(collapseMode);
			
			final ScoreCombinationMode scoreMode = getSettings().getArgument(this.scoreModeOptions).getValue();
			control.setScoreMode(scoreMode);
			
			final Boolean dryRun = getSettings().getArgument(this.dryRunOptions).getValue();
			control.setDryRun(dryRun);
			
			return control;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.databaseOptions = new DatabaseOptions(getSettings().getRoot(), Requirement.required, "ppa");
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			this.repositoryOptions = new RepositoryOptions(getSettings().getRoot(), Requirement.required,
			                                               this.databaseOptions);
			map.put(this.repositoryOptions.getName(), this.repositoryOptions);
			
			this.blobWindowSizeOptions = new LongArgument.Options(
			                                                      set,
			                                                      "blobWindowSize",
			                                                      "Max number of days changes sets might be appart to be condifered for tangling. (-1 = unlimited)",
			                                                      14l, Requirement.required);
			map.put(this.blobWindowSizeOptions.getName(), this.blobWindowSizeOptions);
			
			this.minBlobSizeOptions = new LongArgument.Options(
			                                                   set,
			                                                   "minBlobsize",
			                                                   "The minimal number of transactions to be combined within a blob.",
			                                                   2l, Requirement.required);
			map.put(this.minBlobSizeOptions.getName(), this.minBlobSizeOptions);
			
			this.maxBlobSizeOptions = new LongArgument.Options(
			                                                   set,
			                                                   "maxBlobsize",
			                                                   "The maximal number of transactions to be combined within a blob. (-1 means not limit)",
			                                                   -1l, Requirement.required);
			map.put(this.maxBlobSizeOptions.getName(), this.maxBlobSizeOptions);
			
			this.outOptions = new OutputFileArgument.Options(set, "outFile",
			                                                 "Write descriptive statistics into this file", null,
			                                                 Requirement.required, true);
			map.put(this.outOptions.getName(), this.outOptions);
			
			this.dryRunOptions = new BooleanArgument.Options(
			                                                 set,
			                                                 "dryrun",
			                                                 "Setting this option means that the actual untangling will be skipped. This is for testing purposes only.",
			                                                 false, Requirement.optional);
			map.put(this.dryRunOptions.getName(), this.dryRunOptions);
			
			this.nOptions = new LongArgument.Options(set, "n", "Choose n random artificial blobs. (-1 = unlimited)",
			                                         -1l, Requirement.optional);
			map.put(this.nOptions.getName(), this.nOptions);
			
			this.seedOptions = new LongArgument.Options(set, "seed", "Use random seed.", null, Requirement.optional);
			map.put(this.seedOptions.getName(), this.seedOptions);
			
			this.collapseModeOptions = new EnumArgument.Options<UntanglingCollapse>(
			                                                                        set,
			                                                                        "collapse",
			                                                                        "Method to collapse when untangling.",
			                                                                        UntanglingCollapse.MAX,
			                                                                        Requirement.optional);
			map.put(this.collapseModeOptions.getName(), this.collapseModeOptions);
			
			this.scoreModeOptions = new EnumArgument.Options<ScoreCombinationMode>(
			                                                                       set,
			                                                                       "scoreMode",
			                                                                       "Method to combine single initial clustering matrix scores.",
			                                                                       ScoreCombinationMode.LINEAR_REGRESSION,
			                                                                       Requirement.optional);
			map.put(this.scoreModeOptions.getName(), this.scoreModeOptions);
			
			this.atomicChangesOptions = new ListArgument.Options(
			                                                     set,
			                                                     "atomicTransactions",
			                                                     "A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
			                                                     new ArrayList<String>(0), Requirement.optional);
			map.put(this.atomicChangesOptions.getName(), this.atomicChangesOptions);
			
			// / ####
			
			this.generatorStrategyOptions = new EnumArgument.Options<>(
			                                                           set,
			                                                           "generatorStrategy",
			                                                           "Strategy to construct artifical blobs.",
			                                                           ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy.PACKAGE,
			                                                           Requirement.required);
			if (this.generatorStrategyOptions.required()) {
				map.put(this.generatorStrategyOptions.getName(), this.generatorStrategyOptions);
			}
			
			this.changeCouplingCombineOptions = new ChangeCouplingCombineOperator.Options(
			                                                                              set,
			                                                                              Requirement.equals(this.generatorStrategyOptions,
			                                                                                                 ArtificialBlobGeneratorStrategy.COUPLINGS),
			                                                                              this.databaseOptions);
			if (this.changeCouplingCombineOptions.required()) {
				map.put(this.changeCouplingCombineOptions.getName(), this.changeCouplingCombineOptions);
			}
			
			this.consecutiveCombineOptions = new ConsecutiveChangeCombineOperator.Options(
			                                                                              set,
			                                                                              Requirement.equals(this.generatorStrategyOptions,
			                                                                                                 ArtificialBlobGeneratorStrategy.CONSECUTIVE));
			if (this.consecutiveCombineOptions.required()) {
				map.put(this.consecutiveCombineOptions.getName(), this.consecutiveCombineOptions);
			}
			
			this.packageDistanceCombineOptions = new PackageDistanceCombineOperator.Options(
			                                                                                set,
			                                                                                Requirement.equals(this.generatorStrategyOptions,
			                                                                                                   ArtificialBlobGeneratorStrategy.PACKAGE));
			if (this.packageDistanceCombineOptions.required()) {
				map.put(this.packageDistanceCombineOptions.getName(), this.packageDistanceCombineOptions);
			}
			
			this.useCallGraphOptions = new BooleanArgument.Options(set, "voteCallgraph",
			                                                       "Use call graph voter when untangling", true,
			                                                       Requirement.required);
			map.put(this.useCallGraphOptions.getName(), this.useCallGraphOptions);
			
			this.callGraphVoterOptions = new CallGraphVoter.Options(set, Requirement.equals(this.useCallGraphOptions,
			                                                                                true),
			                                                        this.repositoryOptions);
			if (this.callGraphVoterOptions.required()) {
				map.put(this.callGraphVoterOptions.getName(), this.callGraphVoterOptions);
			}
			
			this.useChangeCouplingsOptions = new BooleanArgument.Options(set, "voteChangecouplings",
			                                                             "Use change coupling voter when untangling",
			                                                             true, Requirement.required);
			map.put(this.useChangeCouplingsOptions.getName(), this.useChangeCouplingsOptions);
			
			this.changeCouplingVoterOptions = new ChangeCouplingVoter.Options(
			                                                                  set,
			                                                                  Requirement.equals(this.useChangeCouplingsOptions,
			                                                                                     true),
			                                                                  this.databaseOptions);
			if (this.changeCouplingVoterOptions.required()) {
				map.put(this.changeCouplingVoterOptions.getName(), this.changeCouplingVoterOptions);
			}
			
			this.useDataDependenciesOptions = new BooleanArgument.Options(set, "voteDatadependencies",
			                                                              "Use data dependency voter when untangling",
			                                                              true, Requirement.required);
			map.put(this.useDataDependenciesOptions.getName(), this.useDataDependenciesOptions);
			
			this.dataDependencyVoterOptions = new DataDependencyVoter.Options(
			                                                                  set,
			                                                                  Requirement.equals(this.useDataDependenciesOptions,
			                                                                                     true),
			                                                                  this.repositoryOptions);
			if (this.dataDependencyVoterOptions.required()) {
				map.put(this.dataDependencyVoterOptions.getName(), this.dataDependencyVoterOptions);
			}
			
			this.useTestImpactOptions = new BooleanArgument.Options(set, "voteTestimpact",
			                                                        "Use test coverage information", false,
			                                                        Requirement.required);
			map.put(this.useTestImpactOptions.getName(), this.useTestImpactOptions);
			
			this.testImpactVoterOptions = new TestImpactVoter.Options(set,
			                                                          Requirement.equals(this.useTestImpactOptions,
			                                                                             true), this.repositoryOptions);
			if (this.testImpactVoterOptions.required()) {
				map.put(this.testImpactVoterOptions.getName(), this.testImpactVoterOptions);
			}
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}
