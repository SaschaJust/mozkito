package de.unisaarland.cs.st.reposuite.untangling.aggregation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
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
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.untangling.Untangling;
import de.unisaarland.cs.st.reposuite.untangling.blob.AtomicTransaction;

public class LinearRegressionAggregation extends ScoreAggregation<JavaChangeOperation> {
	
	private LinearRegression     model               = new LinearRegression();
	
	private boolean                trained             = false;
	private ArrayList<Attribute>   attributes          = new ArrayList<Attribute>();
	private final Attribute        confidenceAttribute = new Attribute("confidence");
	private int                    index;
	private double                 max_coefficient;
	private final Untangling       untangling;
	
	public LinearRegressionAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
		if (System.getProperty("linerRegressionModel") != null) {
			File serialFile = new File(System.getProperty("linerRegressionModel"));
			if (serialFile.exists()) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					this.model = (LinearRegression) in.readObject();
					this.trained = true;
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (IOException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
				
			}
		}
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
		for (int j = 0; j < values.size(); ++j) {
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
	public boolean train(final Set<AtomicTransaction> transactionSet) {
		
		if (trained) {
			return true;
		}
		
		Condition
		.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		
		List<AtomicTransaction> transactions = new ArrayList<AtomicTransaction>(transactionSet.size());
		transactions.addAll(transactionSet);
		
		//get random 30% of the transactions
		int numSamples = (int) (transactions.size() * 0.1);
		
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
		
		List<List<Double>> trainValues = new LinkedList<List<Double>>();
		
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
					values.add(1d);
					trainValues.add(values);
				}
			}
		}
		
		//generate the negative examples
		List<AtomicTransaction> selectedTransactionList = new LinkedList<AtomicTransaction>();
		selectedTransactionList.addAll(selectedTransactions.keySet());
		int k = trainValues.size();
		for (int i = 0; i < k; ++i) {
			int t1Index = -1;
			int t2Index = -1;
			while (t1Index == t2Index) {
				t1Index = Untangling.random.nextInt(selectedTransactionList.size());
				t2Index = Untangling.random.nextInt(selectedTransactionList.size());
			}
			
			//get two random atomic transactions from the selected transaction
			AtomicTransaction t1 = selectedTransactionList.get(t1Index);
			AtomicTransaction t2 = selectedTransactionList.get(t2Index);
			
			
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
			values.add(0d);
			trainValues.add(values);
		}
		
		List<String> scoreVisitorNames = untangling.getScoreVisitorNames();
		attributes = new ArrayList<Attribute>(scoreVisitorNames.size());
		
		// Declare attributes
		for (String scoreVisitorName : scoreVisitorNames) {
			attributes.add(new Attribute(scoreVisitorName));
		}
		attributes.add(confidenceAttribute);
		
		//create an empty training set
		Instances trainingSet = new Instances("TrainingSet", attributes, trainValues.size());
		trainingSet.setClassIndex(attributes.size() - 1);
		
		//set the training values within the weka training set
		for (int i = 0; i < trainValues.size(); ++i) {
			List<Double> instanceValues = trainValues.get(i);
			
			Condition.check(instanceValues.size() == attributes.size(),
					"InstanceValues and attributes must have equal dimensions: dum(instanceValues)="
							+ instanceValues.size() + ", dim(attributes)=" + attributes.size());
			
			Instance instance = new DenseInstance(instanceValues.size());
			for (int j = 0; j < instanceValues.size(); ++j) {
				instance.setValue(attributes.get(j), instanceValues.get(j));
			}
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
		
		//serialize model for later use
		File serialFile = new File("linearRegressionModel.ser");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
			out.writeObject(model);
			out.close();
			if (Logger.logInfo()) {
				Logger.info("Wrote trained model to file: " + serialFile.getAbsolutePath());
			}
		} catch (FileNotFoundException e1) {
			if (Logger.logError()) {
				Logger.error(e1.getMessage(), e1);
			}
		} catch (IOException e1) {
			if (Logger.logError()) {
				Logger.error(e1.getMessage(), e1);
			}
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
