package de.unisaarland.cs.st.reposuite.untangling.aggregation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.clustering.ScoreAggregation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.untangling.Untangling;
import de.unisaarland.cs.st.reposuite.untangling.blob.BlobTransaction;

public class LinearRegressionAggregation extends ScoreAggregation<JavaChangeOperation> {
	
	private final LinearRegression model               = new LinearRegression();
	
	private boolean                trained             = false;
	private ArrayList<Attribute>   attributes          = new ArrayList<Attribute>();
	private final Attribute        confidenceAttribute = new Attribute("confidence");
	private int                    index;
	private double max_coefficient;
	
	public LinearRegressionAggregation(final List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors) {
		super(scoreVisitors);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.untangling.ConfidenceAggregation#aggregate
	 * (java.util.List)
	 */
	@Override
	@NoneNull
	public double aggregate(final List<Double> values) {
		Condition.check(trained, "You must train a model before using it,");
		Condition.check((values.size() + 1) == attributes.size(),
				"The given set of values differ from the trained attribute's dimension");
		
		Instance instance = new DenseInstance(values.size());
		for (int j = 0; j < (values.size() - 1); ++j) {
			instance.setValue(attributes.get(j), values.get(j));
		}
		try {
			double r = model.classifyInstance(instance);
			r += max_coefficient * values.get(index);
			return r;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return MultilevelClustering.IGNORE_SCORE;
		}
	}
	
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < model.coefficients().length; ++i) {
			sb.append(attributes.get(i).name());
			sb.append(": ");
			sb.append(model.coefficients()[i]);
			sb.append(FileUtils.lineSeparator);
		}
		return sb.toString();
	}
	
	/**
	 * Train the underlying linear regression.
	 *
	 * @return true, if training was completed successful. False otherwise.
	 */
	@NoneNull
	public boolean train(final Set<BlobTransaction> transactionSet) {
		
		List<BlobTransaction> transactions = new ArrayList<BlobTransaction>(transactionSet.size());
		transactions.addAll(transactionSet);
		
		//get random 30% of the transactions
		int numSamples = (int) (transactions.size() * 0.3);
		
		List<BlobTransaction> selectedTransactions = new ArrayList<BlobTransaction>(numSamples);
		for (int i = 0; i < numSamples; ++i) {
			int r = Untangling.random.nextInt(transactions.size());
			selectedTransactions.add(transactions.get(r));
		}
		
		
		//generate the positive examples
		Set<Tuple<JavaChangeOperation, JavaChangeOperation>> operationPairs = new HashSet<Tuple<JavaChangeOperation, JavaChangeOperation>>();
		for (BlobTransaction t : selectedTransactions) {
			JavaChangeOperation[] operationArray = t.getOperations().toArray(
					new JavaChangeOperation[t.getOperations().size()]);
			for (int i = 0; i < operationArray.length; ++i) {
				for (int j = i + 1; j < operationArray.length; ++j) {
					operationPairs.add(new Tuple<JavaChangeOperation, JavaChangeOperation>(operationArray[i],
							operationArray[j]));
				}
			}
		}
		
		
		List<Double> responseValues = new LinkedList<Double>();
		List<List<Double>> trainValues = new LinkedList<List<Double>>();
		for (Tuple<JavaChangeOperation, JavaChangeOperation> pair : operationPairs) {
			List<Double> values = new ArrayList<Double>(super.getScoreVisitors().size());
			for (MultilevelClusteringScoreVisitor<JavaChangeOperation> v : super.getScoreVisitors()) {
				values.add(v.getScore(pair.getFirst(), pair.getSecond()));
			}
			trainValues.add(values);
			responseValues.add(1d);
		}
		
		//generate the negative examples
		for (int i = 0; i < trainValues.size(); ++i) {
			int t1Index = -1;
			int t2Index = -1;
			while (t1Index == t2Index) {
				t1Index = Untangling.random.nextInt(transactions.size());
				t2Index = Untangling.random.nextInt(transactions.size());
			}
			BlobTransaction t1 = transactions.get(t1Index);
			BlobTransaction t2 = transactions.get(t2Index);
			
			List<JavaChangeOperation> t1Ops = t1.getOperations();
			List<JavaChangeOperation> t2Ops = t2.getOperations();
			
			int t1OpIndex = Untangling.random.nextInt(t1Ops.size());
			int t2OpIndex = Untangling.random.nextInt(t2Ops.size());
			
			JavaChangeOperation op1 = t1Ops.get(t1OpIndex);
			JavaChangeOperation op2 = t2Ops.get(t2OpIndex);
			
			if (op1.equals(op2)) {
				--i;
				continue;
			}
			
			List<Double> values = new ArrayList<Double>(super.getScoreVisitors().size());
			for (MultilevelClusteringScoreVisitor<JavaChangeOperation> v : super.getScoreVisitors()) {
				values.add(v.getScore(op1, op2));
			}
			trainValues.add(values);
			responseValues.add(0d);
		}
		
		///############################
		
		attributes = new ArrayList<Attribute>(super.getScoreVisitors().size());
		// Declare attributes
		for (MultilevelClusteringScoreVisitor<JavaChangeOperation> scoreVisitor : super.getScoreVisitors()) {
			attributes.add(new Attribute(scoreVisitor.getClass().getSimpleName()));
		}
		attributes.add(confidenceAttribute);
		
		//create an empty training set
		Instances trainingSet = new Instances("TrainingSet", attributes, responseValues.size());
		trainingSet.setClassIndex(attributes.size());
		
		//set the training values within the weka training set
		for (int i = 0; i < trainValues.size(); ++i) {
			List<Double> instanceValues = trainValues.get(i);
			Instance instance = new DenseInstance(instanceValues.size());
			for (int j = 0; j < (instanceValues.size() - 1); ++j) {
				instance.setValue(attributes.get(j), instanceValues.get(j));
			}
			instance.setValue(confidenceAttribute, instanceValues.get(i));
			// add the instance
			trainingSet.add(instance);
		}
		try {
			model.buildClassifier(trainingSet);
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		
		int counter = 0;
		index = 0;
		max_coefficient = 0d;
		for (double c : model.coefficients()) {
			if (c > max_coefficient) {
				index = counter;
				max_coefficient = c;
			}
			++counter;
		}
		
		trained = true;
		return trained;
	}
	
}
