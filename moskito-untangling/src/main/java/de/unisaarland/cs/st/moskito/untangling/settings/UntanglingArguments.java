/**
 * 
 */
package de.unisaarland.cs.st.moskito.untangling.settings;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.InputFileArgument;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;

import org.apache.commons.lang.StringUtils;

import serp.util.Strings;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;

/**
 * @author just
 * 
 */
public class UntanglingArguments extends AndamaArgumentSet<Boolean> {
	
	private final DirectoryArgument  callgraphArg;
	private final ListArgument       atomicChangesArg;
	private final BooleanArgument    useCallGraph;
	private final BooleanArgument    useChangeCouplings;
	private final BooleanArgument    useDataDependencies;
	private final BooleanArgument    useTestImpact;
	private final InputFileArgument  testImpactFileArg;
	private final DirectoryArgument  datadepArg;
	private final LongArgument       changeCouplingsMinSupport;
	private final DoubleArgument     changeCouplingsMinConfidence;
	private final LongArgument       packageDistanceArg;
	private final LongArgument       minBlobSizeArg;
	private final LongArgument       maxBlobSizeArg;
	private final OutputFileArgument outArg;
	private final DirectoryArgument  callGraphCacheDirArg;
	private final DirectoryArgument  changeCouplingsCacheDirArg;
	private final DirectoryArgument  dataDependencyCacheDirArg;
	private final BooleanArgument    dryRunArg;
	private final LongArgument       nArg;
	private final LongArgument       seedArg;
	private final EnumArgument       collapseArg;
	private final LongArgument       timeArg;
	private final EnumArgument       scoreModeArg;
	
	public UntanglingArguments(final UntanglingSettings settings, final boolean required) {
		this.callgraphArg = new DirectoryArgument(
		                                          settings,
		                                          "callgraph.eclipse",
		                                          "Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).",
		                                          null, true, false);
		
		this.atomicChangesArg = new ListArgument(
		                                         settings,
		                                         "atomic.transactions",
		                                         "A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
		                                         null, false);
		
		this.useCallGraph = new BooleanArgument(settings, "vote.callgraph", "Use call graph voter when untangling",
		                                        "true", false);
		
		this.useChangeCouplings = new BooleanArgument(settings, "vote.changecouplings",
		                                              "Use change coupling voter when untangling", "true", false);
		
		this.useDataDependencies = new BooleanArgument(settings, "vote.datadependency",
		                                               "Use data dependency voter when untangling", "true", false);
		
		this.useTestImpact = new BooleanArgument(settings, "vote.testimpact", "Use test coverage information", "true",
		                                         false);
		
		this.testImpactFileArg = new InputFileArgument(settings, "testimpact.in",
		                                               "File containing a serial version of a ImpactMatrix", null,
		                                               false);
		
		this.datadepArg = new DirectoryArgument(
		                                        settings,
		                                        "datadependency.eclipse",
		                                        "Home directory of the reposuite datadependency applcation (must contain ./eclipse executable).",
		                                        null, false, false);
		
		this.changeCouplingsMinSupport = new LongArgument(
		                                                  settings,
		                                                  "vote.changecouplings.minsupport",
		                                                  "Set the minimum support for used change couplings to this value",
		                                                  "3", false);
		this.changeCouplingsMinConfidence = new DoubleArgument(
		                                                       settings,
		                                                       "vote.changecouplings.minconfidence",
		                                                       "Set minimum confidence for used change couplings to this value",
		                                                       "0.7", false);
		
		this.packageDistanceArg = new LongArgument(
		                                           settings,
		                                           "package.distance",
		                                           "The maximal allowed distance between packages allowed when generating blobs.",
		                                           "0", true);
		
		this.minBlobSizeArg = new LongArgument(settings, "blobsize.min",
		                                       "The minimal number of transactions to be combined within a blob.", "2",
		                                       true);
		
		this.maxBlobSizeArg = new LongArgument(
		                                       settings,
		                                       "blobsize.max",
		                                       "The maximal number of transactions to be combined within a blob. (-1 means not limit)",
		                                       "-1", true);
		
		this.outArg = new OutputFileArgument(settings, "out.file", "Write descriptive statistics into this file", null,
		                                     true, true);
		
		this.callGraphCacheDirArg = new DirectoryArgument(
		                                                  settings,
		                                                  "callgraph.cache.dir",
		                                                  "Cache directory containing call graphs using the naming converntion <transactionId>.cg",
		                                                  null, false, false);
		
		this.changeCouplingsCacheDirArg = new DirectoryArgument(
		                                                        settings,
		                                                        "changecouplings.cache.dir",
		                                                        "Cache directory containing change coupling pre-computations using the naming converntion <transactionId>.cc",
		                                                        null, false, false);
		
		this.dataDependencyCacheDirArg = new DirectoryArgument(
		                                                       settings,
		                                                       "datadependency.cache.dir",
		                                                       "Cache directory containing datadepency pre-computations using the naming converntion <transactionId>.dd",
		                                                       null, false, false);
		
		this.dryRunArg = new BooleanArgument(
		                                     settings,
		                                     "dryrun",
		                                     "Setting this option means that the actual untangling will be skipped. This is for testing purposes only.",
		                                     "false", false);
		
		this.nArg = new LongArgument(settings, "n", "Choose n random artificial blobs. (-1 = unlimited)", "-1", false);
		
		this.seedArg = new LongArgument(settings, "seed", "Use random seed.", null, false);
		
		this.collapseArg = new EnumArgument(settings, "collapse",
		                                    "Method to collapse when untangling. Possible values "
		                                            + StringUtils.join(UntanglingCollapse.stringValues(), ","), "MAX",
		                                    false, UntanglingCollapse.stringValues());
		
		this.timeArg = new LongArgument(
		                                settings,
		                                "blobWindow",
		                                "Max number of days all transactions of an artificial blob can be apart. (-1 = unlimited)",
		                                "-1", false);
		
		this.scoreModeArg = new EnumArgument(settings, "scoreMode",
		                                     "Method to combine single initial clustering matrix scores. Possbile values: "
		                                             + Strings.join(ScoreCombinationMode.values(), ","),
		                                     ScoreCombinationMode.LINEAR_REGRESSION.toString(), false,
		                                     ScoreCombinationMode.stringValues());
	}
	
