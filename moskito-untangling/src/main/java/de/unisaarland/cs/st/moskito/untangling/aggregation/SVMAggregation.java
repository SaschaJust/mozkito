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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.untangling.Untangling;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class SVMAggregation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SVMAggregation extends UntanglingScoreAggregation implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = 5743363755550937828L;
	
	/** The Constant TRAIN_FRACTION. */
	private static final double TRAIN_FRACTION   = .5;
	
	/**
	 * Creates the instance.
	 * 
	 * @param untangling
	 *            the untangling
	 * @return the sVM aggregation
	 */
	public static SVMAggregation createInstance(final Untangling untangling) {
		SVMAggregation result = null;
		if (System.getProperty("svmModel") != null) {
			final File serialFile = new File(System.getProperty("svmModel"));
			if (serialFile.exists()) {
				try {
					final ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					result = (SVMAggregation) in.readObject();
					in.close();
				} catch (final FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
			if (Logger.logWarn()) {
				Logger.warn("Could not deserialize SVMAggregation model. Creating new one.");
			}
		}
		if (result == null) {
			result = new SVMAggregation(untangling);
		}
		return result;
	}
	
	/** The trained. */
	private boolean          trained = false;
	
	/** The untangling. */
	private final Untangling untangling;
	
	/** The model. */
	private svm_model        model;
	
	/**
	 * Instantiates a new sVM aggregation.
	 * 
	 * @param untangling
	 *            the untangling
	 */
	protected SVMAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.ScoreAggregation#aggregate(java.util.List)
	 */
	@Override
	public double aggregate(final List<Double> values) {
		Condition.check(this.trained, "You must train a model before using it,");
		
		final svm_node x[] = new svm_node[values.size()];
		for (int i = 0; i < values.size(); ++i) {
			x[i] = new svm_node();
			x[i].index = i + 1;
			x[i].value = values.get(i);
		}
		
		return svm.svm_predict(this.model, x);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.ScoreAggregation#getInfo()
	 */
	@Override
	public String getInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Type: " + SVMAggregation.class.getSimpleName());
		return sb.toString();
	}
	
	/**
	 * Train.
	 * 
	 * @param transactionSet
	 *            the transaction set
	 * @return true, if successful
	 */
	public boolean train(final Collection<ChangeSet> transactionSet) {
		
		if (this.trained) {
			return true;
		}
		
		Condition.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		final Map<SampleType, List<List<Double>>> samples = super.getSamples(transactionSet, TRAIN_FRACTION,
		                                                                     this.untangling);
		
		final List<List<Double>> positiveSamples = samples.get(SampleType.POSITIVE);
		final List<List<Double>> negativeSamples = samples.get(SampleType.NEGATIVE);
		
		final svm_problem prob = new svm_problem();
		prob.l = positiveSamples.size() + negativeSamples.size();
		prob.y = new double[prob.l];
		prob.x = new svm_node[prob.l][positiveSamples.get(0).size()];
		
		for (int i = 0; i < positiveSamples.size(); ++i) {
			final List<Double> list = positiveSamples.get(i);
			for (int j = 0; j < list.size(); ++j) {
				prob.x[i][j] = new svm_node();
				prob.x[i][j].index = j + 1;
				prob.x[i][j].value = list.get(j);
				prob.y[i] = 1d;
			}
		}
		
		for (int i = 0; i < negativeSamples.size(); ++i) {
			final List<Double> list = negativeSamples.get(i);
			for (int j = 0; j < list.size(); ++j) {
				prob.x[i + positiveSamples.size()][j] = new svm_node();
				prob.x[i + positiveSamples.size()][j].index = j + 1;
				prob.x[i + positiveSamples.size()][j].value = list.get(j);
				prob.y[i + positiveSamples.size()] = 0d;
			}
		}
		
		final svm_parameter param = new svm_parameter();
		param.svm_type = 0;
		param.kernel_type = 2;
		param.degree = 3;
		param.gamma = 1 / positiveSamples.get(0).size();
		param.coef0 = 0d;
		param.nu = 0.5d;
		param.cache_size = 100d;
		param.C = 1d;
		param.eps = 0.001d;
		param.p = 0.10000000000000001d;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		
		this.model = svm.svm_train(prob, param);
		
		this.trained = true;
		return this.trained;
		
	}
	
}
