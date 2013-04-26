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
package org.mozkito.mappings.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import au.com.bytecode.opencsv.CSVReader;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.FileCondition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.strategies.SVMStrategy;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class LibSVMTrainer.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class LibSVMTrainer extends Trainer {
	
	/** The Constant EPS. */
	private static final double EPS        = 0.001d;
	
	/** The Constant P. */
	private static final double P          = 0.10000000;
	
	/** The Constant CACHE_SIZE. */
	private static final double CACHE_SIZE = 100d;
	
	/** The Constant NU. */
	private static final double NU         = 0.5d;
	
	/** The Constant DEGREE. */
	private static final int    DEGREE     = 3;
	
	/** The model. */
	private svm_model           model;
	
	/** The Constant MIN_TOKENS. */
	private static final int    MIN_TOKENS = 3;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the lib svm model.
	 * 
	 * @return the lib svm model
	 */
	public svm_model getLibSVMModel() {
		// PRECONDITIONS
		
		try {
			return this.model;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Learn values.
	 * 
	 * @param problem
	 *            the problem
	 * @param map
	 *            the map
	 * @param confidence
	 *            the confidence
	 */
	private void learnValues(final svm_problem problem,
	                         final Map<Tuple<String, String>, List<Double>> map,
	                         final double confidence) {
		int i = 0;
		for (final Tuple<String, String> relation : map.keySet()) {
			final List<Double> vector = map.get(relation);
			for (int j = 0; j < vector.size(); ++j) {
				problem.x[i][j].index = j - 1;
				problem.x[i][j].value = vector.get(j);
				problem.y[i] = confidence;
			}
			++i;
		}
	}
	
	/**
	 * Read file.
	 * 
	 * @param file
	 *            the file
	 * @param vectorLength
	 *            the vector length
	 * @return the map
	 */
	private Map<Tuple<String, String>, List<Double>> readFile(final File file,
	                                                          final int vectorLength) {
		final Map<Tuple<String, String>, List<Double>> map = new HashMap<>();
		
		try (final CSVReader reader = new CSVReader(
		                                            new BufferedReader(new InputStreamReader(new FileInputStream(file))),
		                                            ',')) {
			
			String[] line = null;
			int entry = 0;
			while ((line = reader.readNext()) != null) {
				++entry;
				if (line.length < LibSVMTrainer.MIN_TOKENS) {
					if (Logger.logError()) {
						Logger.error("There has to be at least one confidence value in addition to 2 IDs, but got: %s", JavaUtils.arrayToString(line)); //$NON-NLS-1$
					}
					throw new UnrecoverableError();
				} else {
					if (vectorLength != (line.length - 2)) {
						throw new UnrecoverableError(
						                             String.format("Support vector size differs at entry line %s. Was %s before, but is %s now.", entry, vectorLength, line.length - 2)); //$NON-NLS-1$
					}
					
					final List<Double> vector = new ArrayList<Double>(vectorLength);
					
					for (int valueId = 2; valueId < line.length; ++valueId) {
						vector.set(valueId - 2, Double.parseDouble(line[valueId]));
					}
					
					map.put(new Tuple<String, String>(line[0], line[1]), vector);
				}
			}
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
		
		return map;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.training.Trainer#train()
	 */
	@Override
	public void train() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Train.
	 * 
	 * @param positiveInput
	 *            the positive input
	 * @param negativeInput
	 *            the negative input
	 * @return the svm_model
	 */
	public svm_model train(final File positiveInput,
	                       final File negativeInput) {
		// PRECONDITIONS
		FileCondition.readableFile(positiveInput,
		                           "File '%s' in '%s:%s'", positiveInput.getAbsolutePath(), SVMStrategy.class.getSimpleName(), "train"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// BODY
		final svm_problem problem = new svm_problem();
		
		// TODO determine vector size
		int vectorLength = 0;
		
		try (final CSVReader reader = new CSVReader(
		                                            new BufferedReader(
		                                                               new InputStreamReader(
		                                                                                     new FileInputStream(
		                                                                                                         positiveInput))),
		                                            ',')) {
			
			String[] line = null;
			if ((line = reader.readNext()) != null) {
				vectorLength = line.length - 2;
				if (vectorLength < 1) {
					throw new UnrecoverableError(Messages.getString("LibSVMTrainer.invalidVectorSize", vectorLength)); //$NON-NLS-1$
				}
				
			}
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
		
		final Map<Tuple<String, String>, List<Double>> positiveMap = readFile(positiveInput, vectorLength);
		final Map<Tuple<String, String>, List<Double>> negativeMap = readFile(negativeInput, vectorLength);
		
		// setup the problem
		problem.l = positiveMap.size() + negativeMap.size();
		problem.y = new double[problem.l];
		problem.x = new svm_node[problem.l][vectorLength];
		
		learnValues(problem, positiveMap, 1.0);
		learnValues(problem, negativeMap, 0.0);
		
		final svm_parameter param = new svm_parameter();
		param.svm_type = 0;
		param.kernel_type = 2;
		param.degree = LibSVMTrainer.DEGREE;
		param.gamma = 1 / vectorLength;
		param.coef0 = 0d;
		param.nu = LibSVMTrainer.NU;
		param.cache_size = LibSVMTrainer.CACHE_SIZE;
		param.C = 1d;
		param.eps = LibSVMTrainer.EPS;
		param.p = LibSVMTrainer.P;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		
		this.model = svm.svm_train(problem, param);
		
		return this.model;
		
	}
}
