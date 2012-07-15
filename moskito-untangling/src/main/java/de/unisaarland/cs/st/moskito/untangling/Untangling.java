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
package de.unisaarland.cs.st.moskito.untangling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Days;
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
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.untangling.aggregation.LinearRegressionAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.SVMAggregation;
import de.unisaarland.cs.st.moskito.untangling.aggregation.VarSumAggregation;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlob;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;
import de.unisaarland.cs.st.moskito.untangling.blob.SerializableArtificialBlob;
import de.unisaarland.cs.st.moskito.untangling.blob.combine.CombineOperator;
import de.unisaarland.cs.st.moskito.untangling.settings.UntanglingControl;
import de.unisaarland.cs.st.moskito.untangling.settings.UntanglingOptions;
import de.unisaarland.cs.st.moskito.untangling.voters.FileDistanceVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.LineDistanceVoter;
import de.unisaarland.cs.st.moskito.untangling.voters.MultilevelClusteringScoreVisitorFactory;

/**
 * The Class Untangling.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Untangling {
	
	/**
	 * The Enum ScoreCombinationMode.
	 * 
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	public enum ScoreCombinationMode {
		
		/** The SUM. */
		SUM, /** The VARSUM. */
		VARSUM, /** The LINEA r_ regression. */
		LINEAR_REGRESSION, /** The SVM. */
		SVM;
		
		/**
		 * String values.
		 * 
		 * @return the string[]
		 */
		public static String[] stringValues() {
			final Set<String> values = new HashSet<String>();
			for (final ScoreCombinationMode g : ScoreCombinationMode.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	/**
	 * The Enum UntanglingCollapse.
	 * 
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	public enum UntanglingCollapse {
		
		/** The AVG. */
		AVG,
		/** The MAX. */
		MAX,
		/** The RATIO. */
		RATIO;
		
		/**
		 * String values.
		 * 
		 * @return the string[]
		 */
		public static String[] stringValues() {
			final Set<String> values = new HashSet<String>();
			for (final UntanglingCollapse g : UntanglingCollapse.values()) {
				values.add(g.toString());
			}
			return values.toArray(new String[values.size()]);
		}
	}
	
	/** The random. */
	public static Random                          random     = new Random();
	
	/** The seed. */
	public long                                   seed;
	
	/** The aggregator. */
	private ScoreAggregation<JavaChangeOperation> aggregator = null;
	
	/** The untangling control. */
	private final UntanglingControl               untanglingControl;
	
	/**
	 * Instantiates a new untangling.
	 * 
	 * @param settings
	 *            the settings
	 */
	public Untangling(final Settings settings) {
		
		try {
			
			final UntanglingOptions untanglingOptions = new UntanglingOptions(settings.getRoot(), Requirement.required);
			final ArgumentSet<UntanglingControl, UntanglingOptions> untanglingControlArgumentSet = ArgumentSetFactory.create(untanglingOptions);
			
			if (settings.helpRequested()) {
				System.err.println(settings.getHelpString());
				throw new Shutdown();
			}
			
			this.untanglingControl = untanglingControlArgumentSet.getValue();
			
			if (this.untanglingControl.getSeed() != null) {
				this.seed = this.untanglingControl.getSeed();
			} else {
				this.seed = random.nextLong();
			}
			random.setSeed(this.seed);
			
		} catch (final ArgumentRegistrationException | SettingsParseError | ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			if (Logger.logInfo()) {
				Logger.info(settings.getHelpString());
			}
			throw new Shutdown();
		}
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
	
	/**
	 * Generate score visitors.
	 * 
	 * @param transaction
	 *            the transaction
	 * @return the list
	 */
	public List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> generateScoreVisitors(final RCSTransaction transaction) {
		
		final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = new LinkedList<>();
		
		for (final MultilevelClusteringScoreVisitorFactory<? extends MultilevelClusteringScoreVisitor<JavaChangeOperation>> voterFactory : this.untanglingControl.getConfidenceVoters()) {
			scoreVisitors.add(voterFactory.createVoter(transaction));
		}
		scoreVisitors.add(new LineDistanceVoter());
		scoreVisitors.add(new FileDistanceVoter());
		return scoreVisitors;
	}
	
	/**
	 * Gets the score visitor names.
	 * 
	 * @return the score visitor names
	 */
	public List<String> getScoreVisitorNames() {
		final List<String> result = new LinkedList<String>();
		result.add(LineDistanceVoter.class.getSimpleName());
		result.add(FileDistanceVoter.class.getSimpleName());
		for (final MultilevelClusteringScoreVisitorFactory<? extends MultilevelClusteringScoreVisitor<JavaChangeOperation>> voterFactory : this.untanglingControl.getConfidenceVoters()) {
			result.add(voterFactory.getInnerClass().getSimpleName());
		}
		return result;
	}
	
	/**
	 * Run.
	 */
	public void run() {
		
		final PersistenceUtil persistenceUtil = this.untanglingControl.getPersistenceUtil();
		
		List<ArtificialBlob> artificialBlobs = new LinkedList<ArtificialBlob>();
		File serialBlobFile = null;
		
		if (this.untanglingControl.getArtificialBlobCacheDir() != null) {
			final File cacheRootDir = this.untanglingControl.getArtificialBlobCacheDir();
			final StringBuilder fileName = new StringBuilder();
			fileName.append(this.untanglingControl.getPersistenceUtil().getToolInformation().hashCode());
			fileName.append("_");
			fileName.append(this.untanglingControl.getBlobWindowSize());
			fileName.append("_artificialBlobs.ser");
			serialBlobFile = new File(cacheRootDir.getAbsolutePath() + FileUtils.fileSeparator + fileName.toString());
			if (serialBlobFile.exists()) {
				try (FileInputStream fis = new FileInputStream(serialBlobFile);
				        ObjectInputStream in = new ObjectInputStream(fis);) {
					
					@SuppressWarnings ("unchecked")
					final List<SerializableArtificialBlob> serializedBlobs = (List<SerializableArtificialBlob>) in.readObject();
					for (final SerializableArtificialBlob serialBlob : serializedBlobs) {
						artificialBlobs.add(serialBlob.unserialize(persistenceUtil));
					}
					
				} catch (final IOException | ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
		}
		
		if (artificialBlobs.isEmpty()) {
			
			final List<String> atomicTransactionIds = this.untanglingControl.getAtomicTransactionIds();
			
			// this map will contain change set lists that correspond to the chaneg sets reachable from the
			// corresponding
			// key within the blobWindowsSize
			final Map<ChangeSet, List<ChangeSet>> combinationCandidates = new HashMap<>();
			
			Criteria<RCSTransaction> transactionCriteria = null;
			if ((atomicTransactionIds != null) && (!atomicTransactionIds.isEmpty())) {
				transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).in("id",
				                                                                              atomicTransactionIds);
			} else {
				transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).eq("atomic", true);
			}
			
			// now load the criteria and ad fill the candidate map
			
			final List<RCSTransaction> atomicTransactions = persistenceUtil.load(transactionCriteria.oderByDesc("javaTimestamp"));
			final int blobWindowSize = this.untanglingControl.getBlobWindowSize();
			
			final Set<ChangeSet> toCompare = new HashSet<>();
			
			for (final RCSTransaction t : atomicTransactions) {
				
				// FIXME this is required due to some unknown problem which
				// causes NullpointerExceptions because Fetch.LAZY returns null.
				t.getAuthor();
				t.toString();
				t.getBranchNames();
				
				final Collection<JavaChangeOperation> ops = PPAPersistenceUtil.getChangeOperation(persistenceUtil, t);
				final Set<JavaChangeOperation> toRemove = new HashSet<JavaChangeOperation>();
				for (final JavaChangeOperation op : ops) {
					if (!(op.getChangedElementLocation().getElement() instanceof JavaMethodDefinition)) {
						toRemove.add(op);
					}
				}
				ops.removeAll(toRemove);
				if (!ops.isEmpty()) {
					final ChangeSet changeSet = new ChangeSet(t, ops);
					toCompare.add(changeSet);
					combinationCandidates.put(new ChangeSet(t, ops), new LinkedList<ChangeSet>());
					final Set<ChangeSet> toCompareRemove = new HashSet<>();
					for (final ChangeSet candidate : toCompare) {
						if (Math.abs(Days.daysBetween(t.getTimestamp(), candidate.getTransaction().getTimestamp())
						                 .getDays()) <= blobWindowSize) {
							combinationCandidates.get(candidate).add(changeSet);
						} else {
							toCompareRemove.add(candidate);
						}
					}
					toCompare.removeAll(toCompareRemove);
				}
			}
			
			// Now we have a map of change sets pointing to change sets that are within the same blobWindowSize
			
			final CombineOperator<ChangeSet> combineOperator = this.untanglingControl.getCombineOperator();
			
			// build all artificial blobs. Combine all atomic transactions.
			
			final ArtificialBlobGenerator blobGenerator = new ArtificialBlobGenerator(combineOperator);
			for (final Entry<ChangeSet, List<ChangeSet>> entry : combinationCandidates.entrySet()) {
				final Set<ChangeSet> entrySet = new HashSet<ChangeSet>();
				entrySet.addAll(entry.getValue());
				entrySet.add(entry.getKey());
				artificialBlobs.addAll(blobGenerator.generateAll(entrySet, this.untanglingControl.getMinBlobSize(),
				                                                 this.untanglingControl.getMaxBlobSize()));
			}
			
			// serialize
			if (serialBlobFile != null) {
				try (FileOutputStream fos = new FileOutputStream(serialBlobFile);
				        ObjectOutputStream out = new ObjectOutputStream(fos);) {
					final List<SerializableArtificialBlob> serialList = new LinkedList<>();
					for (final ArtificialBlob blob : artificialBlobs) {
						serialList.add(new SerializableArtificialBlob(blob));
					}
					out.writeObject(serialList);
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
		}
		
		int blobSetSize = artificialBlobs.size();
		if (Logger.logInfo()) {
			Logger.info("Generated %s artificial blobs.", blobSetSize);
		}
		
		// TODO this is a debugging fragment. Remove later.
		if (System.getProperty("generateBlobsOnly") != null) {
			return;
		}
		
		final File outFile = this.untanglingControl.getOutputFile();
		try (FileWriter fileWriter = new FileWriter(outFile); BufferedWriter outWriter = new BufferedWriter(fileWriter);) {
			
			outWriter.write("DiffSize,#ChangeOperations,relativeDiffSize,lowestScore");
			outWriter.append(FileUtils.lineSeparator);
			
			if ((this.untanglingControl.getN() != -1l) && (this.untanglingControl.getN() < artificialBlobs.size())) {
				final List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
				for (int i = 0; i < this.untanglingControl.getN(); ++i) {
					final int r = random.nextInt(artificialBlobs.size());
					selectedArtificialBlobs.add(artificialBlobs.remove(r));
				}
				artificialBlobs = selectedArtificialBlobs;
				blobSetSize = artificialBlobs.size();
			}
			
			final Set<RCSTransaction> usedTransactions = new HashSet<RCSTransaction>();
			
			MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor = null;
			switch (this.untanglingControl.getCollapseMode()) {
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
			switch (this.untanglingControl.getScoreMode()) {
				case SUM:
					this.aggregator = new SumAggregation<JavaChangeOperation>();
					break;
				case VARSUM:
					this.aggregator = new VarSumAggregation<JavaChangeOperation>();
					break;
				case LINEAR_REGRESSION:
					final LinearRegressionAggregation linarRegressionAggregator = new LinearRegressionAggregation(this);
					// train score aggregation model
					final Set<ChangeSet> trainTransactions = new HashSet<ChangeSet>();
					for (final ArtificialBlob blob : artificialBlobs) {
						trainTransactions.addAll(blob.getAtomicTransactions());
					}
					linarRegressionAggregator.train(trainTransactions);
					this.aggregator = linarRegressionAggregator;
					break;
				case SVM:
					final SVMAggregation svmAggregator = SVMAggregation.createInstance(this);
					// train score aggregation model
					final Set<ChangeSet> svmTrainTransactions = new HashSet<ChangeSet>();
					for (final ArtificialBlob blob : artificialBlobs) {
						svmTrainTransactions.addAll(blob.getAtomicTransactions());
					}
					svmAggregator.train(svmTrainTransactions);
					this.aggregator = svmAggregator;
					break;
				default:
					throw new UnrecoverableError("Unknown score aggregation mode found: "
					        + this.untanglingControl.getScoreMode());
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
				if (!this.untanglingControl.isDryRun()) {
					
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
		} catch (final IOException e1) {
			throw new UnrecoverableError(e1);
		}
	}
}
