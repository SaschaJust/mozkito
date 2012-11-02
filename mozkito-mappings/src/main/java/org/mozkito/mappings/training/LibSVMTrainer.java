/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.mappings.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.FileCondition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.strategies.SVMStrategy;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class LibSVMTrainer extends Trainer {
	
	private svm_model model;
	
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
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/**
	 * @return
	 */
	public svm_model getLibSVMModel() {
		// PRECONDITIONS
		
		try {
			return this.model;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	private final void learnValues(final svm_problem problem,
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
	
	private final Map<Tuple<String, String>, List<Double>> readFile(final File file,
	                                                                final int vectorLength) {
		final Map<Tuple<String, String>, List<Double>> map = new HashMap<>();
		
		try (final CSVReader reader = new CSVReader(
		                                            new BufferedReader(new InputStreamReader(new FileInputStream(file))),
		                                            ',')) {
			
			String[] line = null;
			int entry = 0;
			while ((line = reader.readNext()) != null) {
				++entry;
				if (line.length < 3) {
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
					throw new UnrecoverableError(String.format("Invalid vector size: %s", vectorLength));
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
		param.degree = 3;
		param.gamma = 1 / vectorLength;
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
		
		this.model = svm.svm_train(problem, param);
		
		return this.model;
		
	}
}
