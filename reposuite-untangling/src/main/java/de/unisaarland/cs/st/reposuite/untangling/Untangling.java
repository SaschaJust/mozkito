/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.untangling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.uncommons.maths.combinatorics.PermutationGenerator;

import de.unisaarland.cs.st.reposuite.clustering.AvgCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MaxCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.clustering.SumCollapseVisitor;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.BooleanArgument;
import de.unisaarland.cs.st.reposuite.settings.DatabaseArguments;
import de.unisaarland.cs.st.reposuite.settings.DirectoryArgument;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;
import de.unisaarland.cs.st.reposuite.settings.EnumArgument;
import de.unisaarland.cs.st.reposuite.settings.InputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.OutputFileArgument;
import de.unisaarland.cs.st.reposuite.settings.RepositoryArguments;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.untangling.blob.ArtificialBlob;
import de.unisaarland.cs.st.reposuite.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.reposuite.untangling.blob.BlobTransaction;
import de.unisaarland.cs.st.reposuite.untangling.voters.CallGraphVoter;
import de.unisaarland.cs.st.reposuite.untangling.voters.ChangeCouplingVoter;
import de.unisaarland.cs.st.reposuite.untangling.voters.DataDependencyVoter;
import de.unisaarland.cs.st.reposuite.untangling.voters.FileDistanceVoter;
import de.unisaarland.cs.st.reposuite.untangling.voters.TestImpactVoter;

