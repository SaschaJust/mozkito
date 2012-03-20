/**
 * 
 */
package de.unisaarland.cs.st.moskito.untangling.settings;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.arguments.BooleanArgument;
import net.ownhero.dev.hiari.settings.arguments.DirectoryArgument;
import net.ownhero.dev.hiari.settings.arguments.DoubleArgument;
import net.ownhero.dev.hiari.settings.arguments.EnumArgument;
import net.ownhero.dev.hiari.settings.arguments.InputFileArgument;
import net.ownhero.dev.hiari.settings.arguments.ListArgument;
import net.ownhero.dev.hiari.settings.arguments.LongArgument;
import net.ownhero.dev.hiari.settings.arguments.OutputFileArgument;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UntanglingArguments extends ArgumentSet<Boolean> {
	
	private final DirectoryArgument                  callgraphArg;
	private final ListArgument                       atomicChangesArg;
	private final BooleanArgument                    useCallGraph;
	private final BooleanArgument                    useChangeCouplings;
	private final BooleanArgument                    useDataDependencies;
	private final BooleanArgument                    useTestImpact;
	private final InputFileArgument                  testImpactFileArg;
	private final DirectoryArgument                  datadepArg;
	private final LongArgument                       changeCouplingsMinSupport;
	private final DoubleArgument                     changeCouplingsMinConfidence;
	private final LongArgument                       packageDistanceArg;
	private final LongArgument                       minBlobSizeArg;
	private final LongArgument                       maxBlobSizeArg;
	private final OutputFileArgument                 outArg;
	private final DirectoryArgument                  callGraphCacheDirArg;
	private final DirectoryArgument                  changeCouplingsCacheDirArg;
	private final DirectoryArgument                  dataDependencyCacheDirArg;
	private final BooleanArgument                    dryRunArg;
	private final LongArgument                       nArg;
	private final LongArgument                       seedArg;
	private final EnumArgument<UntanglingCollapse>   collapseArg;
	private final LongArgument                       timeArg;
	private final EnumArgument<ScoreCombinationMode> scoreModeArg;
	
	public UntanglingArguments(final ArgumentSet<?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Untangling options.", requirement);
		
		this.callgraphArg = new DirectoryArgument(
		                                          argumentSet,
		                                          "callgraph.eclipse",
		                                          "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
		                                          null, Requirement.required, false);
		
		this.atomicChangesArg = new ListArgument(
		                                         argumentSet,
		                                         "atomic.transactions",
		                                         "A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
		                                         null, Requirement.optional);
		
		this.useCallGraph = new BooleanArgument(argumentSet, "vote.callgraph", "Use call graph voter when untangling",
		                                        "true", Requirement.optional);
		
		this.useChangeCouplings = new BooleanArgument(argumentSet, "vote.changecouplings",
		                                              "Use change coupling voter when untangling", "true",
		                                              Requirement.optional);
		
		this.useDataDependencies = new BooleanArgument(argumentSet, "vote.datadependency",
		                                               "Use data dependency voter when untangling", "true",
		                                               Requirement.optional);
		
		this.useTestImpact = new BooleanArgument(argumentSet, "vote.testimpact", "Use test coverage information",
		                                         "true", Requirement.optional);
		
		this.testImpactFileArg = new InputFileArgument(argumentSet, "testimpact.in",
		                                               "File containing a serial version of a ImpactMatrix", null,
		                                               Requirement.optional);
		
		this.datadepArg = new DirectoryArgument(
		                                        argumentSet,
		                                        "datadependency.eclipse",
		                                        "Home directory of the reposuite datadependency applcation (must contain ./eclipse executable).",
		                                        null, Requirement.optional, false);
		
		this.changeCouplingsMinSupport = new LongArgument(
		                                                  argumentSet,
		                                                  "vote.changecouplings.minsupport",
		                                                  "Set the minimum support for used change couplings to this value",
		                                                  "3", Requirement.optional);
		this.changeCouplingsMinConfidence = new DoubleArgument(
		                                                       argumentSet,
		                                                       "vote.changecouplings.minconfidence",
		                                                       "Set minimum confidence for used change couplings to this value",
		                                                       "0.7", Requirement.optional);
		
		this.packageDistanceArg = new LongArgument(
		                                           argumentSet,
		                                           "package.distance",
		                                           "The maximal allowed distance between packages allowed when generating blobs.",
		                                           "0", Requirement.required);
		
		this.minBlobSizeArg = new LongArgument(argumentSet, "blobsize.min",
		                                       "The minimal number of transactions to be combined within a blob.", "2",
		                                       Requirement.required);
		
		this.maxBlobSizeArg = new LongArgument(
		                                       argumentSet,
		                                       "blobsize.max",
		                                       "The maximal number of transactions to be combined within a blob. (-1 means not limit)",
		                                       "-1", Requirement.required);
		
		this.outArg = new OutputFileArgument(argumentSet, "out.file", "Write descriptive statistics into this file",
		                                     null, Requirement.required, true);
		
		this.callGraphCacheDirArg = new DirectoryArgument(
		                                                  argumentSet,
		                                                  "callgraph.cache.dir",
		                                                  "Cache directory containing call graphs using the naming converntion <transactionId>.cg",
		                                                  null, Requirement.optional, false);
		
		this.changeCouplingsCacheDirArg = new DirectoryArgument(
		                                                        argumentSet,
		                                                        "changecouplings.cache.dir",
		                                                        "Cache directory containing change coupling pre-computations using the naming converntion <transactionId>.cc",
		                                                        null, Requirement.optional, false);
		
		this.dataDependencyCacheDirArg = new DirectoryArgument(
		                                                       argumentSet,
		                                                       "datadependency.cache.dir",
		                                                       "Cache directory containing datadepency pre-computations using the naming converntion <transactionId>.dd",
		                                                       null, Requirement.optional, false);
		
		this.dryRunArg = new BooleanArgument(
		                                     argumentSet,
		                                     "dryrun",
		                                     "Setting this option means that the actual untangling will be skipped. This is for testing purposes only.",
		                                     "false", Requirement.optional);
		
		this.nArg = new LongArgument(argumentSet, "n", "Choose n random artificial blobs. (-1 = unlimited)", "-1",
		                             Requirement.optional);
		
		this.seedArg = new LongArgument(argumentSet, "seed", "Use random seed.", null, Requirement.optional);
		
		this.collapseArg = new EnumArgument<UntanglingCollapse>(argumentSet, "collapse",
		                                                        "Method to collapse when untangling.",
		                                                        UntanglingCollapse.MAX, Requirement.optional);
		
		this.timeArg = new LongArgument(
		                                argumentSet,
		                                "blobWindow",
		                                "Max number of days all transactions of an artificial blob can be apart. (-1 = unlimited)",
		                                "-1", Requirement.optional);
		
		this.scoreModeArg = new EnumArgument<ScoreCombinationMode>(
		                                                           argumentSet,
		                                                           "scoreMode",
		                                                           "Method to combine single initial clustering matrix scores.",
		                                                           ScoreCombinationMode.LINEAR_REGRESSION,
		                                                           Requirement.optional);
	}
	
	/**
	 * @return
	 */
	public ListArgument getAtomicChangesArg() {
		return this.atomicChangesArg;
	}
	
	/**
	 * @return
	 */
	public DirectoryArgument getCallgraphArg() {
		return this.callgraphArg;
	}
	
	/**
	 * @return
	 */
	public DirectoryArgument getCallGraphCacheDirArg() {
		return this.callGraphCacheDirArg;
	}
	
	/**
	 * @return
	 */
	public DirectoryArgument getChangeCouplingsCacheDirArg() {
		return this.changeCouplingsCacheDirArg;
	}
	
	/**
	 * @return
	 */
	public DoubleArgument getChangeCouplingsMinConfidence() {
		return this.changeCouplingsMinConfidence;
	}
	
	/**
	 * @return
	 */
	public LongArgument getChangeCouplingsMinSupport() {
		return this.changeCouplingsMinSupport;
	}
	
	/**
	 * @return
	 */
	public EnumArgument<UntanglingCollapse> getCollapseArg() {
		return this.collapseArg;
	}
	
	/**
	 * @return
	 */
	public DirectoryArgument getDatadepArg() {
		return this.datadepArg;
	}
	
	/**
	 * @return
	 */
	public DirectoryArgument getDataDependencyCacheDirArg() {
		return this.dataDependencyCacheDirArg;
	}
	
	/**
	 * @return
	 */
	public BooleanArgument getDryRunArg() {
		return this.dryRunArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getMaxBlobSizeArg() {
		return this.maxBlobSizeArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getMinBlobSizeArg() {
		return this.minBlobSizeArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getnArg() {
		return this.nArg;
	}
	
	/**
	 * @return
	 */
	public OutputFileArgument getOutArg() {
		return this.outArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getPackageDistanceArg() {
		return this.packageDistanceArg;
	}
	
	/**
	 * @return
	 */
	public EnumArgument<ScoreCombinationMode> getScoreModeArg() {
		return this.scoreModeArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getSeedArg() {
		return this.seedArg;
	}
	
	/**
	 * @return
	 */
	public InputFileArgument getTestImpactFileArg() {
		return this.testImpactFileArg;
	}
	
	/**
	 * @return
	 */
	public LongArgument getTimeArg() {
		return this.timeArg;
	}
	
	/**
	 * @return
	 */
	public BooleanArgument getUseCallGraph() {
		return this.useCallGraph;
	}
	
	/**
	 * @return
	 */
	public BooleanArgument getUseChangeCouplings() {
		return this.useChangeCouplings;
	}
	
	/**
	 * @return
	 */
	public BooleanArgument getUseDataDependencies() {
		return this.useDataDependencies;
	}
	
	/**
	 * @return
	 */
	public BooleanArgument getUseTestImpact() {
		return this.useTestImpact;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		boolean ret = false;
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						setCachedValue(true);
						ret = true;
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			
			return ret;
		} finally {
			if (ret) {
				Condition.check(isInitialized(), "If init() returns true, the %s has to be set to initialized.",
				                getHandle());
			}
		}
	}
	
}
