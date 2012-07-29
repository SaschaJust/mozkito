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

import hr.irb.fastRandomForest.FastRandomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.untangling.Untangling;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class LinearRegressionAggregation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class RandomForestAggregation extends UntanglingScoreAggregation {
	
	/** The TRAI n_ fraction. */
	private static double          TRAIN_FRACTION = .5;
	
	/** The model. */
	private final FastRandomForest model          = new FastRandomForest();
	
	/** The trained. */
	private boolean                trained        = false;
	
	/** The attributes. */
	private ArrayList<Attribute>   attributes     = new ArrayList<Attribute>();
	
	/** The untangling. */
	private final Untangling       untangling;
	
	/** The training instances. */
	private Instances              trainingInstances;
	
	/**
	 * Instantiates a new linear regression aggregation.
	 * 
	 * @param untangling
	 *            the untangling
	 */
	public RandomForestAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.ConfidenceAggregation#aggregate (java.util.List)
	 */
	@Override
	@NoneNull
	public double aggregate(final List<Double> values) {
		Condition.check(this.trained, "You must train a model before using it,");
		if (!this.attributes.isEmpty()) {
			Condition.check((values.size() + 1) == this.attributes.size(),
			                "The given set of values differ from the trained attribute's dimension");
		}
		
		final Instance instance = new DenseInstance(values.size() + 1);
		for (int j = 0; j < values.size(); ++j) {
			instance.setValue(this.trainingInstances.attribute(j), values.get(j));
		}
		instance.setDataset(this.trainingInstances);
		try {
			return this.model.distributionForInstance(instance)[0];
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return MultilevelClustering.IGNORE_SCORE;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.ScoreAggregation#getInfo()
	 */
	@Override
	public String getInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Type: " + RandomForestAggregation.class.getSimpleName());
		sb.append(FileUtils.lineSeparator);
		sb.append(this.model.toString());
		sb.append(FileUtils.lineSeparator);
		sb.append("Feature importance:");
		sb.append(FileUtils.lineSeparator);
		final double[] featureImportances = this.model.getFeatureImportances();
		for (int i = 0; i < this.attributes.size(); ++i) {
			final Attribute attr = this.attributes.get(i);
			sb.append(attr.name());
			sb.append(": ");
			sb.append(featureImportances[i]);
			sb.append(FileUtils.lineSeparator);
		}
		
		return sb.toString();
	}
	
	/**
	 * Train the underlying linear regression.
	 * 
	 * @param transactionSet
	 *            the transaction set
	 * @return true, if training was completed successful. False otherwise.
	 */
	@NoneNull
	public boolean train(final Collection<ChangeSet> transactionSet) {
		
		if (this.trained) {
			return true;
		}
		
		Condition.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		final Map<SampleType, List<List<Double>>> samples = super.getSamples(transactionSet, TRAIN_FRACTION,
		                                                                     this.untangling);
		final List<List<Double>> trainValues = new LinkedList<List<Double>>();
		for (final List<Double> value : samples.get(SampleType.POSITIVE)) {
			final List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(1d);
			trainValues.add(valueCopy);
		}
		for (final List<Double> value : samples.get(SampleType.NEGATIVE)) {
			final List<Double> valueCopy = new ArrayList<Double>(value);
			valueCopy.add(0d);
			trainValues.add(valueCopy);
		}
		
		final List<String> scoreVisitorNames = this.untangling.getScoreVisitorNames();
		this.attributes = new ArrayList<Attribute>(scoreVisitorNames.size());
		
		// Declare attributes
		for (final String scoreVisitorName : scoreVisitorNames) {
			this.attributes.add(new Attribute(scoreVisitorName));
		}
		this.attributes.add(new Attribute("confidence"));
		
		// create an empty training set
		this.trainingInstances = new Instances("TrainingSet", this.attributes, trainValues.size());
		this.trainingInstances.setClassIndex(this.attributes.size() - 1);
		
		// set the training values within the weka training set
		for (int i = 0; i < trainValues.size(); ++i) {
			final List<Double> instanceValues = trainValues.get(i);
			
			Condition.check(instanceValues.size() == this.attributes.size(),
			                "InstanceValues and attributes must have equal dimensions: dum(instanceValues)="
			                        + instanceValues.size() + ", dim(attributes)=" + this.attributes.size());
			
			final Instance instance = new DenseInstance(instanceValues.size());
			for (int j = 0; j < instanceValues.size(); ++j) {
				instance.setValue(this.attributes.get(j), instanceValues.get(j));
			}
			// add the instance
			this.trainingInstances.add(instance);
		}
		try {
			this.model.buildClassifier(this.trainingInstances);
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(this.trainingInstances.toSummaryString());
			}
			throw new UnrecoverableError(e.getMessage());
		}
		
		// serialize model for later use
		final File serialFile = new File("radnomForestModel.ser");
		try {
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
			
			final Object[] toWrite = new Object[] { this.model, this.trainingInstances };
			out.writeObject(toWrite);
			out.close();
			if (Logger.logInfo()) {
				Logger.info("Wrote trained model to file: " + serialFile.getAbsolutePath());
			}
		} catch (final FileNotFoundException e1) {
			if (Logger.logError()) {
				Logger.error(e1);
			}
		} catch (final IOException e1) {
			if (Logger.logError()) {
				Logger.error(e1);
			}
		}
		
		this.trained = true;
		return this.trained;
	}
}
