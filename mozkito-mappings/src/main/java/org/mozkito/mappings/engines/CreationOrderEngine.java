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

import org.joda.time.DateTime;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.versions.model.RCSTransaction;


/**
 * The Class CreationOrderEngine.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CreationOrderEngine extends MappingEngine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<CreationOrderEngine, ArgumentSet<CreationOrderEngine, Options>> {
		
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
			super(argumentSet, CreationOrderEngine.class.getSimpleName(), "...", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public CreationOrderEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				return new CreationOrderEngine(confidenceArgument.getValue());
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
				                                                   Messages.getString("AuthorEqualityEngine.confidenceDescription"), //$NON-NLS-1$
				                                                   getDefaultConfidence(), Requirement.required);
				map.put(this.confidenceOption.getName(), this.confidenceOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The constant description. */
	private static final String DESCRIPTION        = Messages.getString("CreationOrderEngine.description"); //$NON-NLS-1$
	                                                                                                        
	/** The constant defaultConfidence. */
	private static final Double DEFAULT_CONFIDENCE = 1d;
	
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultConfidences
	 */
	private static final Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return DEFAULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(DEFAULT_CONFIDENCE, "Field '%s' in '%s'.", "defaultConfidence", //$NON-NLS-1$ //$NON-NLS-2$
			                  CreationOrderEngine.class.getSimpleName());
		}
	}
	
	/** The confidence. */
	private Double confidence;
	
	/**
	 * @param value
	 */
	public CreationOrderEngine(final Double confidence) {
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
	private final Double getConfidence() {
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
		return DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * org.mozkito.mapping.mappable.MappableEntity, org.mozkito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Relation score) {
		double localConfidence = 0d;
		if (((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).isBefore(((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)))) {
			localConfidence = getConfidence();
		}
		
		addFeature(score, localConfidence, FieldKey.CREATION_TIMESTAMP.name(),
		           ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
		           ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(), FieldKey.CREATION_TIMESTAMP.name(),
		           ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(),
		           ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, Report.class), new Atom(Index.TO, RCSTransaction.class));
	}
	
}
