/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
import de.unisaarland.cs.st.moskito.untangling.blob.AtomicTransaction;


public class SVMAggregation extends UntanglingScoreAggregation implements Serializable {
	
	/**
	 * 
	 */
	private static final long   serialVersionUID = 5743363755550937828L;
	
	private static final double  TRAIN_FRACTION      = .5;
	
	public static SVMAggregation createInstance(final Untangling untangling) {
		SVMAggregation result = null;
		if (System.getProperty("svmModel") != null) {
			File serialFile = new File(System.getProperty("svmModel"));
			if (serialFile.exists()) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					result = (SVMAggregation) in.readObject();
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
			if (Logger.logWarn()) {
				Logger.warn("Could not deserialize SVMAggregation model. Creating new one.");
			}
		}
		if(result == null){
			result = new SVMAggregation(untangling);
		}
		return result;
	}
	private boolean              trained             = false;
	
	private final Untangling    untangling;
	
	private svm_model        model;
	
	protected SVMAggregation(final Untangling untangling) {
		super();
		this.untangling = untangling;
	}
	
	@Override
	public double aggregate(final List<Double> values) {
		Condition.check(trained, "You must train a model before using it,");
		
		svm_node x[] = new svm_node[values.size()];
		for (int i = 0; i < values.size(); ++i) {
			x[i] = new svm_node();
			x[i].index = i + 1;
			x[i].value = values.get(i);
		}
		
		return svm.svm_predict(model, x);
	}
	
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type: " + SVMAggregation.class.getSimpleName());
		return sb.toString();
	}
	
	public boolean train(final Collection<AtomicTransaction> transactionSet) {
		
		if (trained) {
			return true;
		}
		
		Condition
		.check(!transactionSet.isEmpty(), "The transactionSet to train linear regression on must be not empty");
		
		Map<SampleType, List<List<Double>>> samples = super.getSamples(transactionSet, TRAIN_FRACTION, untangling);
		
		List<List<Double>> positiveSamples = samples.get(SampleType.POSITIVE);
		List<List<Double>> negativeSamples = samples.get(SampleType.NEGATIVE);
		
		svm_problem prob = new svm_problem();
		prob.l = positiveSamples.size() + negativeSamples.size();
		prob.y = new double[prob.l];
		prob.x = new svm_node[prob.l][positiveSamples.get(0).size()];
		
		for (int i = 0; i < positiveSamples.size(); ++i) {
			List<Double> list = positiveSamples.get(i);
			for (int j = 0; j < list.size(); ++j) {
				prob.x[i][j] = new svm_node();
				prob.x[i][j].index = j + 1;
				prob.x[i][j].value = list.get(j);
				prob.y[i] = 1d;
			}
		}
		
		for (int i = 0; i < negativeSamples.size(); ++i) {
			List<Double> list = negativeSamples.get(i);
			for (int j = 0; j < list.size(); ++j) {
				prob.x[i + positiveSamples.size()][j] = new svm_node();
				prob.x[i + positiveSamples.size()][j].index = j + 1;
				prob.x[i + positiveSamples.size()][j].value = list.get(j);
				prob.y[i + positiveSamples.size()] = 0d;
			}
		}
		
		svm_parameter param = new svm_parameter();
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
		
		model = svm.svm_train(prob, param);
		
		trained = true;
		return trained;
		
	}
	
}
