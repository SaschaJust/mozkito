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
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.uncommons.maths.combinatorics.PermutationGenerator;

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
import de.unisaarland.cs.st.moskito.untangling.aggregation.LinearRegressionAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.SVMAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.VarSumAggregation;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlob;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.moskito.untangling.blob.AtomicTransaction;
import de.unisaarland.cs.st.moskito.untangling.settings.UntanglingArguments;
import de.unisaarland.cs.st.moskito.untangling.settings.UntanglingSettings;
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
			final Set<String> values = new HashSet<String>();
			for (final ScoreCombinationMode g : ScoreCombinationMode.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	public enum UntanglingCollapse {
		AVG, MAX, RATIO;
		
		public static String[] stringValues() {
			final Set<String> values = new HashSet<String>();
			for (final UntanglingCollapse g : UntanglingCollapse.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	public static Random                          random          = new Random();
	
	public long                                   seed;
	
	private ScoreAggregation<JavaChangeOperation> aggregator      = null;
	
	private TestImpactVoter                       testImpactVoter = null;
	
	private final Repository                      repository;
	
	private final boolean                         dryrun;
	
	private final PersistenceUtil                 persistenceUtil;
	
	private final RepositoryArguments             repositoryArg;
	
	private final DatabaseArguments               databaseArgs;
	
	private final UntanglingArguments             untanglingArgs;
	
	/**
	 * Instantiates a new untangling.
	 */
	public Untangling() {
		final UntanglingSettings settings = new UntanglingSettings();
		
		this.repositoryArg = settings.setRepositoryArg(true);
		this.databaseArgs = settings.setDatabaseArgs(true, "untangling");
		this.untanglingArgs = settings.setUntanglingArgs(true);
		settings.setLoggerArg(false);
		
		settings.parseArguments();
		
		if (this.untanglingArgs.getSeedArg().getValue() != null) {
			this.seed = this.untanglingArgs.getSeedArg().getValue();
		} else {
			this.seed = random.nextLong();
		}
		random.setSeed(this.seed);
		
		this.repository = this.repositoryArg.getValue();
		this.dryrun = this.untanglingArgs.getDryRunArg().getValue();
		
		this.databaseArgs.getValue();
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
		
		final List<List<JavaChangeOperation>> originalPartitions = blob.getChangeOperationPartitions();
		
		final PermutationGenerator<Set<JavaChangeOperation>> pGen = new PermutationGenerator<Set<JavaChangeOperation>>(
		                                                                                                               partitions);
		
		int minDiff = Integer.MAX_VALUE;
		
		while (pGen.hasMore()) {
			final List<Set<JavaChangeOperation>> nextPermutation = pGen.nextPermutationAsList();
			int diff = 0;
			for (int i = 0; i < nextPermutation.size(); ++i) {
				final Set<JavaChangeOperation> untangledPart = nextPermutation.get(i);
				final List<JavaChangeOperation> originalPart = originalPartitions.get(i);
				diff += CollectionUtils.subtract(originalPart, untangledPart).size();
			}
			if (diff < minDiff) {
				minDiff = diff;
			}
		}
		return minDiff;
	}
	
	public List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> generateScoreVisitors(final RCSTransaction transaction) {
		
		if ((this.testImpactVoter == null) && (this.untanglingArgs.getUseTestImpact().getValue())) {
			final File testCoverageIn = this.untanglingArgs.getTestImpactFileArg().getValue();
			if (testCoverageIn == null) {
				throw new UnrecoverableError("If you want to use a test coverage voter, please specify the argument: "
				        + this.untanglingArgs.getTestImpactFileArg().getName());
			}
			try {
				this.testImpactVoter = new TestImpactVoter(testCoverageIn);
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error("Error while creating TestCoverageVoter. Skipping this voter. More details see below.");
					Logger.error(e.getMessage(), e);
				}
			} catch (final ClassNotFoundException e) {
				if (Logger.logError()) {
					Logger.error("Error while creating TestCoverageVoter. Skipping this voter. More details see below.");
					Logger.error(e.getMessage(), e);
				}
			}
		}
		
		final List<String> eclipseArgs = new LinkedList<String>();
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
		
		final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
		scoreVisitors.add(new LineDistanceVoter());
		scoreVisitors.add(new FileDistanceVoter());
		// add call graph visitor
		if (this.untanglingArgs.getUseCallGraph().getValue()) {
			scoreVisitors.add(new CallGraphVoter(this.untanglingArgs.getCallgraphArg().getValue(),
			                                     eclipseArgs.toArray(new String[eclipseArgs.size()]), transaction,
			                                     this.untanglingArgs.getCallGraphCacheDirArg().getValue()));
		}
		
		// add change coupling visitor
		if (this.untanglingArgs.getUseChangeCouplings().getValue()) {
			if ((this.untanglingArgs.getChangeCouplingsMinConfidence().getValue() == null)
			        || (this.untanglingArgs.getChangeCouplingsMinSupport().getValue() == null)) {
				throw new UnrecoverableError(
				                             "When using change couplings, you have to specify a min support and min confidence value.");
			}
			
			final File ccCacheDir = this.untanglingArgs.getChangeCouplingsCacheDirArg().getValue();
			scoreVisitors.add(new FileChangeCouplingVoter(transaction,
			                                              this.untanglingArgs.getChangeCouplingsMinSupport().getValue()
			                                                                 .intValue(),
			                                              this.untanglingArgs.getChangeCouplingsMinConfidence()
			                                                                 .getValue().doubleValue(),
			                                              this.persistenceUtil, ccCacheDir));
		}
		
		// add data dependency visitor
		if (this.untanglingArgs.getUseDataDependencies().getValue()) {
			final File dataDepEclipseDir = this.untanglingArgs.getDatadepArg().getValue();
			if (dataDepEclipseDir == null) {
				throw new UnrecoverableError("When using data dependencies -D"
				        + this.untanglingArgs.getUseDataDependencies().getName() + " you must set the -D"
				        + this.untanglingArgs.getDatadepArg().getName() + "!");
			}
			scoreVisitors.add(new DataDependencyVoter(dataDepEclipseDir, this.repository, transaction,
			                                          this.untanglingArgs.getDataDependencyCacheDirArg().getValue()));
		}
		
		// add test impact visitor
		if (this.testImpactVoter != null) {
			scoreVisitors.add(this.testImpactVoter);
		}
		
		return scoreVisitors;
		
	}
	
	public List<String> getScoreVisitorNames() {
		final List<String> result = new LinkedList<String>();
		result.add(LineDistanceVoter.class.getSimpleName());
		result.add(FileDistanceVoter.class.getSimpleName());
		if (this.untanglingArgs.getUseCallGraph().getValue()) {
			result.add(CallGraphVoter.class.getSimpleName());
		}
		if (this.untanglingArgs.getUseChangeCouplings().getValue()) {
			result.add(FileChangeCouplingVoter.class.getSimpleName());
		}
		if (this.untanglingArgs.getUseDataDependencies().getValue()) {
			result.add(DataDependencyVoter.class.getSimpleName());
		}
		if (this.untanglingArgs.getUseTestImpact().getValue()) {
			result.add(TestImpactVoter.class.getSimpleName());
		}
		return result;
	}
	
	/**
	 * Run.
	 */
	public void run() {
		
		// load the atomic transactions and their change operations
		final List<AtomicTransaction> transactions = new LinkedList<AtomicTransaction>();
		
		if (this.untanglingArgs.getAtomicChangesArg().getValue() != null) {
			final HashSet<String> atomicTransactions = this.untanglingArgs.getAtomicChangesArg().getValue();
			for (final String transactionId : atomicTransactions) {
				final RCSTransaction t = this.persistenceUtil.loadById(transactionId, RCSTransaction.class);
				
				// FIXME this is required due to some unknown problem which
				// causes NullpointerExceptions becaus Fetch.LAZY returns null.
				t.getAuthor();
				t.toString();
				
				final Collection<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
				                                                                                  t);
				transactions.add(new AtomicTransaction(t, ops));
			}
		} else {
			final Criteria<RCSTransaction> criteria = this.persistenceUtil.createCriteria(RCSTransaction.class)
			                                                              .eq("atomic", true);
			final List<RCSTransaction> atomicTransactions = this.persistenceUtil.load(criteria);
			for (final RCSTransaction t : atomicTransactions) {
				
				// FIXME this is required due to some unknown problem which
				// causes NullpointerExceptions becaus Fetch.LAZY returns null.
				t.getAuthor();
				t.toString();
				
				final Collection<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
				                                                                                  t);
				final Set<JavaChangeOperation> toRemove = new HashSet<JavaChangeOperation>();
				for (final JavaChangeOperation op : ops) {
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
			final Set<AtomicTransaction> randomTransactions = new HashSet<AtomicTransaction>();
			for (int i = 0; i < 50; ++i) {
				random.nextInt(transactions.size());
				randomTransactions.add(transactions.get(i));
				transactions.remove(i);
			}
			transactions.clear();
			transactions.addAll(randomTransactions);
		}
		
		artificialBlobs.addAll(ArtificialBlobGenerator.generateAll(transactions,
		                                                           this.untanglingArgs.getPackageDistanceArg()
		                                                                              .getValue().intValue(),
		                                                           this.untanglingArgs.getMinBlobSizeArg().getValue()
		                                                                              .intValue(),
		                                                           this.untanglingArgs.getMaxBlobSizeArg().getValue()
		                                                                              .intValue(),
		                                                           this.untanglingArgs.getTimeArg().getValue()));
		
		int blobSetSize = artificialBlobs.size();
		if (Logger.logInfo()) {
			Logger.info("Generated " + blobSetSize + " artificial blobs.");
		}
		
		if (System.getProperty("generateBlobsOnly") != null) {
			return;
		}
		
		final File outFile = this.untanglingArgs.getOutArg().getValue();
		BufferedWriter outWriter;
		try {
			outWriter = new BufferedWriter(new FileWriter(outFile));
			outWriter.write("DiffSize,#ChangeOperations,relativeDiffSize,lowestScore");
			outWriter.append(FileUtils.lineSeparator);
		} catch (final IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		if ((this.untanglingArgs.getnArg().getValue() != -1l)
		        && (this.untanglingArgs.getnArg().getValue() < artificialBlobs.size())) {
			final List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
			for (int i = 0; i < this.untanglingArgs.getnArg().getValue(); ++i) {
				final int r = random.nextInt(artificialBlobs.size());
				selectedArtificialBlobs.add(artificialBlobs.remove(r));
			}
			artificialBlobs = selectedArtificialBlobs;
			blobSetSize = artificialBlobs.size();
		}
		
		final Set<RCSTransaction> usedTransactions = new HashSet<RCSTransaction>();
		
		MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor = null;
		final UntanglingCollapse collapse = UntanglingCollapse.valueOf(this.untanglingArgs.getCollapseArg().getValue());
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
		final ScoreCombinationMode scoreAggregationMode = ScoreCombinationMode.valueOf(this.untanglingArgs.getScoreModeArg()
		                                                                                                  .getValue());
		switch (scoreAggregationMode) {
			case SUM:
				this.aggregator = new SumAggregation<JavaChangeOperation>();
				break;
			case VARSUM:
				this.aggregator = new VarSumAggregation<JavaChangeOperation>();
				break;
			case LINEAR_REGRESSION:
				final LinearRegressionAggregation linarRegressionAggregator = new LinearRegressionAggregation(this);
				// train score aggregation model
				final Set<AtomicTransaction> trainTransactions = new HashSet<AtomicTransaction>();
				for (final ArtificialBlob blob : artificialBlobs) {
					trainTransactions.addAll(blob.getAtomicTransactions());
				}
				linarRegressionAggregator.train(trainTransactions);
				this.aggregator = linarRegressionAggregator;
				break;
			case SVM:
				final SVMAggregation svmAggregator = SVMAggregation.createInstance(this);
				// train score aggregation model
				final Set<AtomicTransaction> svmTrainTransactions = new HashSet<AtomicTransaction>();
				for (final ArtificialBlob blob : artificialBlobs) {
					svmTrainTransactions.addAll(blob.getAtomicTransactions());
				}
				svmAggregator.train(svmTrainTransactions);
				this.aggregator = svmAggregator;
				break;
			default:
				throw new UnrecoverableError("Unknown score aggregation mode found: "
				        + this.untanglingArgs.getScoreModeArg().getValue());
		}
		
		// for each artificial blob
		final DescriptiveStatistics stat = new DescriptiveStatistics();
		final DescriptiveStatistics relativeStat = new DescriptiveStatistics();
		int counter = 0;
		for (final ArtificialBlob blob : artificialBlobs) {
			
			if (Logger.logInfo()) {
				Logger.info("Processing artificial blob: " + (++counter) + "/" + blobSetSize);
			}
			
			final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = generateScoreVisitors(blob.getLatestTransaction());
			
			// run the partitioning algorithm
			if (!this.dryrun) {
				
				final MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(
				                                                                                                           blob.getAllChangeOperations(),
				                                                                                                           scoreVisitors,
				                                                                                                           this.aggregator,
				                                                                                                           collapseVisitor);
				
				final Set<Set<JavaChangeOperation>> partitions = clustering.getPartitions(blob.size());
				
				// compare the true and the computed partitions and score the
				// similarity score in a descriptive statistic
				final int diff = comparePartitions(blob, partitions);
				stat.addValue(diff);
				final double relDiff = ((double) diff) / ((double) blob.getAllChangeOperations().size());
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
				} catch (final IOException e) {
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
			for (final RCSTransaction t : usedTransactions) {
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
		} catch (final IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
	
}
