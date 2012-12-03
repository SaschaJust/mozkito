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
package org.mozkito.untangling.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.LongArgument.Options;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.settings.RepositoryOptions;
import org.mozkito.untangling.Untangling.ScoreCombinationMode;
import org.mozkito.untangling.Untangling.UntanglingCollapse;
import org.mozkito.untangling.blob.ArtificialBlobGenerator;
import org.mozkito.untangling.blob.ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy;
import org.mozkito.untangling.blob.ChangeSet;
import org.mozkito.untangling.blob.combine.ChangeCouplingCombineOperator;
import org.mozkito.untangling.blob.combine.ConsecutiveChangeCombineOperator;
import org.mozkito.untangling.blob.combine.PackageDistanceCombineOperator;
import org.mozkito.untangling.voters.CallGraphVoter;
import org.mozkito.untangling.voters.ChangeCouplingVoter;
import org.mozkito.untangling.voters.DataDependencyVoter;
import org.mozkito.untangling.voters.TestImpactVoter;
import org.mozkito.untangling.voters.TestImpactVoter.Factory;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class UntanglingOptions.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UntanglingOptions extends
        ArgumentSetOptions<UntanglingControl, ArgumentSet<UntanglingControl, UntanglingOptions>> {
	
	/** The repository options. */
	private RepositoryOptions                                                             repositoryOptions;
	
	/** The atomic changes options. */
	private ListArgument.Options                                                          atomicChangesOptions;
	
	/** The use call graph options. */
	private BooleanArgument.Options                                                       useCallGraphOptions;
	
	/** The use change couplings options. */
	private BooleanArgument.Options                                                       useChangeCouplingsOptions;
	
	/** The use data dependencies options. */
	private BooleanArgument.Options                                                       useDataDependenciesOptions;
	
	/** The use test impact options. */
	private BooleanArgument.Options                                                       useTestImpactOptions;
	
	/** The min blob size options. */
	private LongArgument.Options                                                          minBlobSizeOptions;
	
	/** The max blob size options. */
	private LongArgument.Options                                                          maxBlobSizeOptions;
	
	/** The out options. */
	private OutputFileArgument.Options                                                    outOptions;
	
	/** The dry run options. */
	private BooleanArgument.Options                                                       dryRunOptions;
	
	/** The n options. */
	private LongArgument.Options                                                          nOptions;
	
	/** The seed options. */
	private LongArgument.Options                                                          seedOptions;
	
	/** The collapse mode options. */
	private EnumArgument.Options<UntanglingCollapse>                                      collapseModeOptions;
	
	/** The score mode options. */
	private EnumArgument.Options<ScoreCombinationMode>                                    scoreModeOptions;
	
	/** The generator strategy options. */
	private EnumArgument.Options<ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy> generatorStrategyOptions;
	
	/** The database options. */
	private DatabaseOptions                                                               databaseOptions;
	
	/** The change coupling combine options. */
	private org.mozkito.untangling.blob.combine.ChangeCouplingCombineOperator.Options     changeCouplingCombineOptions;
	
	/** The consecutive combine options. */
	private org.mozkito.untangling.blob.combine.ConsecutiveChangeCombineOperator.Options  consecutiveCombineOptions;
	
	/** The blob window size options. */
	private Options                                                                       blobWindowSizeOptions;
	
	/** The package distance combine options. */
	private org.mozkito.untangling.blob.combine.PackageDistanceCombineOperator.Options    packageDistanceCombineOptions;
	
	/** The call graph voter options. */
	private org.mozkito.untangling.voters.CallGraphVoter.Options                          callGraphVoterOptions;
	
	/** The change coupling voter options. */
	private org.mozkito.untangling.voters.ChangeCouplingVoter.Options                     changeCouplingVoterOptions;
	
	/** The data dependency voter options. */
	private org.mozkito.untangling.voters.DataDependencyVoter.Options                     dataDependencyVoterOptions;
	
	/** The test impact voter options. */
	private org.mozkito.untangling.voters.TestImpactVoter.Options                         testImpactVoterOptions;
	
	/** The artificial blob cache options. */
	private net.ownhero.dev.hiari.settings.DirectoryArgument.Options                      artificialBlobCacheOptions;
	
	/** The precision experiment options. */
	private net.ownhero.dev.hiari.settings.BooleanArgument.Options                        precisionExperimentOptions;
	
	/** The to untangle options. */
	private net.ownhero.dev.hiari.settings.InputFileArgument.Options                      toUntangleOptions;
	
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
	
	/**
	 * Adds the option.
	 * 
	 * @param options
	 *            the options
	 * @param map
	 *            the map
	 */
	private void addOption(final IOptions<?, ?> options,
	                       final Map<String, IOptions<?, ?>> map) {
		map.put(options.getName(), options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public UntanglingControl init() {
		// PRECONDITIONS
		
		try {
			
			final Boolean precisionExperiment = getSettings().getArgument(this.precisionExperimentOptions).getValue();
			
			final UntanglingControl control = new UntanglingControl(precisionExperiment);
			
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			control.setPersistenceUtil(persistenceUtil);
			
			if (precisionExperiment) {
				final ArtificialBlobGeneratorStrategy strategy = getSettings().getArgument(this.generatorStrategyOptions)
				                                                              .getValue();
				switch (strategy) {
					case CONSECUTIVE:
						control.setCombineOperator(getSettings().getArgumentSet(this.consecutiveCombineOptions)
						                                        .getValue());
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
				
				final LongArgument seedArg = getSettings().getArgument(this.seedOptions);
				if (seedArg != null) {
					final Long seed = seedArg.getValue();
					control.setSeed(seed);
				}
				
				final Long blobWindowSize = getSettings().getArgument(this.blobWindowSizeOptions).getValue();
				control.setBlobWindowSize(blobWindowSize);
				
				final Long minBlobSize = getSettings().getArgument(this.minBlobSizeOptions).getValue();
				control.setMinBlobSize(minBlobSize);
				final Long maxBlobSize = getSettings().getArgument(this.maxBlobSizeOptions).getValue();
				control.setMaxBlobSize(maxBlobSize);
				
				final LongArgument nArg = getSettings().getArgument(this.nOptions);
				if (nArg != null) {
					final Long n = nArg.getValue();
					control.setN(n);
				}
				
				final Boolean dryRun = getSettings().getArgument(this.dryRunOptions).getValue();
				control.setDryRun(dryRun);
				
				final File artificialBlobCache = getSettings().getArgument(this.artificialBlobCacheOptions).getValue();
				if (artificialBlobCache != null) {
					control.setArtificialBlobCacheDir(artificialBlobCache);
				}
			} // precision experiment END
			else {
				final File changeSetsToUntangle = getSettings().getArgument(this.toUntangleOptions).getValue();
				final Collection<UntangleInstruction> instructions = new LinkedList<>();
				try (BufferedReader reader = new BufferedReader(new FileReader(changeSetsToUntangle))) {
					String line = reader.readLine();
					if (line == null) {
						if (Logger.logError()) {
							Logger.error("Empty set of change sets to be untangled.");
						}
					}
					while ((line = reader.readLine()) != null) {
						final String[] lineParts = line.split(",");
						if (lineParts.length != 2) {
							if (Logger.logWarn()) {
								Logger.warn("Malformatted line in file containing change sets to be untangled. Ignoring line: %s",
								            line);
							}
						}
						final RCSTransaction rCSTransaction = persistenceUtil.loadById(lineParts[0],
						                                                               RCSTransaction.class);
						if (rCSTransaction == null) {
							if (Logger.logWarn()) {
								Logger.warn("Could not find change set with ID %s. Ignoring corresponding line in change set file.",
								            lineParts[0]);
							}
							continue;
						}
						instructions.add(new UntangleInstruction(
						                                         new ChangeSet(
						                                                       rCSTransaction,
						                                                       PPAPersistenceUtil.getChangeOperation(persistenceUtil,
						                                                                                             rCSTransaction)),
						                                         Double.valueOf(lineParts[1])));
					}
				} catch (NumberFormatException | IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
				control.setChangeSetsToUntangle(instructions);
			}
			
			final ArgumentSet<org.mozkito.untangling.voters.CallGraphVoter.Factory, org.mozkito.untangling.voters.CallGraphVoter.Options> callGraphVoterArg = getSettings().getArgumentSet(this.callGraphVoterOptions);
			if (callGraphVoterArg != null) {
				final CallGraphVoter.Factory callGraphVoter = callGraphVoterArg.getValue();
				if (callGraphVoter != null) {
					control.addConfidenceVoter(callGraphVoter);
				}
			}
			
			final ArgumentSet<org.mozkito.untangling.voters.ChangeCouplingVoter.Factory, org.mozkito.untangling.voters.ChangeCouplingVoter.Options> changeCouplingVoterArg = getSettings().getArgumentSet(this.changeCouplingVoterOptions);
			if (changeCouplingVoterArg != null) {
				final ChangeCouplingVoter.Factory changeCouplingVoter = changeCouplingVoterArg.getValue();
				if (changeCouplingVoter != null) {
					control.addConfidenceVoter(changeCouplingVoter);
				}
			}
			
			final ArgumentSet<org.mozkito.untangling.voters.DataDependencyVoter.Factory, org.mozkito.untangling.voters.DataDependencyVoter.Options> dataDepVoterArg = getSettings().getArgumentSet(this.dataDependencyVoterOptions);
			if (dataDepVoterArg != null) {
				final DataDependencyVoter.Factory dataDependencyVoter = dataDepVoterArg.getValue();
				if (dataDependencyVoter != null) {
					control.addConfidenceVoter(dataDependencyVoter);
				}
			}
			
			final ArgumentSet<Factory, org.mozkito.untangling.voters.TestImpactVoter.Options> testImpactVoterArg = getSettings().getArgumentSet(this.testImpactVoterOptions);
			if (testImpactVoterArg != null) {
				final TestImpactVoter.Factory testImpactVoter = testImpactVoterArg.getValue();
				if (testImpactVoter != null) {
					control.addConfidenceVoter(testImpactVoter);
				}
			}
			
			final ListArgument atomicChangesArg = getSettings().getArgument(this.atomicChangesOptions);
			if (atomicChangesArg != null) {
				final List<String> atomicTransactionIds = atomicChangesArg.getValue();
				control.setAtomicTransactionIds(atomicTransactionIds);
			}
			
			final File outFile = getSettings().getArgument(this.outOptions).getValue();
			control.setOutputFile(outFile);
			
			final UntanglingCollapse collapseMode = getSettings().getArgument(this.collapseModeOptions).getValue();
			control.setCollapseMode(collapseMode);
			
			final ScoreCombinationMode scoreMode = getSettings().getArgument(this.scoreModeOptions).getValue();
			control.setScoreMode(scoreMode);
			
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
			
			this.databaseOptions = new DatabaseOptions(getSettings().getRoot(), Requirement.required, "codeanalysis");
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			this.repositoryOptions = new RepositoryOptions(getSettings().getRoot(), Requirement.required,
			                                               this.databaseOptions);
			map.put(this.repositoryOptions.getName(), this.repositoryOptions);
			
			this.precisionExperimentOptions = new BooleanArgument.Options(
			                                                              set,
			                                                              "precisionExperiment",
			                                                              "Set to true if you want to measure the precision of the untangling algorithm. By default algorithm will be used to untangle given change sets.",
			                                                              false, Requirement.required);
			addOption(this.precisionExperimentOptions, map);
			
			this.blobWindowSizeOptions = new LongArgument.Options(
			                                                      set,
			                                                      "blobWindowSize",
			                                                      "Max number of days changes sets might be appart to be condifered for tangling. (-1 = unlimited)",
			                                                      14l,
			                                                      Requirement.equals(this.precisionExperimentOptions,
			                                                                         true));
			addOption(this.blobWindowSizeOptions, map);
			
			this.minBlobSizeOptions = new LongArgument.Options(
			                                                   set,
			                                                   "minBlobsize",
			                                                   "The minimal number of transactions to be combined within a blob.",
			                                                   2l, Requirement.equals(this.precisionExperimentOptions,
			                                                                          true));
			addOption(this.minBlobSizeOptions, map);
			
			this.maxBlobSizeOptions = new LongArgument.Options(
			                                                   set,
			                                                   "maxBlobsize",
			                                                   "The maximal number of transactions to be combined within a blob. (-1 means not limit)",
			                                                   -1l, Requirement.equals(this.precisionExperimentOptions,
			                                                                           true));
			addOption(this.maxBlobSizeOptions, map);
			
			this.dryRunOptions = new BooleanArgument.Options(
			                                                 set,
			                                                 "dryrun",
			                                                 "Setting this option means that the actual untangling will be skipped. This is for testing purposes only.",
			                                                 false, Requirement.equals(this.precisionExperimentOptions,
			                                                                           true));
			addOption(this.dryRunOptions, map);
			
			this.nOptions = new LongArgument.Options(set, "n", "Choose n random artificial blobs. (-1 = unlimited)",
			                                         -1l, Requirement.equals(this.precisionExperimentOptions, true));
			addOption(this.nOptions, map);
			
			this.seedOptions = new LongArgument.Options(set, "seed", "Use random seed.", null, Requirement.optional);
			addOption(this.seedOptions, map);
			
			this.generatorStrategyOptions = new EnumArgument.Options<>(
			                                                           set,
			                                                           "generatorStrategy",
			                                                           "Strategy to construct artifical blobs.",
			                                                           ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy.PACKAGE,
			                                                           Requirement.equals(this.precisionExperimentOptions,
			                                                                              true));
			addOption(this.generatorStrategyOptions, map);
			
			if (this.generatorStrategyOptions.required()) {
				this.artificialBlobCacheOptions = new DirectoryArgument.Options(
				                                                                set,
				                                                                "blobCacheDir",
				                                                                "Directory that will be used to cache artificial blobs for reuse. Caching strategies is handled internally using database settings and other relevant settings.",
				                                                                null, Requirement.optional, true);
				addOption(this.artificialBlobCacheOptions, map);
			}
			
			this.toUntangleOptions = new InputFileArgument.Options(
			                                                       set,
			                                                       "changeSets",
			                                                       "CSV file contain change sets to be untangled. Format: <change set id>,<num partitions>. If num partitions less than one, untangling algorithm will interpret value as merging threshold.",
			                                                       null,
			                                                       Requirement.equals(this.precisionExperimentOptions,
			                                                                          false));
			addOption(this.toUntangleOptions, map);
			
			this.outOptions = new OutputFileArgument.Options(
			                                                 set,
			                                                 "outFile",
			                                                 "Write output into this file: in case of precision experiment file will contain descriptive statistics. Otherwise file will contain untangled partition information.",
			                                                 null, Requirement.required, true);
			map.put(this.outOptions.getName(), this.outOptions);
			
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
			                                                                       ScoreCombinationMode.SVM,
			                                                                       Requirement.optional);
			map.put(this.scoreModeOptions.getName(), this.scoreModeOptions);
			
			this.atomicChangesOptions = new ListArgument.Options(
			                                                     set,
			                                                     "atomicTransactions",
			                                                     "A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
			                                                     new ArrayList<String>(0), Requirement.optional);
			map.put(this.atomicChangesOptions.getName(), this.atomicChangesOptions);
			
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
