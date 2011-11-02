package de.unisaarland.cs.st.moskito.untangling.aggregation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.untangling.Untangling;
import de.unisaarland.cs.st.moskito.untangling.blob.AtomicTransaction;

public class LinearRegressionAggregation extends UntanglingScoreAggregation {
	
	private static double        TRAIN_FRACTION = .5;
	private LinearRegression     model          = new LinearRegression();
	private boolean              trained        = false;
	private ArrayList<Attribute> attributes     = new ArrayList<Attribute>();
	private final Untangling     untangling;
	private Instances            trainingInstances;
	
	public LinearRegressionAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
		if (System.getProperty("linerRegressionModel") != null) {
			File serialFile = new File(System.getProperty("linerRegressionModel"));
			if (serialFile.exists()) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					Object[] objects = (Object[]) in.readObject();
					this.model = (LinearRegression) objects[0];
					this.trainingInstances = (Instances) objects[1];
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
	 * @see
	 * de.unisaarland.cs.st.moskito.untangling.ConfidenceAggregation#aggregate
	 * (java.util.List)
	 */
	@Override
	@NoneNull
	public double aggregate(final List<Double> values) {
		Condition.check(this.trained, "You must train a model before using it,");
		if (!this.attributes.isEmpty()) {
			Condition.check((values.size() + 1) == this.attributes.size(),
			                "The given set of values differ from the trained attribute's dimension");
		}
		
		Instance instance = new DenseInstance(values.size() + 1);
		for (int j = 0; j < values.size(); ++j) {
			instance.setValue(this.trainingInstances.attribute(j), values.get(j));
		}
		instance.setDataset(this.trainingInstances);
		try {
			return this.model.distributionForInstance(instance)[0];
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
		sb.append("Type: " + LinearRegressionAggregation.class.getSimpleName());
		sb.append(FileUtils.lineSeparator);
		sb.append(this.model.toString());
		return sb.toString();
	}
	
	/**
	 * Train the underlying linear regression.
	 * 
	 * @return true, if training was completed successful. False otherwise.
	 */
	@NoneNull
	public boolean train(final Collection<AtomicTransaction> transactionSet) {
		
		if (this.trained) {
			return true;
		}
		
		Condition.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		Map<SampleType, List<List<Double>>> samples = super.getSamples(transactionSet, TRAIN_FRACTION, this.untangling);
		List<List<Double>> trainValues = new LinkedList<List<Double>>();
		for (List<Double> value : samples.get(SampleType.POSITIVE)) {
			List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(1d);
			trainValues.add(valueCopy);
		}
		for (List<Double> value : samples.get(SampleType.NEGATIVE)) {
			List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(0d);
			trainValues.add(valueCopy);
		}
		
		List<String> scoreVisitorNames = this.untangling.getScoreVisitorNames();
		this.attributes = new ArrayList<Attribute>(scoreVisitorNames.size());
		
		// Declare attributes
		for (String scoreVisitorName : scoreVisitorNames) {
			this.attributes.add(new Attribute(scoreVisitorName));
		}
		this.attributes.add(new Attribute("confidence"));
		
		// create an empty training set
		this.trainingInstances = new Instances("TrainingSet", this.attributes, trainValues.size());
		this.trainingInstances.setClassIndex(this.attributes.size() - 1);
		
		// set the training values within the weka training set
		for (int i = 0; i < trainValues.size(); ++i) {
			List<Double> instanceValues = trainValues.get(i);
			
			Condition.check(instanceValues.size() == this.attributes.size(),
			                "InstanceValues and attributes must have equal dimensions: dum(instanceValues)="
			                        + instanceValues.size() + ", dim(attributes)=" + this.attributes.size());
			
			Instance instance = new DenseInstance(instanceValues.size());
			for (int j = 0; j < instanceValues.size(); ++j) {
				instance.setValue(this.attributes.get(j), instanceValues.get(j));
			}
			// add the instance
			this.trainingInstances.add(instance);
		}
		try {
			this.model.buildClassifier(this.trainingInstances);
		} catch (Exception e) {
			throw new UnrecoverableError(e.getMessage());
		}
		
		try {
			File instancesFile = new File("linearRegressionModel_Instances.arff");
			BufferedWriter writer = new BufferedWriter(new FileWriter(instancesFile));
			writer.write(this.trainingInstances.toString());
			writer.close();
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage());
		}
		
		// serialize model for later use
		File serialFile = new File("linearRegressionModel.ser");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
			
			Object[] toWrite = new Object[] { this.model, this.trainingInstances };
			out.writeObject(toWrite);
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
		
		this.trained = true;
		return this.trained;
	}
	
}
