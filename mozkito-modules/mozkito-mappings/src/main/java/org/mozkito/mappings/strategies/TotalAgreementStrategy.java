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

import java.util.Queue;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.model.Feature;

/**
 * The Class TotalAggreementStrategy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TotalAgreementStrategy extends Strategy {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("TotalAggreementStrategy.description"); //$NON-NLS-1$
	/** The Constant TAG. */
	public static final String TAG         = "totalAgreement";                                         //$NON-NLS-1$
	                                                                                                    
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return Strategy.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy#map (org.mozkito.mapping.model.RCSBugMapping)
	 */
	@Override
	public Composite map(final Composite composite) {
		int value = 0;
		
		final Queue<Feature> features = composite.getRelation().getFeatures();
		for (final Feature feature : features) {
			final int cache = Double.compare(feature.getConfidence(), 0.0d);
			if (Math.abs(value - cache) > 1) {
				value = 0;
			} else {
				value = cache;
			}
		}
		
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
