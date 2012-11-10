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

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

/**
 * This engine scores if the 'to' entity contains a reference to the 'from' entity in the body text.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class BackrefEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<BackrefEngine, ArgumentSet<BackrefEngine, Options>> {
		
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
			super(argumentSet, BackrefEngine.class.getSimpleName(), BackrefEngine.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public BackrefEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				return new BackrefEngine(confidenceArgument.getValue());
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
				                                                   Messages.getString("BackrefEngine.confidenceDescription"), //$NON-NLS-1$
				                                                   BackrefEngine.getDefaultConfidence(),
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
	private static final String DESCRIPTION        = Messages.getString("BackrefEngine.description"); //$NON-NLS-1$
	                                                                                                  
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultConfidences
	 */
	private static Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return BackrefEngine.DEFAULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(BackrefEngine.DEFAULT_CONFIDENCE, "Field '%s' in '%s'.", "defaultConfidence", //$NON-NLS-1$ //$NON-NLS-2$
			                  BackrefEngine.class.getSimpleName());
		}
	}
	
	/** The confidence. */
	private Double confidence;
	
	/**
	 * Instantiates a new backref engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	BackrefEngine(final Double confidence) {
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
	public final Double getConfidence() {
		// PRECONDITIONS
		
		try {
			return this.confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		return BackrefEngine.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity, org.mozkito.mapping.mappable.MappableEntity,
	 * org.mozkito.mapping.model.Mapping)
	 */
	@Override
	public final void score(final MappableEntity element1,
	                        final MappableEntity element2,
	                        final Relation score) {
		final String fullText = element2.getText();
		final String id = element1.getId();
		
		double localConfidence = 0d;
		if (fullText.contains(id.toString())) {
			localConfidence = getConfidence();
			
		}
		addFeature(score, localConfidence, FieldKey.ID.name(), id, id, "FULLTEXT", fullText, fullText); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public final Expression supported() {
		return new Atom(Index.FROM, FieldKey.ID);
	}
}
