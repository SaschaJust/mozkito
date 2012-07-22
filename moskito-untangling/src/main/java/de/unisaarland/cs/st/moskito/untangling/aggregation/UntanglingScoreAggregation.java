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

package de.unisaarland.cs.st.moskito.untangling.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.clustering.ScoreAggregation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.untangling.Untangling;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class UntanglingScoreAggregation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class UntanglingScoreAggregation extends ScoreAggregation<JavaChangeOperation> {
	
	/**
	 * The Enum SampleType.
	 * 
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
	public Map<SampleType, List<List<Double>>> getSamples(final Collection<ChangeSet> transactionSet,
	                                                      final double trainFraction,
	                                                      final Untangling untangling) {
		Condition.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		final List<ChangeSet> transactions = new ArrayList<ChangeSet>(transactionSet.size());
		transactions.addAll(transactionSet);
		
		if (Logger.logDebug()) {
			Logger.debug("Fetching training samples: %s or %s change sets.", String.valueOf(trainFraction),
			             String.valueOf(transactions.size()));
		}
		
		int numSamples = (int) (transactions.size() * trainFraction);
		
		if (Logger.logInfo()) {
			Logger.info("Using " + numSamples + " samples as positive training set.");
		}
		
		final Map<ChangeSet, Set<JavaChangeOperation>> selectedTransactions = new HashMap<ChangeSet, Set<JavaChangeOperation>>();
		for (int i = 0; i < numSamples; ++i) {
			final int r = Untangling.random.nextInt(transactions.size());
			
			final ChangeSet t = transactions.get(r);
			final Set<JavaChangeOperation> changeOperations = t.getChangeOperation(JavaMethodDefinition.class);
			
			if (changeOperations.size() < 2) {
				numSamples = Math.min(numSamples + 1, transactions.size());
			}
			selectedTransactions.put(t, changeOperations);
		}
		
		final List<List<Double>> positiveValues = new LinkedList<List<Double>>();
		
		// generate the positive examples
		for (final Entry<ChangeSet, Set<JavaChangeOperation>> e : selectedTransactions.entrySet()) {
			final ChangeSet t = e.getKey();
			final JavaChangeOperation[] operationArray = e.getValue().toArray(new JavaChangeOperation[e.getValue()
			                                                                                           .size()]);
			for (int i = 0; i < operationArray.length; ++i) {
				for (int j = i + 1; j < operationArray.length; ++j) {
					final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = untangling.generateScoreVisitors(t.getTransaction());
					final List<Double> values = new ArrayList<Double>(scoreVisitors.size() + 1);
					for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
						values.add(v.getScore(operationArray[i], operationArray[j]));
					}
					positiveValues.add(values);
				}
			}
		}
		
		final Set<Tuple<Integer, Integer>> seenCombinations = new HashSet<Tuple<Integer, Integer>>();
		
		final Comparator<? super RCSTransaction> transactionComparator = new TransactionSet(TransactionSetOrder.ASC).comparator();
		
		// generate the negative examples
		final List<List<Double>> negativeValues = new LinkedList<List<Double>>();
		final List<ChangeSet> selectedTransactionList = new LinkedList<ChangeSet>();
		selectedTransactionList.addAll(selectedTransactions.keySet());
		final int k = positiveValues.size();
		final long factorial = ((k - 1) * k) / 2;
		for (int i = 0; (i < k) && (seenCombinations.size() < factorial); ++i) {
			int t1Index = -1;
			int t2Index = -1;
			while (t1Index == t2Index) {
				t1Index = Untangling.random.nextInt(selectedTransactionList.size());
				t2Index = Untangling.random.nextInt(selectedTransactionList.size());
			}
			
			// get two random atomic transactions from the selected transaction
			final ChangeSet t1 = selectedTransactionList.get(t1Index);
			final ChangeSet t2 = selectedTransactionList.get(t2Index);
			
			if (t1Index < t2Index) {
				seenCombinations.add(new Tuple<Integer, Integer>(t1Index, t2Index));
			} else {
				seenCombinations.add(new Tuple<Integer, Integer>(t2Index, t1Index));
			}
			
			final List<JavaChangeOperation> t1Ops = new LinkedList<JavaChangeOperation>();
			t1Ops.addAll(selectedTransactions.get(t1));
			
			final List<JavaChangeOperation> t2Ops = new LinkedList<JavaChangeOperation>();
			t2Ops.addAll(selectedTransactions.get(t2));
			
			final int t1OpIndex = Untangling.random.nextInt(t1Ops.size());
			final int t2OpIndex = Untangling.random.nextInt(t2Ops.size());
			
			final JavaChangeOperation op1 = t1Ops.get(t1OpIndex);
			final JavaChangeOperation op2 = t2Ops.get(t2OpIndex);
			
			if (op1.equals(op2)) {
				--i;
				continue;
			}
			
			List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = null;
			
			if (transactionComparator.compare(t1.getTransaction(), t2.getTransaction()) > 0) {
				scoreVisitors = untangling.generateScoreVisitors(t1.getTransaction());
			} else {
				scoreVisitors = untangling.generateScoreVisitors(t2.getTransaction());
			}
			
			final List<Double> values = new ArrayList<Double>(scoreVisitors.size() + 1);
			for (final MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
				final double value = v.getScore(op1, op2);
				values.add(value);
			}
			negativeValues.add(values);
		}
		
		final Map<SampleType, List<List<Double>>> result = new HashMap<SampleType, List<List<Double>>>();
		
		result.put(SampleType.POSITIVE, positiveValues);
		result.put(SampleType.NEGATIVE, positiveValues);
		
		return result;
	}
}
