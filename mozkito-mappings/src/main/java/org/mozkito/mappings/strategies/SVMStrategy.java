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
package org.mozkito.mappings.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.IComposite;
import org.mozkito.mappings.training.LibSVMTrainer;

/**
 * The Class TotalAggreementStrategy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SVMStrategy extends Strategy {
	
	public static final class Options extends ArgumentSetOptions<SVMStrategy, ArgumentSet<SVMStrategy, Options>> {
		
		private static final String       DESCRIPTION = "...";
		private static final String       TAG         = "svm";
		private InputFileArgument.Options negativeFileOption;
		private InputFileArgument.Options positiveFileOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, Options.TAG, Options.DESCRIPTION, requirements);
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
	
	svm_model model;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps positive/negative according to the learned model of the SVM.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy#map (org.mozkito.mapping.model.RCSBugMapping)
	 */
	@Override
	public IComposite map(final IComposite composite) {
		double value = 0;
		if (this.model == null) {
			final LibSVMTrainer trainer = new LibSVMTrainer();
			trainer.train();
			this.model = trainer.getLibSVMModel();
		}
		
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