/**
 * The Class Untangling.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Untangling {
	
	public enum UntanglingAggregate {
		AVG, MAX, SUM;
		
		public static String[] stringValues() {
			Set<String> values = new HashSet<String>();
			for (UntanglingAggregate g : UntanglingAggregate.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	/**
	 * Untangle.̋
	 * 
	 * @param blob
	 *            the blob
	 * @param numClusters
	 *            the num clusters
	 * @param scoreVisitors
	 *            the score visitors
	 * @param collapseVisitor
	 *            the collapse visitor
	 * @return the sets the
	 */
	@NoneNull
	public static Set<Set<JavaChangeOperation>> untangle(final ArtificialBlob blob, final int numClusters,
			final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors,
			final MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor) {
		@SuppressWarnings("unused") Set<Set<JavaChangeOperation>> result = new HashSet<Set<JavaChangeOperation>>();
		
		MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(
				blob.getAllChangeOperations(), scoreVisitors, collapseVisitor);
		
		return clustering.getPartitions(numClusters);
	}
	
	/** The repository arg. */
	private final RepositoryArguments                                   repositoryArg;
	
	/** The callgraph arg. */
	private final DirectoryArgument                                     callgraphArg;
	
	/** The blob arg. */
	private final ListArgument                                          atomicChangesArg;
	
	/** The score visitors. */
	private List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors;
	
	/** The use call graph. */
	private final BooleanArgument                                       useCallGraph;
	
	/** The database args. */
	private final DatabaseArguments                                     databaseArgs;
	
	/** The use change couplings. */
	private final BooleanArgument                                       useChangeCouplings;
	
	/** The change couplings min support. */
	private final LongArgument                                          changeCouplingsMinSupport;
	
	/** The change couplings min confidence. */
	private final DoubleArgument                                        changeCouplingsMinConfidence;
	
	/** The package distance arg. */
	private final LongArgument                                          packageDistanceArg;
	
	/** The min blob size arg. */
	private final LongArgument                                          minBlobSizeArg;
	
	/** The max blob size arg. */
	private final LongArgument                                          maxBlobSizeArg;
	
	/** The out arg. */
	private final OutputFileArgument                                    outArg;
	
	/** The call graph cache dir arg. */
	private final DirectoryArgument                                     callGraphCacheDirArg;
	
	/** The dry run arg. */
	private final BooleanArgument                                       dryRunArg;
	
	/** The use data dependencies. */
	private final BooleanArgument                                       useDataDependencies;
	
	/** The datadep arg. */
	private final DirectoryArgument                                     datadepArg;
	
	/** The n arg. */
	private final LongArgument                                          nArg;
	
	private final BooleanArgument                                       useTestImpact;
	
	private final InputFileArgument                                     testImpactFileArg;
	
	private final LongArgument                                          timeArg;
	
	private final EnumArgument                                          aggregateArg;
	
	/**
	 * Instantiates a new untangling.
	 */
	public Untangling() {
		RepositorySettings settings = new RepositorySettings();
		
		repositoryArg = settings.setRepositoryArg(true);
		databaseArgs = settings.setDatabaseArgs(true, "untangling");
		settings.setLoggerArg(false);
		callgraphArg = new DirectoryArgument(settings, "callgraph.eclipse",
				"Home directory of the reposuite callgraph applcation (must contain ./eclipse executable).", null,
				true, false);
		
		atomicChangesArg = new ListArgument(
				settings,
				"atomic.transactions",
				"A list of transactions to be considered as atomic transactions (if not set read all atomic transactions from DB)",
				null, false);
		
		useCallGraph = new BooleanArgument(settings, "vote.callgraph", "Use call graph voter when untangling", "true",
				false);
		
		useChangeCouplings = new BooleanArgument(settings, "vote.changecouplings",
				"Use change coupling voter when untangling", "true", false);
		
		useDataDependencies = new BooleanArgument(settings, "vote.datadependency",
				"Use data dependency voter when untangling", "true", false);
		
		useTestImpact = new BooleanArgument(settings, "vote.testimpact", "Use test coverage information", "true",
				false);
		
		testImpactFileArg = new InputFileArgument(settings, "testimpact.in",
				"File containing a serial version of a ImpactMatrix", null, false);
		
		datadepArg = new DirectoryArgument(settings, "datadependency.eclipse",
				"Home directory of the reposuite datadependency applcation (must contain ./eclipse executable).", null,
				false, false);
		
		changeCouplingsMinSupport = new LongArgument(settings, "vote.changecouplings.minsupport",
				"Set the minimum support for used change couplings to this value", "3", false);
		changeCouplingsMinConfidence = new DoubleArgument(settings, "vote.changecouplings.minconfidence",
				"Set minimum confidence for used change couplings to this value", "0.7", false);
		
		packageDistanceArg = new LongArgument(settings, "package.distance",
				"The maximal allowed distance between packages allowed when generating blobs.", "0", true);
		
		minBlobSizeArg = new LongArgument(settings, "blobsize.min",
				"The minimal number of transactions to be combined within a blob.", "2", true);
		
		maxBlobSizeArg = new LongArgument(settings, "blobsize.max",
				"The maximal number of transactions to be combined within a blob. (-1 means not limit)", "-1", true);
		
		outArg = new OutputFileArgument(settings, "out.file", "Write descriptive statistics into this file", null,
				true, true);
		
		callGraphCacheDirArg = new DirectoryArgument(settings, "callgraph.cache.dir",
				"Cache directory containing call graphs using the naming converntion <transactionId>.cg", null, false,
				false);
		
		dryRunArg = new BooleanArgument(
				settings,
				"dryrun",
				"Setting this option means that the actual untangling will be skipped. This is for testing purposes only.",
				"false", false);
		
		nArg = new LongArgument(settings, "n", "Choose n random artificial blobs. (-1 = unlimited)", "-1", false);
		
		aggregateArg = new EnumArgument(settings, "aggregate", "Method to aggregate when untangling. Possible values "
				+ StringUtils.join(UntanglingAggregate.stringValues()), "MAX", false,
				UntanglingAggregate.stringValues());
		
		timeArg = new LongArgument(settings, "blobWindow",
				"Max number of days all transactions of an artificial blob can be apart. (-1 = unlimited)", "-1", false);
		
		settings.parseArguments();
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
	private int comparePartitions(final ArtificialBlob blob, final Set<Set<JavaChangeOperation>> partitions) {
		
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
	
	/**
	 * Run.
	 */
	public void run() {
		
		boolean dryrun = dryRunArg.getValue();
		
		databaseArgs.getValue();
		PersistenceUtil persistenceUtil = null;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1.getMessage(), e1);
		}
		
		List<String> eclipseArgs = new LinkedList<String>();
		
		Repository repository = repositoryArg.getValue();
		
		eclipseArgs.add("-vmargs");
		eclipseArgs.add(" -Drepository.uri=" + repositoryArg.getRepoDirArg().getValue().toString());
		if (repositoryArg.getPassArg().getValue() != null) {
			eclipseArgs.add(" -Drepository.password=" + repositoryArg.getPassArg().getValue());
		}
		eclipseArgs.add(" -Drepository.type=" + repositoryArg.getRepoTypeArg().getValue());
		if (repositoryArg.getUserArg().getValue() != null) {
			eclipseArgs.add(" -Drepository.user=" + repositoryArg.getUserArg().getValue());
		}
		
		// load the atomic transactions and their change operations
		Set<BlobTransaction> transactions = new HashSet<BlobTransaction>();
		
		if (atomicChangesArg.getValue() != null) {
			HashSet<String> atomicTransactions = atomicChangesArg.getValue();
			for (String transactionId : atomicTransactions) {
				RCSTransaction t = persistenceUtil.loadById(transactionId, RCSTransaction.class);
				List<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(persistenceUtil, t);
				transactions.add(new BlobTransaction(t, ops));
			}
		} else {
			Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class).eq("atomic", true);
			List<RCSTransaction> atomicTransactions = persistenceUtil.load(criteria);
			for (RCSTransaction t : atomicTransactions) {
				List<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(persistenceUtil, t);
				Set<JavaChangeOperation> toRemove = new HashSet<JavaChangeOperation>();
				for (JavaChangeOperation op : ops) {
					if (!(op.getChangedElementLocation().getElement() instanceof JavaMethodDefinition)) {
						toRemove.add(op);
					}
				}
				ops.removeAll(toRemove);
				transactions.add(new BlobTransaction(t, ops));
			}
		}
		
		TestImpactVoter testImpactVoter = null;
		if (useTestImpact.getValue()) {
			File testCoverageIn = testImpactFileArg.getValue();
			if (testCoverageIn == null) {
				throw new UnrecoverableError("If you want to use a test coverage voter, please specify the argument: "
						+ testImpactFileArg.getName());
			}
			try {
				testImpactVoter = new TestImpactVoter(testCoverageIn);
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
		
		// build all artificial blobs. Combine all atomic transactions.
		List<ArtificialBlob> artificialBlobs = new LinkedList<ArtificialBlob>();
		artificialBlobs.addAll(ArtificialBlobGenerator.generateAll(transactions, packageDistanceArg.getValue()
				.intValue(), minBlobSizeArg.getValue().intValue(), maxBlobSizeArg.getValue().intValue()));
		
		int blobSetSize = artificialBlobs.size();
		if (Logger.logInfo()) {
			Logger.info("Generated " + blobSetSize + " artificial blobs.");
		}
		
		File outFile = outArg.getValue();
		BufferedWriter outWriter;
		try {
			outWriter = new BufferedWriter(new FileWriter(outFile));
			outWriter.write("DiffSize,#ChangeOperations,relativeDiffSize");
			outWriter.append(FileUtils.lineSeparator);
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		if (timeArg.getValue() > -1) {
			List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
			for (ArtificialBlob blob : artificialBlobs) {
				if (blob.getDayWindow() < timeArg.getValue()) {
					selectedArtificialBlobs.add(blob);
				}
			}
			artificialBlobs = selectedArtificialBlobs;
			blobSetSize = artificialBlobs.size();
		}
		
		if ((nArg.getValue() != -1l) && (nArg.getValue() < artificialBlobs.size())) {
			List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
			Random generator = new Random();
			for (int i = 0; i < nArg.getValue(); ++i) {
				int r = generator.nextInt(artificialBlobs.size());
				selectedArtificialBlobs.add(artificialBlobs.remove(r));
			}
			artificialBlobs = selectedArtificialBlobs;
			blobSetSize = artificialBlobs.size();
		}
		
		Set<RCSTransaction> usedTransactions = new HashSet<RCSTransaction>();
		
		FileDistanceVoter fileDistanceVoter = new FileDistanceVoter();
		
		MultilevelClusteringCollapseVisitor<JavaChangeOperation> aggregateVisitor = null;
		UntanglingAggregate aggregate = UntanglingAggregate.valueOf(aggregateArg.getValue());
		switch (aggregate) {
			case AVG:
				aggregateVisitor = new AvgCollapseVisitor<JavaChangeOperation>();
				break;
			case SUM:
				aggregateVisitor = new SumCollapseVisitor<JavaChangeOperation>();
				break;
			default:
				aggregateVisitor = new MaxCollapseVisitor<JavaChangeOperation>();
				break;
		}
		if (aggregateArg.getValue().equals("AVG")) {
			
		}
		
		// for each artificial blob
		DescriptiveStatistics stat = new DescriptiveStatistics();
		DescriptiveStatistics relativeStat = new DescriptiveStatistics();
		int counter = 0;
		for (ArtificialBlob blob : artificialBlobs) {
			
			if (Logger.logInfo()) {
				Logger.info("Processing artificial blob: " + (++counter) + "/" + blobSetSize);
			}
			
			scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
			
			RCSTransaction baseT = blob.getLatestTransaction();
			
			// add call graph visitor
			if (useCallGraph.getValue()) {
				scoreVisitors.add(new CallGraphVoter(callgraphArg.getValue(), eclipseArgs
						.toArray(new String[eclipseArgs.size()]), baseT, callGraphCacheDirArg.getValue()));
			}
			
			// add change coupling visitor
			if (useChangeCouplings.getValue()) {
				if ((changeCouplingsMinConfidence.getValue() == null) || (changeCouplingsMinSupport.getValue() == null)) {
					throw new UnrecoverableError(
							"When using change couplings, you have to specify a min support and min confidence value.");
				}
				scoreVisitors.add(new ChangeCouplingVoter(baseT, changeCouplingsMinSupport.getValue().intValue(),
						changeCouplingsMinConfidence.getValue().doubleValue(), persistenceUtil));
			}
			
			// add data dependency visitor
			if (useDataDependencies.getValue()) {
				File dataDepEclipseDir = datadepArg.getValue();
				if (dataDepEclipseDir == null) {
					throw new UnrecoverableError("When using data dependencies -D" + useDataDependencies.getName()
							+ " you must set the -D" + datadepArg.getName() + "!");
				}
				scoreVisitors.add(new DataDependencyVoter(dataDepEclipseDir, repository, baseT));
			}
			
			scoreVisitors.add(fileDistanceVoter);
			
			// add test impact visitor
			if (testImpactVoter != null) {
				scoreVisitors.add(testImpactVoter);
			}
			
			// TODO add Yana's change rule visitor
			// TODO add semdiff visitor
			
			// run the partitioning algorithm
			if (!dryrun) {
				Set<Set<JavaChangeOperation>> partitions = untangle(blob, blob.size(), scoreVisitors, aggregateVisitor);
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
			outWriter.close();
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
}
