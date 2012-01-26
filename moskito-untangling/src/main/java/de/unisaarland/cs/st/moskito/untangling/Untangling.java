/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.InputFileArgument;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.uncommons.maths.combinatorics.PermutationGenerator;

import serp.util.Strings;
import de.unisaarland.cs.st.moskito.clustering.AvgCollapseVisitor;
import de.unisaarland.cs.st.moskito.clustering.MaxCollapseVisitor;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.clustering.ScoreAggregation;
import de.unisaarland.cs.st.moskito.clustering.SumAggregation;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositoryArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;
import de.unisaarland.cs.st.moskito.untangling.aggregation.LinearRegressionAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.SVMAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.VarSumAggregation;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlob;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.moskito.untangling.blob.AtomicTransaction;
import de.unisaarland.cs.st.moskito.untangling.voters.CallGraphVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.DataDependencyVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.FileChangeCouplingVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.FileDistanceVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.LineDistanceVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.TestImpactVoter;

/**
 * The Class Untangling.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Untangling {
	
	public enum ScoreCombinationMode {
		
		SUM, VARSUM, LINEAR_REGRESSION, SVM;
		
		public static String[] stringValues() {
			Set<String> values = new HashSet<String>();
			for (ScoreCombinationMode g : ScoreCombinationMode.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	public enum UntanglingCollapse {
		AVG, MAX, RATIO;
		
		public static String[] stringValues() {
			Set<String> values = new HashSet<String>();
			for (UntanglingCollapse g : UntanglingCollapse.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	public static Random                          random          = new Random();
	
	/**
	 * Untangle.̋
	 * 
	 * @param blob
	 *            the blob
	 * @param numClusters
	 *            the num clusters
	 * @param scoreVisitors2
	 * @param scoreVisitors
	 *            the score visitors
	 * @param collapseVisitor
	 *            the collapse visitor
	 * @return the sets the
	 */
	
	public long                                   seed;
	
	/** The repository arg. */
	private final RepositoryArguments             repositoryArg;
	
	/** The callgraph arg. */
	private final DirectoryArgument               callgraphArg;
	
	/** The blob arg. */
	private final ListArgument                    atomicChangesArg;
	
	/** The use call graph. */
	private final BooleanArgument                 useCallGraph;
	
	/** The database args. */
	private final DatabaseArguments               databaseArgs;
	
	/** The use change couplings. */
	private final BooleanArgument                 useChangeCouplings;
	
	/** The change couplings min support. */
	private final LongArgument                    changeCouplingsMinSupport;
	
	/** The change couplings min confidence. */
	private final DoubleArgument                  changeCouplingsMinConfidence;
	
	/** The package distance arg. */
	private final LongArgument                    packageDistanceArg;
	
	/** The min blob size arg. */
	private final LongArgument                    minBlobSizeArg;
	
	/** The max blob size arg. */
	private final LongArgument                    maxBlobSizeArg;
	
	/** The out arg. */
	private final OutputFileArgument              outArg;
	
	/** The call graph cache dir arg. */
	private final DirectoryArgument               callGraphCacheDirArg;
	
	/** The dry run arg. */
	private final BooleanArgument                 dryRunArg;
	
	/** The use data dependencies. */
	private final BooleanArgument                 useDataDependencies;
	
	/** The datadep arg. */
	private final DirectoryArgument               datadepArg;
	
	/** The n arg. */
	private final LongArgument                    nArg;
	
	private final BooleanArgument                 useTestImpact;
	
	private final InputFileArgument               testImpactFileArg;
	
	private final LongArgument                    timeArg;
	
	private final EnumArgument                    collapseArg;
	
	private final EnumArgument                    scoreModeArg;
	
	private ScoreAggregation<JavaChangeOperation> aggregator      = null;
	
	private TestImpactVoter                       testImpactVoter = null;
	
	private final Repository                      repository;
	
	private final boolean                         dryrun;
	
	private PersistenceUtil                       persistenceUtil;
	
	private final DirectoryArgument               changeCouplingsCacheDirArg;
	
	private final DirectoryArgument               dataDependencyCacheDirArg;
	
	/**
	 * Instantiates a new untangling.
	 */
	public Untangling() {
		RepositorySettings settings = new RepositorySettings();
		
		this.repositoryArg = settings.setRepositoryArg(true);
		this.databaseArgs = settings.setDatabaseArgs(true, "untangling");
		settings.setLoggerArg(false);
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
		
		LongArgument seedArg = new LongArgument(settings, "seed", "Use random seed.", null, false);
		
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
		
		settings.parseArguments();
		if (seedArg.getValue() != null) {
			this.seed = seedArg.getValue();
		} else {
			this.seed = random.nextLong();
		}
		random.setSeed(this.seed);
		this.repository = this.repositoryArg.getValue();
		this.dryrun = this.dryRunArg.getValue();
		
		this.persistenceUtil = this.databaseArgs.getValue();

	}
	
	/**
	 * Compare partitions.
	 * 
	 * @param blob
	 *            the blob
	 * @param partitions
	 *            the partitions
	 * @return the int
	 */
	private int comparePartitions(final ArtificialBlob blob,
			final Set<Set<JavaChangeOperation>> partitions) {
		
		Condition.check(blob.getTransactions().size() == partitions.size(),
				"The size of partitions in artificial blob and the size of untangled partitions must be equal.");
		
		List<List<JavaChangeOperation>> originalPartitions = blob.getChangeOperationPartitions();
		
		PermutationGenerator<Set<JavaChangeOperation>> pGen = new PermutationGenerator<Set<JavaChangeOperation>>(
				partitions);
		
		int minDiff = Integer.MAX_VALUE;
		
		while (pGen.hasMore()) {
			List<Set<JavaChangeOperation>> nextPermutation = pGen.nextPermutationAsList();
			int diff = 0;
			for (int i = 0; i < nextPermutation.size(); ++i) {
				Set<JavaChangeOperation> untangledPart = nextPermutation.get(i);
				List<JavaChangeOperation> originalPart = originalPartitions.get(i);
				diff += CollectionUtils.subtract(originalPart, untangledPart).size();
			}
			if (diff < minDiff) {
				minDiff = diff;
			}
		}
		return minDiff;
	}
	
	public List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> generateScoreVisitors(final RCSTransaction transaction) {
		
		if ((this.testImpactVoter == null) && (this.useTestImpact.getValue())) {
			File testCoverageIn = this.testImpactFileArg.getValue();
			if (testCoverageIn == null) {
				throw new UnrecoverableError("If you want to use a test coverage voter, please specify the argument: "
						+ this.testImpactFileArg.getName());
			}
			try {
				this.testImpactVoter = new TestImpactVoter(testCoverageIn);
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Error while creating TestCoverageVoter. Skipping this voter. More details see below.");
					Logger.error(e.getMessage(), e);
				}
			} catch (ClassNotFoundException e) {
				if (Logger.logError()) {
					Logger.error("Error while creating TestCoverageVoter. Skipping this voter. More details see below.");
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		List<String> eclipseArgs = new LinkedList<String>();
		eclipseArgs.add("-vmargs");
		eclipseArgs.add(" -Dppa");
		eclipseArgs.add(" -Drepository.uri=file://" + this.repositoryArg.getRepoDirArg().getValue().toString());
		if (this.repositoryArg.getPassArg().getValue() != null) {
			eclipseArgs.add(" -Drepository.password=" + this.repositoryArg.getPassArg().getValue());
		}
		eclipseArgs.add(" -Drepository.type=" + this.repositoryArg.getRepoTypeArg().getValue());
		if (this.repositoryArg.getUserArg().getValue() != null) {
			eclipseArgs.add(" -Drepository.user=" + this.repositoryArg.getUserArg().getValue());
		}
		
		List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
		scoreVisitors.add(new LineDistanceVoter());
		scoreVisitors.add(new FileDistanceVoter());
		// add call graph visitor
		if (this.useCallGraph.getValue()) {
			scoreVisitors.add(new CallGraphVoter(this.callgraphArg.getValue(),
					eclipseArgs.toArray(new String[eclipseArgs.size()]), transaction,
					this.callGraphCacheDirArg.getValue()));
		}
		
		// add change coupling visitor
		if (this.useChangeCouplings.getValue()) {
			if ((this.changeCouplingsMinConfidence.getValue() == null)
					|| (this.changeCouplingsMinSupport.getValue() == null)) {
				throw new UnrecoverableError(
						"When using change couplings, you have to specify a min support and min confidence value.");
			}
			
			File ccCacheDir = this.changeCouplingsCacheDirArg.getValue();
			scoreVisitors.add(new FileChangeCouplingVoter(transaction, this.changeCouplingsMinSupport.getValue()
					.intValue(),
					this.changeCouplingsMinConfidence.getValue().doubleValue(),
					this.persistenceUtil, ccCacheDir));
		}
		
		// add data dependency visitor
		if (this.useDataDependencies.getValue()) {
			File dataDepEclipseDir = this.datadepArg.getValue();
			if (dataDepEclipseDir == null) {
				throw new UnrecoverableError("When using data dependencies -D" + this.useDataDependencies.getName()
						+ " you must set the -D" + this.datadepArg.getName() + "!");
			}
			scoreVisitors.add(new DataDependencyVoter(dataDepEclipseDir, this.repository, transaction,
					this.dataDependencyCacheDirArg.getValue()));
		}
		
		// add test impact visitor
		if (this.testImpactVoter != null) {
			scoreVisitors.add(this.testImpactVoter);
		}
		
		return scoreVisitors;
		
	}
	
	public List<String> getScoreVisitorNames() {
		List<String> result = new LinkedList<String>();
		result.add(LineDistanceVoter.class.getSimpleName());
		result.add(FileDistanceVoter.class.getSimpleName());
		if (this.useCallGraph.getValue()) {
			result.add(CallGraphVoter.class.getSimpleName());
		}
		if (this.useChangeCouplings.getValue()) {
			result.add(FileChangeCouplingVoter.class.getSimpleName());
		}
		if (this.useDataDependencies.getValue()) {
			result.add(DataDependencyVoter.class.getSimpleName());
		}
		if (this.useTestImpact.getValue()) {
			result.add(TestImpactVoter.class.getSimpleName());
		}
		return result;
	}
	
	/**
	 * Run.
	 */
	public void run() {
		
		// load the atomic transactions and their change operations
		List<AtomicTransaction> transactions = new LinkedList<AtomicTransaction>();
		
		if (this.atomicChangesArg.getValue() != null) {
			HashSet<String> atomicTransactions = this.atomicChangesArg.getValue();
			for (String transactionId : atomicTransactions) {
				RCSTransaction t = this.persistenceUtil.loadById(transactionId, RCSTransaction.class);
				
				// FIXME this is required due to some unknown problem which
				// causes NullpointerExceptions becaus Fetch.LAZY returns null.
				t.getAuthor();
				t.toString();
				
				Collection<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, t);
				transactions.add(new AtomicTransaction(t, ops));
			}
		} else {
			Criteria<RCSTransaction> criteria = this.persistenceUtil.createCriteria(RCSTransaction.class).eq("atomic",
					true);
			List<RCSTransaction> atomicTransactions = this.persistenceUtil.load(criteria);
			for (RCSTransaction t : atomicTransactions) {
				
				// FIXME this is required due to some unknown problem which
				// causes NullpointerExceptions becaus Fetch.LAZY returns null.
				t.getAuthor();
				t.toString();
				
				Collection<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, t);
				Set<JavaChangeOperation> toRemove = new HashSet<JavaChangeOperation>();
				for (JavaChangeOperation op : ops) {
					if (!(op.getChangedElementLocation().getElement() instanceof JavaMethodDefinition)) {
						toRemove.add(op);
					}
				}
				ops.removeAll(toRemove);
				if (!ops.isEmpty()) {
					transactions.add(new AtomicTransaction(t, ops));
				}
			}
		}
		
		// build all artificial blobs. Combine all atomic transactions.
		List<ArtificialBlob> artificialBlobs = new LinkedList<ArtificialBlob>();
		
		if (transactions.size() > 50) {
			Set<AtomicTransaction> randomTransactions = new HashSet<AtomicTransaction>();
			for (int i = 0; i < 50; ++i) {
				random.nextInt(transactions.size());
				randomTransactions.add(transactions.get(i));
				transactions.remove(i);
			}
			transactions.clear();
			transactions.addAll(randomTransactions);
		}
		
		artificialBlobs.addAll(ArtificialBlobGenerator.generateAll(transactions, this.packageDistanceArg.getValue()
				.intValue(),
				this.minBlobSizeArg.getValue().intValue(),
				this.maxBlobSizeArg.getValue().intValue(),
				this.timeArg.getValue()));
		
		int blobSetSize = artificialBlobs.size();
		if (Logger.logInfo()) {
			Logger.info("Generated " + blobSetSize + " artificial blobs.");
		}
		
		if (System.getProperty("generateBlobsOnly") != null) {
			return;
		}
		
		File outFile = this.outArg.getValue();
		BufferedWriter outWriter;
		try {
			outWriter = new BufferedWriter(new FileWriter(outFile));
			outWriter.write("DiffSize,#ChangeOperations,relativeDiffSize,lowestScore");
			outWriter.append(FileUtils.lineSeparator);
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		if ((this.nArg.getValue() != -1l) && (this.nArg.getValue() < artificialBlobs.size())) {
			List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
			for (int i = 0; i < this.nArg.getValue(); ++i) {
				int r = random.nextInt(artificialBlobs.size());
				selectedArtificialBlobs.add(artificialBlobs.remove(r));
			}
			artificialBlobs = selectedArtificialBlobs;
			blobSetSize = artificialBlobs.size();
		}
		
		Set<RCSTransaction> usedTransactions = new HashSet<RCSTransaction>();
		
		MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor = null;
		UntanglingCollapse collapse = UntanglingCollapse.valueOf(this.collapseArg.getValue());
		switch (collapse) {
			case AVG:
				collapseVisitor = new AvgCollapseVisitor<JavaChangeOperation>();
				break;
			case RATIO:
				collapseVisitor = new AvgCollapseVisitor<JavaChangeOperation>();
				break;
			default:
				collapseVisitor = new MaxCollapseVisitor<JavaChangeOperation>();
				break;
		}
		
		// create the corresponding score aggregation model
		ScoreCombinationMode scoreAggregationMode = ScoreCombinationMode.valueOf(this.scoreModeArg.getValue());
		switch (scoreAggregationMode) {
			case SUM:
				this.aggregator = new SumAggregation<JavaChangeOperation>();
				break;
			case VARSUM:
				this.aggregator = new VarSumAggregation<JavaChangeOperation>();
				break;
			case LINEAR_REGRESSION:
				LinearRegressionAggregation linarRegressionAggregator = new LinearRegressionAggregation(this);
				// train score aggregation model
				Set<AtomicTransaction> trainTransactions = new HashSet<AtomicTransaction>();
				for (ArtificialBlob blob : artificialBlobs) {
					trainTransactions.addAll(blob.getAtomicTransactions());
				}
				linarRegressionAggregator.train(trainTransactions);
				this.aggregator = linarRegressionAggregator;
				break;
			case SVM:
				SVMAggregation svmAggregator = SVMAggregation.createInstance(this);
				// train score aggregation model
				Set<AtomicTransaction> svmTrainTransactions = new HashSet<AtomicTransaction>();
				for (ArtificialBlob blob : artificialBlobs) {
					svmTrainTransactions.addAll(blob.getAtomicTransactions());
				}
				svmAggregator.train(svmTrainTransactions);
				this.aggregator = svmAggregator;
				break;
			default:
				throw new UnrecoverableError("Unknown score aggregation mode found: " + this.scoreModeArg.getValue());
		}
		
		// for each artificial blob
		DescriptiveStatistics stat = new DescriptiveStatistics();
		DescriptiveStatistics relativeStat = new DescriptiveStatistics();
		int counter = 0;
		for (ArtificialBlob blob : artificialBlobs) {
			
			if (Logger.logInfo()) {
				Logger.info("Processing artificial blob: " + (++counter) + "/" + blobSetSize);
			}
			
			List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = this.generateScoreVisitors(blob.getLatestTransaction());
			
			// run the partitioning algorithm
			if (!this.dryrun) {
				
				MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(
						blob.getAllChangeOperations(),
						scoreVisitors,
						this.aggregator,
						collapseVisitor);
				
				Set<Set<JavaChangeOperation>> partitions = clustering.getPartitions(blob.size());
				
				// compare the true and the computed partitions and score the
				// similarity score in a descriptive statistic
				int diff = comparePartitions(blob, partitions);
				stat.addValue(diff);
				double relDiff = ((double) diff) / ((double) blob.getAllChangeOperations().size());
				relativeStat.addValue(relDiff);
				try {
					outWriter.append(String.valueOf(diff));
					outWriter.append(",");
					outWriter.append(String.valueOf(blob.getAllChangeOperations().size()));
					outWriter.append(",");
					outWriter.append(String.valueOf(relDiff));
					outWriter.append(",");
					outWriter.append(String.valueOf(clustering.getLowestScore()));
					outWriter.append(FileUtils.lineSeparator);
				} catch (IOException e) {
					throw new UnrecoverableError(e.getMessage(), e);
				}
			}
			usedTransactions.addAll(blob.getTransactions());
			
		}
		
		// report the descriptive statistics about the partition scores.
		try {
			outWriter.append("Avg. MissRate:," + stat.getMean());
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append("Med. MissRate:," + stat.getPercentile(50));
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append("Avg. relative MissRate:," + relativeStat.getMean());
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append("Med. relative MissRate:," + relativeStat.getPercentile(50));
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append("Used transactions:");
			for (RCSTransaction t : usedTransactions) {
				outWriter.append(t.getId());
				outWriter.append(",");
			}
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append("Used random seed: ");
			outWriter.append(String.valueOf(this.seed));
			outWriter.append("Aggregation-Model info:");
			outWriter.append(FileUtils.lineSeparator);
			outWriter.append(this.aggregator.getInfo());
			outWriter.append(FileUtils.lineSeparator);
			
			outWriter.close();
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
	
}
