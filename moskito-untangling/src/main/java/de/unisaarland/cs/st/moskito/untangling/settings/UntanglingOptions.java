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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;

/**
 * The Class UntanglingOptions.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UntanglingOptions extends
        ArgumentSetOptions<UntanglingControl, ArgumentSet<UntanglingControl, UntanglingOptions>> {
	
	/** The repository options. */
	private final RepositoryOptions                    repositoryOptions;
	
	/** The callgraph eclipse options. */
	private DirectoryArgument.Options                  callgraphEclipseOptions;
	
	/** The atomic changes options. */
	private ListArgument.Options                       atomicChangesOptions;
	
	/** The use call graph options. */
	private BooleanArgument.Options                    useCallGraphOptions;
	
	/** The use change couplings options. */
	private BooleanArgument.Options                    useChangeCouplingsOptions;
	
	/** The use data dependencies options. */
	private BooleanArgument.Options                    useDataDependenciesOptions;
	
	/** The use test impact options. */
	private BooleanArgument.Options                    useTestImpactOptions;
	
	/** The test impact file options. */
	private InputFileArgument.Options                  testImpactFileOptions;
	
	/** The datadependency eclipse options. */
	private DirectoryArgument.Options                  datadependencyEclipseOptions;
	
	/** The change couplings min support. */
	private LongArgument.Options                       changeCouplingsMinSupport;
	
	/** The change couplings min confidence. */
	private DoubleArgument.Options                     changeCouplingsMinConfidence;
	
	/** The package distance options. */
	private LongArgument.Options                       packageDistanceOptions;
	
	/** The min blob size options. */
	private LongArgument.Options                       minBlobSizeOptions;
	
	/** The max blob size options. */
	private LongArgument.Options                       maxBlobSizeOptions;
	
	/** The out options. */
	private OutputFileArgument.Options                 outOptions;
	
	/** The call graph cache dir options. */
	private DirectoryArgument.Options                  callGraphCacheDirOptions;
	
	/** The change couplings cache dir options. */
	private DirectoryArgument.Options                  changeCouplingsCacheDirOptions;
	
	/** The data dependency cache dir options. */
	private DirectoryArgument.Options                  dataDependencyCacheDirOptions;
	
	/** The dry run options. */
	private BooleanArgument.Options                    dryRunOptions;
	
	/** The n options. */
	private LongArgument.Options                       nOptions;
	
	/** The seed options. */
	private LongArgument.Options                       seedOptions;
	
	/** The collapse mode options. */
	private EnumArgument.Options<UntanglingCollapse>   collapseModeOptions;
	
	/** The blob window size options. */
	private LongArgument.Options                       blobWindowSizeOptions;
	
	/** The score mode options. */
	private EnumArgument.Options<ScoreCombinationMode> scoreModeOptions;
	
	/**
	 * Instantiates a new untangling options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param repositoryOptions
	 *            the repository options
	 */
	public UntanglingOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final RepositoryOptions repositoryOptions) {
		super(argumentSet, "untangling", "Options to configure the untangling process details.", requirements);
		this.repositoryOptions = repositoryOptions;
		
	}
	
	/**
	 * Call graph requirements.
	 * 
	 * @param set
	 *            the set
	 * @return the map<? extends string,? extends i options<?,?>>
	 */
	private Map<? extends String, ? extends IOptions<?, ?>> callGraphRequirements(final ArgumentSet<?, ?> set) {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			this.useCallGraphOptions = new BooleanArgument.Options(set, "voteCallgraph",
			                                                       "Use call graph voter when untangling", true,
			                                                       Requirement.required);
			map.put(this.useCallGraphOptions.getName(), this.useCallGraphOptions);
			
			this.callgraphEclipseOptions = new DirectoryArgument.Options(
			                                                             set,
			                                                             "callgraphEclipse",
			                                                             "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
			                                                             null,
			                                                             Requirement.iff(this.useCallGraphOptions),
			                                                             false);
			map.put(this.callgraphEclipseOptions.getName(), this.callgraphEclipseOptions);
			
			this.callGraphCacheDirOptions = new DirectoryArgument.Options(
			                                                              set,
			                                                              "callgraphCacheDir",
			                                                              "Cache directory containing call graphs using the naming converntion <transactionId>.cg",
			                                                              null,
			                                                              Requirement.iff(this.useCallGraphOptions),
			                                                              false);
			map.put(this.callGraphCacheDirOptions.getName(), this.callGraphCacheDirOptions);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Change coupling requirements.
	 * 
	 * @param set
	 *            the set
	 * @return the map
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	private Map<String, IOptions<?, ?>> changeCouplingRequirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                                           SettingsParseError {
		final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		this.useChangeCouplingsOptions = new BooleanArgument.Options(set, "voteChangecouplings",
		                                                             "Use change coupling voter when untangling", true,
		                                                             Requirement.required);
		map.put(this.useChangeCouplingsOptions.getName(), this.useChangeCouplingsOptions);
		
		this.changeCouplingsMinSupport = new LongArgument.Options(
		                                                          set,
		                                                          "changecouplingsMinSupport",
		                                                          "Set the minimum support for used change couplings to this value",
		                                                          3l, Requirement.iff(this.useChangeCouplingsOptions));
		map.put(this.changeCouplingsMinSupport.getName(), this.changeCouplingsMinSupport);
		
		this.changeCouplingsMinConfidence = new DoubleArgument.Options(
		                                                               set,
		                                                               "changecouplingsMinConfidence",
		                                                               "Set minimum confidence for used change couplings to this value",
		                                                               0.7d,
		                                                               Requirement.iff(this.useChangeCouplingsOptions));
		map.put(this.changeCouplingsMinConfidence.getName(), this.changeCouplingsMinConfidence);
		
		this.changeCouplingsCacheDirOptions = new DirectoryArgument.Options(
		                                                                    set,
		                                                                    "changecouplingsCacheDir",
		                                                                    "Cache directory containing change coupling pre-computations using the naming converntion <transactionId>.cc",
		                                                                    null,
		                                                                    Requirement.iff(this.useChangeCouplingsOptions),
		                                                                    false);
		map.put(this.changeCouplingsCacheDirOptions.getName(), this.changeCouplingsCacheDirOptions);
		return map;
	}
	
	/**
	 * Data dependency requirements.
	 * 
	 * @param set
	 *            the set
	 * @return the map<? extends string,? extends i options<?,?>>
	 */
	private Map<? extends String, ? extends IOptions<?, ?>> dataDependencyRequirements(final ArgumentSet<?, ?> set) {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			this.useDataDependenciesOptions = new BooleanArgument.Options(set, "voteDatadependency",
			                                                              "Use data dependency voter when untangling",
			                                                              true, Requirement.required);
			map.put(this.useDataDependenciesOptions.getName(), this.useDataDependenciesOptions);
			
			this.datadependencyEclipseOptions = new DirectoryArgument.Options(
			                                                                  set,
			                                                                  "datadependencyEclipse",
			                                                                  "Home directory of the reposuite datadependency applcation (must contain ./eclipse executable).",
			                                                                  null,
			                                                                  Requirement.iff(this.useDataDependenciesOptions),
			                                                                  false);
			map.put(this.datadependencyEclipseOptions.getName(), this.datadependencyEclipseOptions);
			this.dataDependencyCacheDirOptions = new DirectoryArgument.Options(
			                                                                   set,
			                                                                   "datadependencyCacheDir",
			                                                                   "Cache directory containing datadepency pre-computations using the naming converntion <transactionId>.dd",
			                                                                   null,
			                                                                   Requirement.iff(this.useDataDependenciesOptions),
			                                                                   false);
			map.put(this.dataDependencyCacheDirOptions.getName(), this.dataDependencyCacheDirOptions);
			return map;
		} finally {
			// POSTCONDITIONS
		}
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
			
			final ArgumentSet<Repository, RepositoryOptions> repositoryArgument = getSettings().getArgumentSet(this.repositoryOptions);
			final DirectoryArgument callGraphEclipseArgument = getSettings().getArgument(this.callgraphEclipseOptions);
			final ListArgument atomicChangesArgument = getSettings().getArgument(this.atomicChangesOptions);
			final BooleanArgument useCallGraphArgument = getSettings().getArgument(this.useCallGraphOptions);
			final BooleanArgument useChangeCouplingsArgument = getSettings().getArgument(this.useChangeCouplingsOptions);
			final BooleanArgument useDataDependenciesArgument = getSettings().getArgument(this.useDataDependenciesOptions);
			final BooleanArgument useTestImpactArgument = getSettings().getArgument(this.useTestImpactOptions);
			final InputFileArgument testImpactFileArgument = getSettings().getArgument(this.testImpactFileOptions);
			final DirectoryArgument dataDependencyEclipseArgument = getSettings().getArgument(this.datadependencyEclipseOptions);
			final LongArgument changeCouplingMinSupportArgument = getSettings().getArgument(this.changeCouplingsMinSupport);
			final DoubleArgument changeCouplingMinConfidenceArgument = getSettings().getArgument(this.changeCouplingsMinConfidence);
			final LongArgument packageDistanceArgument = getSettings().getArgument(this.packageDistanceOptions);
			final LongArgument minBlobSizeArgument = getSettings().getArgument(this.minBlobSizeOptions);
			final LongArgument maxBlobSizeArgument = getSettings().getArgument(this.maxBlobSizeOptions);
			final OutputFileArgument outArgument = getSettings().getArgument(this.outOptions);
			final DirectoryArgument callGraphCacheDirArgument = getSettings().getArgument(this.callGraphCacheDirOptions);
			final DirectoryArgument changeCouplingsCacheDirArgument = getSettings().getArgument(this.changeCouplingsCacheDirOptions);
			final DirectoryArgument dataDependencyCacheDirArgument = getSettings().getArgument(this.dataDependencyCacheDirOptions);
			final BooleanArgument dryRunArgument = getSettings().getArgument(this.dryRunOptions);
			final LongArgument nArgument = getSettings().getArgument(this.nOptions);
			final LongArgument seedArgument = getSettings().getArgument(this.seedOptions);
			final EnumArgument<UntanglingCollapse> collapseArgument = getSettings().getArgument(this.collapseModeOptions);
			final LongArgument blobWindowSizeArgument = getSettings().getArgument(this.blobWindowSizeOptions);
			final EnumArgument<ScoreCombinationMode> scoreModeArgument = getSettings().getArgument(this.scoreModeOptions);
			
			control.setRepository(repositoryArgument.getValue());
			control.setCallGraphEclipseDir(callGraphEclipseArgument.getValue());
			control.setAtomicChanges(atomicChangesArgument.getValue());
			control.enableCallGraph(useCallGraphArgument.getValue());
			control.enableChangeCouplings(useChangeCouplingsArgument.getValue());
			control.enableDataDependencies(useDataDependenciesArgument.getValue());
			control.enableTestImpact(useTestImpactArgument.getValue());
			control.setTestImpactFile(testImpactFileArgument.getValue());
			control.setDataDependencyEclipseDir(dataDependencyEclipseArgument.getValue());
			control.setChangeCouplingMinSupport(changeCouplingMinSupportArgument.getValue());
			control.setChangeCouplingMinConfidence(changeCouplingMinConfidenceArgument.getValue());
			control.setPackageDistance(packageDistanceArgument.getValue());
			control.setMinBlobSize(minBlobSizeArgument.getValue());
			control.setMaxBlobSize(maxBlobSizeArgument.getValue());
			control.setOutputFile(outArgument.getValue());
			control.setCallGraphCacheDir(callGraphCacheDirArgument.getValue());
			control.setChangeCouplingsCacheDir(changeCouplingsCacheDirArgument.getValue());
			control.setDataDependencyCacheDir(dataDependencyCacheDirArgument.getValue());
			control.setDryRun(dryRunArgument.getValue());
			control.setN(nArgument.getValue());
			control.setSeed(seedArgument.getValue());
			control.setCollapseMode(collapseArgument.getValue());
			control.setBlobWindowSize(blobWindowSizeArgument.getValue());
			control.setScoreMode(scoreModeArgument.getValue());
			
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
			
			map.put(this.repositoryOptions.getName(), this.repositoryOptions);
			
			map.putAll(callGraphRequirements(set));
			map.putAll(changeCouplingRequirements(set));
			map.putAll(dataDependencyRequirements(set));
			map.putAll(testImpactRequirements(set));
			
			this.atomicChangesOptions = new ListArgument.Options(
			                                                     set,
			                                                     "atomicTransactions",
			                                                     "A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
			                                                     new ArrayList<String>(0), Requirement.required);
			map.put(this.atomicChangesOptions.getName(), this.atomicChangesOptions);
			
			this.packageDistanceOptions = new LongArgument.Options(
			                                                       set,
			                                                       "packageDistance",
			                                                       "The maximal allowed distance between packages allowed when generating blobs.",
			                                                       0l, Requirement.required);
			map.put(this.packageDistanceOptions.getName(), this.packageDistanceOptions);
			
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
			
			this.blobWindowSizeOptions = new LongArgument.Options(
			                                                      set,
			                                                      "blobWindow",
			                                                      "Max number of days all transactions of an artificial blob can be apart. (-1 = unlimited)",
			                                                      -1l, Requirement.optional);
			map.put(this.blobWindowSizeOptions.getName(), this.blobWindowSizeOptions);
			
			this.scoreModeOptions = new EnumArgument.Options<ScoreCombinationMode>(
			                                                                       set,
			                                                                       "scoreMode",
			                                                                       "Method to combine single initial clustering matrix scores.",
			                                                                       ScoreCombinationMode.LINEAR_REGRESSION,
			                                                                       Requirement.optional);
			map.put(this.scoreModeOptions.getName(), this.scoreModeOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Test impact requirements.
	 * 
	 * @param set
	 *            the set
	 * @return the map<? extends string,? extends i options<?,?>>
	 */
	private Map<? extends String, ? extends IOptions<?, ?>> testImpactRequirements(final ArgumentSet<?, ?> set) {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			this.useTestImpactOptions = new BooleanArgument.Options(set, "voteTestimpact",
			                                                        "Use test coverage information", true,
			                                                        Requirement.required);
			map.put(this.useTestImpactOptions.getName(), this.useTestImpactOptions);
			
			this.testImpactFileOptions = new InputFileArgument.Options(
			                                                           set,
			                                                           "testimpactIn",
			                                                           "File containing a serial version of a ImpactMatrix",
			                                                           null, Requirement.iff(this.useTestImpactOptions));
			map.put(this.testImpactFileOptions.getName(), this.testImpactFileOptions);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