	public ListArgument getAtomicChangesArg() {
		return this.atomicChangesArg;
	}
	
	public DirectoryArgument getCallgraphArg() {
		return this.callgraphArg;
	}
	
	public DirectoryArgument getCallGraphCacheDirArg() {
		return this.callGraphCacheDirArg;
	}
	
	public DirectoryArgument getChangeCouplingsCacheDirArg() {
		return this.changeCouplingsCacheDirArg;
	}
	
	public DoubleArgument getChangeCouplingsMinConfidence() {
		return this.changeCouplingsMinConfidence;
	}
	
	public LongArgument getChangeCouplingsMinSupport() {
		return this.changeCouplingsMinSupport;
	}
	
	public EnumArgument getCollapseArg() {
		return this.collapseArg;
	}
	
	public DirectoryArgument getDatadepArg() {
		return this.datadepArg;
	}
	
	public DirectoryArgument getDataDependencyCacheDirArg() {
		return this.dataDependencyCacheDirArg;
	}
	
	public BooleanArgument getDryRunArg() {
		return this.dryRunArg;
	}
	
	public LongArgument getMaxBlobSizeArg() {
		return this.maxBlobSizeArg;
	}
	
	public LongArgument getMinBlobSizeArg() {
		return this.minBlobSizeArg;
	}
	
	public LongArgument getnArg() {
		return this.nArg;
	}
	
	public OutputFileArgument getOutArg() {
		return this.outArg;
	}
	
	public LongArgument getPackageDistanceArg() {
		return this.packageDistanceArg;
	}
	
	public EnumArgument getScoreModeArg() {
		return this.scoreModeArg;
	}
	
	public LongArgument getSeedArg() {
		return this.seedArg;
	}
	
	public InputFileArgument getTestImpactFileArg() {
		return this.testImpactFileArg;
	}
	
	public LongArgument getTimeArg() {
		return this.timeArg;
	}
	
	public BooleanArgument getUseCallGraph() {
		return this.useCallGraph;
	}
	
	public BooleanArgument getUseChangeCouplings() {
		return this.useChangeCouplings;
	}
	
	public BooleanArgument getUseDataDependencies() {
		return this.useDataDependencies;
	}
	
	public BooleanArgument getUseTestImpact() {
		return this.useTestImpact;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentSet#getValue()
	 */
	@Override
	protected Boolean getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
