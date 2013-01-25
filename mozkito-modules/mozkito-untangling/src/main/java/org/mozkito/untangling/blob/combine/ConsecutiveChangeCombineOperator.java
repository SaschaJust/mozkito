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
package org.mozkito.untangling.blob.combine;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import org.mozkito.untangling.blob.ChangeOperationSet;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeCouplingCombineOperator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ConsecutiveChangeCombineOperator implements CombineOperator<ChangeOperationSet> {
	
	/**
	 * The Class Options.
	 */
	public static class Options
	        extends
	        ArgumentSetOptions<ConsecutiveChangeCombineOperator, ArgumentSet<ConsecutiveChangeCombineOperator, Options>> {
		
		/** The time window options. */
		private net.ownhero.dev.hiari.settings.LongArgument.Options timeWindowOptions;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, "consecCombineOp", "ConsecutiveChangeCombineOperator options.", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ConsecutiveChangeCombineOperator init() {
			// PRECONDITIONS
			final Long timeWindow = getSettings().getArgument(this.timeWindowOptions).getValue();
			return new ConsecutiveChangeCombineOperator(timeWindow.intValue());
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			this.timeWindowOptions = new LongArgument.Options(
			                                                  argumentSet,
			                                                  "timeWindowSize",
			                                                  "The number of hours that may lay between two change sets combined by the ConsecutiveChangeCombineOperator.",
			                                                  0l, Requirement.required);
			map.put(this.timeWindowOptions.getName(), this.timeWindowOptions);
			return map;
		}
	}
	
	/** The time window. */
	private final int timeWindow;
	
	/**
	 * Instantiates a new consecutive change combine operator.
	 * 
	 * @param timeWindow
	 *            the time window
	 */
	protected ConsecutiveChangeCombineOperator(final int timeWindow) {
		this.timeWindow = timeWindow;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.untangling.blob.compare.CombineOperator#canBeCombined(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeOperationSet cl1,
	                             final ChangeOperationSet cl2) {
		// PRECONDITIONS
		
		try {
			
			final ChangeSet cl1T = cl1.getChangeSet();
			final ChangeSet cl2T = cl2.getChangeSet();
			
			if (cl1T.getAuthor().equals(cl2T.getAuthor())) {
				// only consider change sets stemming from the same author.
				final DateTime cl1Time = cl1T.getTimestamp();
				final DateTime cl2Time = cl2T.getTimestamp();
				
				if (cl1Time.getDayOfYear() == cl2Time.getDayOfYear()) {
					// both change sets must be applied the same day
					final int hoursDiff = Math.abs(Hours.hoursBetween(cl1Time, cl2Time).getHours());
					if (hoursDiff <= this.timeWindow) {
						return true;
					}
				}
			}
			
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}