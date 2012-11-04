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

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.model.Feature;

/**
 * The Class TotalAggreementStrategy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TotalAggreementStrategy extends Strategy {
	
	public static final class Options extends
	        ArgumentSetOptions<TotalAggreementStrategy, ArgumentSet<TotalAggreementStrategy, Options>> {
		
		private static final String DESCRIPTION = "...";
		private static final String TAG         = "totalAgreement";
		
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
		public TotalAggreementStrategy init() {
			// PRECONDITIONS
			
			try {
				return new TotalAggreementStrategy();
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
			return new HashMap<String, IOptions<?, ?>>();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps positive/negative iff all engines agree on that";
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
