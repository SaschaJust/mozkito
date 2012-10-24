/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.mozkito.mappings.strategies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.FileCondition;
import net.ownhero.dev.kisa.Logger;
import au.com.bytecode.opencsv.CSVReader;
import de.unisaarland.cs.st.mozkito.mappings.model.Feature;
import de.unisaarland.cs.st.mozkito.mappings.model.IComposite;

/**
 * The Class TotalAggreementStrategy.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class SVMStrategy extends MappingStrategy {
	
	public static final class Options extends ArgumentSetOptions<SVMStrategy, ArgumentSet<SVMStrategy, Options>> {
		
		private static final String       TAG         = "svm";
		private static final String       DESCRIPTION = "...";
		private InputFileArgument.Options positiveFileOption;
		private InputFileArgument.Options negativeFileOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public SVMStrategy init() {
			// PRECONDITIONS
			
			try {
				return new SVMStrategy();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.positiveFileOption = new InputFileArgument.Options(argumentSet, "positiveSamples", "...", null,
			                                                        Requirement.required);
			this.negativeFileOption = new InputFileArgument.Options(argumentSet, "negativeSamples", "...", null,
			                                                        Requirement.required);
			
			return map;
		}
		
	}
	
	private static final void learnValues(final svm_problem problem,
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
	
	private static final Map<Tuple<String, String>, List<Double>> readFile(final File file,
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
	
	public static svm_model train(final File positiveInput,
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
		
		final svm_model model = svm.svm_train(problem, param);
		
		return model;
		
	}
	
	svm_model model;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps positive/negative according to the learned model of the SVM.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.mozkito.mapping.model.RCSBugMapping)
	 */
	@Override
	public IComposite map(final IComposite composite) {
		double value = 0;
		
		final Queue<Feature> features = composite.getRelation().getFeatures();
		final svm_node x[] = new svm_node[features.size()];
		
		final int i = 0;
		for (final Feature feature : features) {
			x[i].index = i + 1;
			x[i].value = feature.getConfidence();
		}
		
		value = svm.svm_predict(this.model, x);
		
		if (value > 0) {
			composite.addStrategy(getHandle(), true);
		} else if (value < 0) {
			composite.addStrategy(getHandle(), false);
		} else {
			composite.addStrategy(getHandle(), null);
		}
		
		return composite;
	}
	
}
