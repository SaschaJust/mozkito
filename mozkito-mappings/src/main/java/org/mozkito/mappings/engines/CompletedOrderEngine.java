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
package org.mozkito.mappings.engines;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class CompletedOrderEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CompletedOrderEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<CompletedOrderEngine, ArgumentSet<CompletedOrderEngine, Options>> {
		
		/** The confidence option. */
		private DoubleArgument.Options confidenceOption;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, CompletedOrderEngine.class.getSimpleName(), CompletedOrderEngine.DESCRIPTION,
			      requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public CompletedOrderEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				return new CompletedOrderEngine(confidenceArgument.getValue());
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
			// PRECONDITIONS
			
			try {
				final Map<String, IOptions<?, ?>> map = new HashMap<>();
				this.confidenceOption = new DoubleArgument.Options(
				                                                   argumentSet,
				                                                   "confidence", //$NON-NLS-1$
				                                                   Messages.getString("CompletedOrderEngine.confidenceDescription"), //$NON-NLS-1$
				                                                   CompletedOrderEngine.getDefaultConfidence(),
				                                                   Requirement.required);
				map.put(this.confidenceOption.getName(), this.confidenceOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The constant defaultConfidence. */
	private static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The constant description. */
	private static final String DESCRIPTION        = Messages.getString("CompletedOrderEngine.description"); //$NON-NLS-1$
	                                                                                                         
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultConfidences
	 */
	private static Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return CompletedOrderEngine.DEFAULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(CompletedOrderEngine.DEFAULT_CONFIDENCE, "Field '%s' in '%s'.", "defaultConfidence", //$NON-NLS-1$ //$NON-NLS-2$
			                  CompletedOrderEngine.class.getSimpleName());
		}
	}
	
	/** The confidence. */
	private Double confidence;
	
	/**
	 * @param value
	 */
	CompletedOrderEngine(final Double confidence) {
		// PRECONDITIONS
		
		try {
			this.confidence = confidence;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	private Double getConfidence() {
		// PRECONDITIONS
		
		try {
			return this.confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		return CompletedOrderEngine.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity, org.mozkito.mapping.mappable.MappableEntity,
	 * org.mozkito.mapping.model.Mapping)
	 */
	@Override
	public final void score(final MappableEntity from,
	                        final MappableEntity to,
	                        final Relation score) {
		final RCSTransaction transaction = ((MappableTransaction) from).getTransaction();
		final Report report = ((MappableReport) to).getReport();
		double localConfidence = 0d;
		
		if ((report.getResolutionTimestamp() != null)
		        && transaction.getTimestamp().isBefore(report.getResolutionTimestamp())) {
			if (Logger.logDebug()) {
				Logger.debug("Transaction was committed before report got marked as resolved."); //$NON-NLS-1$
			}
			localConfidence = getConfidence();
		}
		
		addFeature(score, localConfidence, FieldKey.CREATION_TIMESTAMP.name(), transaction.getTimestamp(),
		           transaction.getTimestamp(), FieldKey.CREATION_TIMESTAMP.name(), report.getResolutionTimestamp(),
		           report.getResolutionTimestamp());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.TO, Report.class), new Atom(Index.FROM, RCSTransaction.class));
	}
	
}
