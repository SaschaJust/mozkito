package de.unisaarland.cs.st.moskito.untangling.aggregation;

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
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.clustering.ScoreAggregation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.untangling.Untangling;
import de.unisaarland.cs.st.moskito.untangling.blob.AtomicTransaction;


public abstract class UntanglingScoreAggregation extends ScoreAggregation<JavaChangeOperation> {
	
	public enum SampleType {
		POSITIVE, NEGATIVE;
	}
	
	public Map<SampleType, List<List<Double>>> getSamples(final Collection<AtomicTransaction> transactionSet,
			final double trainFraction, final Untangling untangling) {
		Condition
		.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		List<AtomicTransaction> transactions = new ArrayList<AtomicTransaction>(transactionSet.size());
		transactions.addAll(transactionSet);
		
		//get random 30% of the transactions
		int numSamples = (int) (transactions.size() * trainFraction);
		
		if (Logger.logInfo()) {
			Logger.info("Using " + numSamples + " samples as positive training set.");
		}
		
		Map<AtomicTransaction, Set<JavaChangeOperation>> selectedTransactions = new HashMap<AtomicTransaction, Set<JavaChangeOperation>>();
		for (int i = 0; i < numSamples; ++i) {
			int r = Untangling.random.nextInt(transactions.size());
			
			AtomicTransaction t = transactions.get(r);
			Set<JavaChangeOperation> changeOperations = t.getChangeOperation(JavaMethodDefinition.class);
			
			if (changeOperations.size() < 2) {
				numSamples = Math.min(numSamples + 1, transactions.size());
			}
			selectedTransactions.put(t, changeOperations);
		}
		
		List<List<Double>> positiveValues = new LinkedList<List<Double>>();
		
		//generate the positive examples
		for (Entry<AtomicTransaction, Set<JavaChangeOperation>> e : selectedTransactions.entrySet()) {
			AtomicTransaction t = e.getKey();
			JavaChangeOperation[] operationArray = e.getValue().toArray(new JavaChangeOperation[e.getValue().size()]);
			for (int i = 0; i < operationArray.length; ++i) {
				for (int j = i + 1; j < operationArray.length; ++j) {
					List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = untangling
							.generateScoreVisitors(t.getTransaction());
					List<Double> values = new ArrayList<Double>(scoreVisitors.size() + 1);
					for (MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
						values.add(v.getScore(operationArray[i], operationArray[j]));
					}
					positiveValues.add(values);
				}
			}
		}
		
		Set<Tuple<Integer, Integer>> seenCombinations = new HashSet<Tuple<Integer, Integer>>();
		
		//generate the negative examples
		List<List<Double>> negativeValues = new LinkedList<List<Double>>();
		List<AtomicTransaction> selectedTransactionList = new LinkedList<AtomicTransaction>();
		selectedTransactionList.addAll(selectedTransactions.keySet());
		int k = positiveValues.size();
		long factorial = ((k - 1) * k) / 2;
		for (int i = 0; (i < k) && (seenCombinations.size() < factorial); ++i) {
			int t1Index = -1;
			int t2Index = -1;
			while (t1Index == t2Index) {
				t1Index = Untangling.random.nextInt(selectedTransactionList.size());
				t2Index = Untangling.random.nextInt(selectedTransactionList.size());
			}
			
			//get two random atomic transactions from the selected transaction
			AtomicTransaction t1 = selectedTransactionList.get(t1Index);
			AtomicTransaction t2 = selectedTransactionList.get(t2Index);
			
			if (t1Index < t2Index) {
				seenCombinations.add(new Tuple<Integer, Integer>(t1Index, t2Index));
			} else {
				seenCombinations.add(new Tuple<Integer, Integer>(t2Index, t1Index));
			}
			
			List<JavaChangeOperation> t1Ops = new LinkedList<JavaChangeOperation>();
			t1Ops.addAll(selectedTransactions.get(t1));
			
			List<JavaChangeOperation> t2Ops = new LinkedList<JavaChangeOperation>();
			t2Ops.addAll(selectedTransactions.get(t2));
			
			int t1OpIndex = Untangling.random.nextInt(t1Ops.size());
			int t2OpIndex = Untangling.random.nextInt(t2Ops.size());
			
			JavaChangeOperation op1 = t1Ops.get(t1OpIndex);
			JavaChangeOperation op2 = t2Ops.get(t2OpIndex);
			
			if (op1.equals(op2)) {
				--i;
				continue;
			}
			
			List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = null;
			
			if (t1.getTransaction().compareTo(t2.getTransaction()) > 0) {
				scoreVisitors = untangling.generateScoreVisitors(t1.getTransaction());
			} else {
				scoreVisitors = untangling.generateScoreVisitors(t2.getTransaction());
			}
			
			List<Double> values = new ArrayList<Double>(scoreVisitors.size() + 1);
			for (MultilevelClusteringScoreVisitor<JavaChangeOperation> v : scoreVisitors) {
				double value = v.getScore(op1, op2);
				values.add(value);
			}
			negativeValues.add(values);
		}
		
		Map<SampleType, List<List<Double>>> result = new HashMap<SampleType, List<List<Double>>>();
		
		result.put(SampleType.POSITIVE, positiveValues);
		result.put(SampleType.NEGATIVE, positiveValues);
		
		return result;
	}
}
