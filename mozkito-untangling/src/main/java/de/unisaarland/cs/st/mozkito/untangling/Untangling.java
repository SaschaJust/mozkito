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
package de.unisaarland.cs.st.mozkito.untangling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import de.unisaarland.cs.st.mozkito.clustering.AvgCollapseVisitor;
import de.unisaarland.cs.st.mozkito.clustering.MaxCollapseVisitor;
import de.unisaarland.cs.st.mozkito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.mozkito.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.mozkito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.mozkito.clustering.ScoreAggregation;
import de.unisaarland.cs.st.mozkito.clustering.SumAggregation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaMethodDefinition;
import de.unisaarland.cs.st.mozkito.persistence.Criteria;
import de.unisaarland.cs.st.mozkito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.untangling.aggregation.LinearRegressionAggregation;
import de.unisaarland.cs.st.mozkito.untangling.aggregation.SVMAggregation;
import de.unisaarland.cs.st.mozkito.untangling.aggregation.VarSumAggregation;
import de.unisaarland.cs.st.mozkito.untangling.blob.ArtificialBlob;
import de.unisaarland.cs.st.mozkito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.mozkito.untangling.blob.ChangeSet;
import de.unisaarland.cs.st.mozkito.untangling.blob.SerializableArtificialBlob;
import de.unisaarland.cs.st.mozkito.untangling.blob.combine.CombineOperator;
import de.unisaarland.cs.st.mozkito.untangling.settings.UntangleInstruction;
import de.unisaarland.cs.st.mozkito.untangling.settings.UntanglingControl;
import de.unisaarland.cs.st.mozkito.untangling.settings.UntanglingOptions;
import de.unisaarland.cs.st.mozkito.untangling.voters.FileDistanceVoter;
import de.unisaarland.cs.st.mozkito.untangling.voters.LineDistanceVoter;
import de.unisaarland.cs.st.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

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
	private UntanglingComparisonResult comparePartitions(final ArtificialBlob blob,
	                                                     final Set<Set<JavaChangeOperation>> partitions) {
		
		Condition.check(blob.getTransactions().size() == partitions.size(),
		                "The size of partitions in artificial blob and the size of untangled partitions must be equal.");
		
		final List<List<JavaChangeOperation>> originalPartitions = blob.getChangeOperationPartitions();
		
		final PermutationGenerator<Set<JavaChangeOperation>> pGen = new PermutationGenerator<Set<JavaChangeOperation>>(
		                                                                                                               partitions);
		
		int minDiff = Integer.MAX_VALUE;
		double maxJaccard = 0;
		int numCorrectPartition = 0;
		int numFalsePartition = 0;
		int fileError = 0;
		final Set<Long> blobFiles = new HashSet<>();
		double fileErrorBase = 0;
		
		while (pGen.hasMore()) {
			final List<Set<JavaChangeOperation>> nextPermutation = pGen.nextPermutationAsList();
			int diff = 0;
			double jaccard = 0d;
			int numCorrect = 0;
			int numFalse = 0;
			
			for (int i = 0; i < nextPermutation.size(); ++i) {
				final Set<JavaChangeOperation> untangledPart = nextPermutation.get(i);
				final List<JavaChangeOperation> originalPart = originalPartitions.get(i);
				diff += CollectionUtils.subtract(originalPart, untangledPart).size();
				
				final int intersect = CollectionUtils.intersection(originalPart, untangledPart).size();
				final int union = CollectionUtils.union(originalPart, untangledPart).size();
				jaccard += (Integer.valueOf(intersect).doubleValue() / Integer.valueOf(union).doubleValue());
				numCorrect += intersect;
				numFalse += CollectionUtils.subtract(untangledPart, originalPart).size();
			}
			
			jaccard = jaccard / blob.size();
			
			if (diff < minDiff) {
				minDiff = diff;
			}
			if (jaccard > maxJaccard) {
				maxJaccard = jaccard;
				numCorrectPartition = numCorrect;
				numFalsePartition = numFalse;
				final boolean compBlobFiles = blobFiles.isEmpty();
				
				final List<Set<Long>> trueFilePartition = new ArrayList<>(originalPartitions.size());
				for (final List<JavaChangeOperation> originalPart : originalPartitions) {
					final Set<Long> filePart = new HashSet<>();
					for (final JavaChangeOperation op : originalPart) {
						final long fileId = op.getRevision().getChangedFile().getGeneratedId();
						filePart.add(fileId);
						if (compBlobFiles) {
							blobFiles.add(fileId);
						}
					}
					trueFilePartition.add(filePart);
				}
				
				fileError = 0;
				fileErrorBase = 0;
				for (int i = 0; i < nextPermutation.size(); ++i) {
					final Set<JavaChangeOperation> originalPart = nextPermutation.get(i);
					final Set<Long> filePart = new HashSet<>();
					for (final JavaChangeOperation op : originalPart) {
						filePart.add(op.getRevision().getChangedFile().getGeneratedId());
					}
					fileError += CollectionUtils.subtract(filePart, trueFilePartition.get(i)).size();
					fileErrorBase += filePart.size();
				}
				
			}
		}
		return new UntanglingComparisonResult(minDiff, maxJaccard, numCorrectPartition, numFalsePartition, blob.size(),
		                                      ((fileError) / fileErrorBase));
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
			result.add(voterFactory.getVoterName());
		}
		return result;
	}
	
	/**
	 * Run.
	 */
	public void run() {
		if (this.untanglingControl.measurePrecision()) {
			runPrecisionExperiment();
			return;
		}
		
		final Set<ChangeSet> atomicChangeSets = new HashSet<ChangeSet>();
		final PersistenceUtil persistenceUtil = this.untanglingControl.getPersistenceUtil();
		
		final List<String> atomicTransactionIds = this.untanglingControl.getAtomicTransactionIds();
		Criteria<RCSTransaction> transactionCriteria = null;
		if ((atomicTransactionIds != null) && (!atomicTransactionIds.isEmpty())) {
			transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).in("id", atomicTransactionIds);
		} else {
			transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).eq("atomic", true);
		}
		
		final List<RCSTransaction> atomicTransactions = persistenceUtil.load(transactionCriteria.oderByDesc("javaTimestamp"));
		
		for (final RCSTransaction t : atomicTransactions) {
			atomicChangeSets.add(new ChangeSet(t, PPAPersistenceUtil.getChangeOperation(persistenceUtil, t)));
		}
		
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
				final LinearRegressionAggregation linarRegressionAggregator = new LinearRegressionAggregation(this, 1d);
				// train score aggregation model
				linarRegressionAggregator.train(atomicChangeSets);
				this.aggregator = linarRegressionAggregator;
				break;
			case SVM:
				final SVMAggregation svmAggregator = SVMAggregation.createInstance(this, 1d);
				svmAggregator.train(atomicChangeSets);
				this.aggregator = svmAggregator;
				break;
			default:
				throw new UnrecoverableError("Unknown score aggregation mode found: "
				        + this.untanglingControl.getScoreMode());
		}
		
		try (BufferedWriter outWriter = new BufferedWriter(new FileWriter(this.untanglingControl.getOutputFile()))) {
			
			outWriter.write("ChangeSetID,PartitionNumber,ChangeOperationIDs");
			outWriter.append(FileUtils.lineSeparator);
			int iCounter = 0;
			final String numInstructions = String.valueOf(this.untanglingControl.getChangeSetsToUntangle().size());
			for (final UntangleInstruction instruction : this.untanglingControl.getChangeSetsToUntangle()) {
				
				if (Logger.logInfo()) {
					Logger.info("Processing untangling instruction: %s (%s / %s).", instruction.toString(),
					            (String.valueOf(++iCounter)), numInstructions);
				}
				
				final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = generateScoreVisitors(instruction.getChangeSet()
				                                                                                                                   .getTransaction());
				
				final MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(
				                                                                                                           instruction.getChangeSet()
				                                                                                                                      .getOperations(),
				                                                                                                           scoreVisitors,
				                                                                                                           this.aggregator,
				                                                                                                           collapseVisitor);
				Set<Set<JavaChangeOperation>> partitions = null;
				if (instruction.getNumPartitions() > 0) {
					partitions = clustering.getPartitions(instruction.getNumPartitions());
				} else {
					// TODO implement threshold untangling support
					throw new UnrecoverableError("Threshold untangling not yet supported.");
				}
				
				int counter = 0;
				for (final Set<JavaChangeOperation> partition : partitions) {
					outWriter.append(instruction.getChangeSet().getTransaction().getId());
					outWriter.append(",");
					outWriter.append(String.valueOf(counter++));
					outWriter.append(",");
					final Iterator<JavaChangeOperation> iterator = partition.iterator();
					if (iterator.hasNext()) {
						outWriter.append(String.valueOf(iterator.next().getId()));
					}
					while (iterator.hasNext()) {
						outWriter.append(":");
						outWriter.append(String.valueOf(iterator.next().getId()));
					}
					outWriter.append(FileUtils.lineSeparator);
				}
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
	}
	
	public void runPrecisionExperiment() {
		final PersistenceUtil persistenceUtil = this.untanglingControl.getPersistenceUtil();
		
		List<ArtificialBlob> artificialBlobs = new LinkedList<ArtificialBlob>();
		final CombineOperator<ChangeSet> combineOperator = this.untanglingControl.getCombineOperator();
		File serialBlobFile = null;
		
		if (this.untanglingControl.getArtificialBlobCacheDir() != null) {
			final File cacheRootDir = this.untanglingControl.getArtificialBlobCacheDir();
			final StringBuilder fileName = new StringBuilder();
			fileName.append(this.untanglingControl.getPersistenceUtil().getToolInformation().hashCode());
			fileName.append("_");
			fileName.append(this.untanglingControl.getBlobWindowSize());
			fileName.append("_");
			fileName.append(combineOperator.getClass().getSimpleName());
			fileName.append("_artificialBlobs.ser");
			serialBlobFile = new File(cacheRootDir.getAbsolutePath() + FileUtils.fileSeparator + fileName.toString());
			if (Logger.logInfo()) {
				Logger.info("Searching for serialized artificial blobs in file %s.", serialBlobFile.getAbsolutePath());
			}
			if (serialBlobFile.exists()) {
				if (Logger.logInfo()) {
					Logger.info("Found serialized artificial blobs in file %s. Using that file to restore pre-computed blobs.",
					            serialBlobFile.getAbsolutePath());
				}
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
		
		final List<String> atomicTransactionIds = this.untanglingControl.getAtomicTransactionIds();
		
		// this map will contain change set lists that correspond to the change sets reachable from the
		// corresponding
		// key within the blobWindowsSize
		final Map<ChangeSet, List<ChangeSet>> combinationCandidates = new HashMap<>();
		
		Criteria<RCSTransaction> transactionCriteria = null;
		if ((atomicTransactionIds != null) && (!atomicTransactionIds.isEmpty())) {
			transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).in("id", atomicTransactionIds);
		} else {
			transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class).eq("atomic", true);
		}
		
		// now load the criteria and ad fill the candidate map
		if (Logger.logInfo()) {
			Logger.info("Computing transaction combination candidates using blobWindowSize=%s",
			            String.valueOf(this.untanglingControl.getBlobWindowSize()));
		}
		final List<RCSTransaction> atomicTransactions = persistenceUtil.load(transactionCriteria.oderByDesc("javaTimestamp"));
		final int blobWindowSize = this.untanglingControl.getBlobWindowSize();
		
		final Set<ChangeSet> toCompare = new HashSet<>();
		
		for (final RCSTransaction t : atomicTransactions) {
			
			// this is required due to some unknown problem which
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
		
		if (artificialBlobs.isEmpty()) {
			// Now we have a map of change sets pointing to change sets that are within the same blobWindowSize
			
			if (Logger.logInfo()) {
				Logger.info("Computing artificial blobs of order two. If serialize was found, unserialization did not succeed.");
			}
			
			// build all artificial blobs. Combine all atomic transactions.
			final ArtificialBlobGenerator blobGenerator = new ArtificialBlobGenerator(combineOperator);
			
			if (Logger.logInfo()) {
				Logger.info("Generating artificial blobs using %d combination candidates.",
				            combinationCandidates.size());
			}
			
			for (final Entry<ChangeSet, List<ChangeSet>> entry : combinationCandidates.entrySet()) {
				final Set<ChangeSet> entrySet = new HashSet<ChangeSet>();
				entrySet.addAll(entry.getValue());
				entrySet.add(entry.getKey());
				// use minimum and maximum blob size of 2 for pre-computation of order two blobs
				artificialBlobs.addAll(blobGenerator.generateAll(entrySet, 2, 2));
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
		
		if (Logger.logInfo()) {
			final Set<ChangeSet> changeSetsInBlobsSet = new HashSet<>();
			for (final ArtificialBlob blob : artificialBlobs) {
				changeSetsInBlobsSet.addAll(blob.getAtomicTransactions());
			}
			Logger.info("Found %d artificial blobs based on %d change sets using blobWindowSize=%d, minBlobSize=2, and maxBlobSizeWindow=2",
			            artificialBlobs.size(), changeSetsInBlobsSet.size(), this.untanglingControl.getBlobWindowSize());
		}
		
		if (this.untanglingControl.getMaxBlobSize() > 2) {
			/*
			 * now use the artificial blobs of size two to generate higher order blobs. For that purpose, use the
			 * combination HashSet and generate Collection<ChangeSet> using artificial blobs of order two. Then reuse
			 * BlobGenerator to generate higher order blobs.
			 */
			final Map<ChangeSet, Set<ArtificialBlob>> blobsPerChangeSet = new HashMap<>();
			for (final ArtificialBlob blob : artificialBlobs) {
				for (final ChangeSet t : blob.getAtomicTransactions()) {
					if (!blobsPerChangeSet.containsKey(t)) {
						blobsPerChangeSet.put(t, new HashSet<ArtificialBlob>());
					}
					blobsPerChangeSet.get(t).add(blob);
				}
			}
			
			final List<Set<ChangeSet>> possibleArtificialBlobCombinations = new ArrayList<>();
			for (final Entry<ChangeSet, List<ChangeSet>> entry : combinationCandidates.entrySet()) {
				final Set<ChangeSet> blobSet = new HashSet<>();
				final ChangeSet key = entry.getKey();
				final Set<ArtificialBlob> set = blobsPerChangeSet.get(key);
				if (set == null) {
					continue;
				}
				for (final ArtificialBlob blob : set) {
					blobSet.addAll(blob.getAtomicTransactions());
				}
				for (final ChangeSet s : entry.getValue()) {
					final Set<ArtificialBlob> tmpSet = blobsPerChangeSet.get(s);
					if (tmpSet == null) {
						continue;
					}
					for (final ArtificialBlob blob : tmpSet) {
						blobSet.addAll(blob.getAtomicTransactions());
					}
				}
				possibleArtificialBlobCombinations.add(blobSet);
			}
			if (this.untanglingControl.getMinBlobSize() > 2) {
				artificialBlobs.clear();
			}
			final ArtificialBlobGenerator pseudoBlobGenerator = new ArtificialBlobGenerator(
			                                                                                new CombineOperator<ChangeSet>() {
				                                                                                
				                                                                                @Override
				                                                                                public boolean canBeCombined(final ChangeSet t1,
				                                                                                                             final ChangeSet t2) {
					                                                                                return true;
				                                                                                }
			                                                                                });
			
			for (final Set<ChangeSet> blobSet : possibleArtificialBlobCombinations) {
				artificialBlobs.addAll(pseudoBlobGenerator.generateAll(blobSet,
				                                                       this.untanglingControl.getMinBlobSize(),
				                                                       this.untanglingControl.getMaxBlobSize()));
			}
			
		}
		
		int blobSetSize = artificialBlobs.size();
		
		final File outFile = this.untanglingControl.getOutputFile();
		try (FileWriter fileWriter = new FileWriter(outFile); BufferedWriter outWriter = new BufferedWriter(fileWriter);) {
			
			outWriter.write("DiffSize,#ChangeOperations,relativeDiffSize,lowestScore,JaccardIndex,TP,FP, Precision, Rel.FileError");
			outWriter.append(FileUtils.lineSeparator);
			
			if ((this.untanglingControl.getN() != -1l) && (this.untanglingControl.getN() < artificialBlobs.size())) {
				if (Logger.logInfo()) {
					Logger.info("Sampling %d artificial blobs to get %d random instances.", artificialBlobs.size(),
					            this.untanglingControl.getN());
				}
				final List<ArtificialBlob> selectedArtificialBlobs = new LinkedList<ArtificialBlob>();
				for (int i = 0; i < this.untanglingControl.getN(); ++i) {
					final int r = random.nextInt(artificialBlobs.size());
					selectedArtificialBlobs.add(artificialBlobs.remove(r));
				}
				artificialBlobs = selectedArtificialBlobs;
				blobSetSize = artificialBlobs.size();
			}
			
			final Set<ChangeSet> changeSetsInBlobs = new HashSet<ChangeSet>();
			for (final ArtificialBlob blob : artificialBlobs) {
				changeSetsInBlobs.addAll(blob.getAtomicTransactions());
			}
			
			final String experimentInfo = String.format("Generated %d artificial blobs based on %d change sets using blobWindowSize=%d, minBlobSize=%d, and maxBlobSizeWindow=%d",
			                                            blobSetSize, changeSetsInBlobs.size(),
			                                            this.untanglingControl.getBlobWindowSize(),
			                                            this.untanglingControl.getMinBlobSize(),
			                                            this.untanglingControl.getMaxBlobSize());
			
			if (Logger.logInfo()) {
				Logger.info(experimentInfo);
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
					linarRegressionAggregator.train(changeSetsInBlobs);
					this.aggregator = linarRegressionAggregator;
					break;
				case SVM:
					final SVMAggregation svmAggregator = SVMAggregation.createInstance(this);
					svmAggregator.train(changeSetsInBlobs);
					this.aggregator = svmAggregator;
					break;
				default:
					throw new UnrecoverableError("Unknown score aggregation mode found: "
					        + this.untanglingControl.getScoreMode());
			}
			
			// for each artificial blob
			final DescriptiveStatistics diffStat = new DescriptiveStatistics();
			final DescriptiveStatistics relativeDiffStat = new DescriptiveStatistics();
			
			final DescriptiveStatistics jaccardIndexStat = new DescriptiveStatistics();
			final DescriptiveStatistics precisionStat = new DescriptiveStatistics();
			
			final DescriptiveStatistics fileErrorStat = new DescriptiveStatistics();
			
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
					final UntanglingComparisonResult diffResult = comparePartitions(blob, partitions);
					diffStat.addValue(diffResult.getDiff());
					relativeDiffStat.addValue(diffResult.getRelativeDiff());
					jaccardIndexStat.addValue(diffResult.getMaxJaccarIndex());
					precisionStat.addValue(diffResult.getPrecision());
					fileErrorStat.addValue(diffResult.getFileError());
					try {
						outWriter.append(String.valueOf(diffResult.getDiff()));
						outWriter.append(",");
						outWriter.append(String.valueOf(blob.getAllChangeOperations().size()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getRelativeDiff()));
						outWriter.append(",");
						outWriter.append(String.valueOf(clustering.getLowestScore()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getMaxJaccarIndex()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getNumCorrectPartition()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getNumFalsePartition()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getPrecision()));
						outWriter.append(",");
						outWriter.append(String.valueOf(diffResult.getFileError()));
						outWriter.append(FileUtils.lineSeparator);
					} catch (final IOException e) {
						throw new UnrecoverableError(e.getMessage(), e);
					}
				}
				usedTransactions.addAll(blob.getTransactions());
				
			}
			
			// report the descriptive statistics about the partition scores.
			try {
				outWriter.append("Avg. MissRate:" + diffStat.getMean());
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Med. MissRate:" + diffStat.getPercentile(50));
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Avg. relative MissRate:" + relativeDiffStat.getMean());
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Med. relative MissRate:" + relativeDiffStat.getPercentile(50));
				outWriter.append(FileUtils.lineSeparator);
				
				outWriter.append("Med. JaccardIndex:" + jaccardIndexStat.getPercentile(50));
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Avg. JaccardIndex:" + jaccardIndexStat.getMean());
				outWriter.append(FileUtils.lineSeparator);
				
				outWriter.append("Med. Precision:" + precisionStat.getPercentile(50));
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Avg. Precision:" + precisionStat.getMean());
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Med. relative FileError:" + fileErrorStat.getPercentile(50));
				outWriter.append(FileUtils.lineSeparator);
				outWriter.append("Avg. relative FileError:" + fileErrorStat.getMean());
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
				
				outWriter.append(experimentInfo);
				
				outWriter.close();
			} catch (final IOException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			}
		} catch (final IOException e1) {
			throw new UnrecoverableError(e1);
		}
	}
}
