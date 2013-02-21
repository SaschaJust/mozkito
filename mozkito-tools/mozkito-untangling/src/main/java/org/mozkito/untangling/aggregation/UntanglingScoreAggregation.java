/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.untangling.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.clustering.ScoreAggregation;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.untangling.Untangling;
import org.mozkito.untangling.blob.ChangeOperationSet;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class UntanglingScoreAggregation.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class UntanglingScoreAggregation extends ScoreAggregation<JavaChangeOperation> {
	
	/**
	 * The Enum SampleType.
	 * 
	 * @author Kim Herzig <herzig@mozkito.org>
	 */
	public enum SampleType {
		
		/** The POSITIVE. */
		POSITIVE,
		/** The NEGATIVE. */
		NEGATIVE;
	}
	
	/**
	 * Gets the samples.
	 * 
	 * @param transactionSet
	 *            the transaction set
	 * @param trainFraction
	 *            the train fraction
	 * @param untangling
	 *            the untangling
	 * @return the samples
	 */
	public Map<SampleType, List<List<Double>>> getSamples(final Collection<ChangeOperationSet> transactionSet,
	                                                      final double trainFraction,
	                                                      final Untangling untangling) {
		Condition.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		final List<ChangeOperationSet> transactions = new ArrayList<ChangeOperationSet>(transactionSet.size());
		transactions.addAll(transactionSet);
		
		if (Logger.logDebug()) {
			Logger.debug("Fetching training samples: %s or %s change sets.", String.valueOf(trainFraction),
			             String.valueOf(transactions.size()));
		}
		
		int numSamples = (int) (transactions.size() * trainFraction);
		
		if (Logger.logInfo()) {
			Logger.info("Using " + numSamples + " samples as positive training set.");
		}
		
		final Map<ChangeOperationSet, Set<JavaChangeOperation>> selectedTransactions = new HashMap<ChangeOperationSet, Set<JavaChangeOperation>>();
		for (int i = 0; i < numSamples; ++i) {
			final int r = Untangling.random.nextInt(transactions.size());
			
			final ChangeOperationSet t = transactions.get(r);
			final Set<JavaChangeOperation> changeOperations = t.getChangeOperation(JavaMethodDefinition.class);
			
			if (changeOperations.size() < 2) {
				numSamples = Math.min(numSamples + 1, transactions.size());
			}
			selectedTransactions.put(t, changeOperations);
		}
		
		final List<List<Double>> positiveValues = new LinkedList<List<Double>>();
		
		// generate the positive examples
		if (Logger.logDebug()) {
			Logger.debug("Creating positive samples.");
		}
		
		for (final Entry<ChangeOperationSet, Set<JavaChangeOperation>> e : selectedTransactions.entrySet()) {
			final ChangeOperationSet t = e.getKey();
			final JavaChangeOperation[] operationArray = e.getValue().toArray(new JavaChangeOperation[e.getValue()
			                                                                                           .size()]);
			final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = untangling.generateScoreVisitors(t.getChangeSet());
			for (int i = 0; i < operationArray.length; ++i) {
				for (int j = i + 1; j < operationArray.length; ++j) {
					
					final List<Double> values = new ArrayList<Double>(scoreVisitors.size());
					for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
						values.add(v.getScore(operationArray[i], operationArray[j]));
					}
					positiveValues.add(values);
				}
			}
			for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
				v.close();
			}
		}
		
		// generate the negative examples
		if (Logger.logDebug()) {
			Logger.debug("Creating negative samples.");
		}
		
		final List<List<Double>> negativeValues = new LinkedList<List<Double>>();
		
		final List<Tuple<Tuple<JavaChangeOperation, JavaChangeOperation>, ChangeSet>> negativePool = new LinkedList<>();
		
		for (final Entry<ChangeOperationSet, Set<JavaChangeOperation>> entry : selectedTransactions.entrySet()) {
			final List<JavaChangeOperation> opList = new ArrayList<>(entry.getValue().size());
			opList.addAll(entry.getValue());
			for (int i = 0; i < opList.size(); ++i) {
				for (int j = i + 1; j < opList.size(); ++j) {
					final Tuple<JavaChangeOperation, JavaChangeOperation> negInnerTuple = new Tuple<JavaChangeOperation, JavaChangeOperation>(
					                                                                                                                          opList.get(i),
					                                                                                                                          opList.get(j));
					negativePool.add(new Tuple<Tuple<JavaChangeOperation, JavaChangeOperation>, ChangeSet>(
					                                                                                       negInnerTuple,
					                                                                                       entry.getKey()
					                                                                                            .getChangeSet()));
				}
			}
		}
		
		final int k = positiveValues.size();
		final long factorial = ((k - 1) * k) / 2;
		final Set<Integer> seenSamples = new HashSet<>();
		if (factorial < negativePool.size()) {
			for (int i = 0; i < factorial; ++i) {
				// select random negative sample
				int sampleIndex = Untangling.random.nextInt(negativePool.size());
				while (seenSamples.contains(sampleIndex)) {
					sampleIndex = Untangling.random.nextInt(negativePool.size());
				}
				seenSamples.add(sampleIndex);
			}
		}
		
		List<Tuple<Tuple<JavaChangeOperation, JavaChangeOperation>, ChangeSet>> sampledNegativePool = new LinkedList<>();
		if (!seenSamples.isEmpty()) {
			
			for (final int i : seenSamples) {
				sampledNegativePool.add(negativePool.get(i));
			}
		} else {
			sampledNegativePool = negativePool;
		}
		
		for (final Tuple<Tuple<JavaChangeOperation, JavaChangeOperation>, ChangeSet> sample : sampledNegativePool) {
			final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = untangling.generateScoreVisitors(sample.getSecond());
			final List<Double> values = new ArrayList<Double>(scoreVisitors.size());
			for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
				final double value = v.getScore(sample.getFirst().getFirst(), sample.getFirst().getSecond());
				values.add(value);
			}
			negativeValues.add(values);
			for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
				v.close();
			}
		}
		
		final Map<SampleType, List<List<Double>>> result = new HashMap<SampleType, List<List<Double>>>();
		
		result.put(SampleType.POSITIVE, positiveValues);
		result.put(SampleType.NEGATIVE, negativeValues);
		
		return result;
	}
}
