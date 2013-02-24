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
package org.mozkito.mappings.strategies;

import java.io.File;
import java.util.Queue;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.training.LibSVMTrainer;

/**
 * The Class TotalAggreementStrategy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SVMStrategy extends Strategy {
	
	/** The model. */
	private svm_model          model;
	
	/** The Constant TAG. */
	public static final String TAG         = "svm";                                        //$NON-NLS-1$
	                                                                                        
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("SVMStrategy.description"); //$NON-NLS-1$
	                                                                                        
	/**
	 * Instantiates a new sVM strategy.
	 * 
	 * @param value
	 *            the value
	 * @param value2
	 *            the value2
	 */
	public SVMStrategy(final File value, final File value2) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated constructor stub
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return SVMStrategy.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy#map (org.mozkito.mapping.model.RCSBugMapping)
	 */
	@Override
	public Composite map(final Composite composite) {
		double value = 0;
		if (this.model == null) {
			final LibSVMTrainer trainer = new LibSVMTrainer();
			trainer.train();
			this.model = trainer.getLibSVMModel();
		}
		
		final Queue<Feature> features = composite.getRelation().getFeatures();
		final svm_node[] x = new svm_node[features.size()];
		
		final int i = 0;
		for (final Feature feature : features) {
			x[i].index = i + 1;
			x[i].value = feature.getConfidence();
		}
		
		value = svm.svm_predict(this.model, x);
		
		if (value > 0) {
			composite.addStrategy(this, true);
		} else if (value < 0) {
			composite.addStrategy(this, false);
		} else {
			composite.addStrategy(this, null);
		}
		
		return composite;
	}
	
}
