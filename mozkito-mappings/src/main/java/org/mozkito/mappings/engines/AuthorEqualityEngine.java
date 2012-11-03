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

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class AuthorEqualityEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<AuthorEqualityEngine, ArgumentSet<AuthorEqualityEngine, Options>> {
		
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
			super(argumentSet, AuthorEqualityEngine.class.getSimpleName(), "...", requirements); //$NON-NLS-1$
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public AuthorEqualityEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				return new AuthorEqualityEngine(confidenceArgument.getValue());
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
				                                                   AuthorEqualityEngine.getDefaultConfidence(),
				                                                   Requirement.required);
				map.put(this.confidenceOption.getName(), this.confidenceOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The constant defaultConfidence. */
	private static final Double DEFAULT_CONFIDENCE = 0.2d;
	
	/** The constant description. */
	private static final String DESCRIPTION        = Messages.getString("AuthorEqualityEngine.description"); //$NON-NLS-1$
	                                                                                                         
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultConfidences
	 */
	private static final Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return AuthorEqualityEngine.DEFAULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(AuthorEqualityEngine.DEFAULT_CONFIDENCE, "Field '%s' in '%s'.", "DEFAULT_CONFIDENCE", //$NON-NLS-1$ //$NON-NLS-2$
			                  AuthorEqualityEngine.class.getSimpleName());
		}
	}
	
	/** The confidence. */
	private Double confidence;
	
	/**
	 * Instantiates a new author equality engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	public AuthorEqualityEngine(final double confidence) {
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
		return AuthorEqualityEngine.DESCRIPTION;
	}
	
	/**
	 * Score.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param score
	 *            the score
	 */
	@Override
	public final void score(final MappableEntity from,
	                        final MappableEntity to,
	                        final Relation score) {
		double localConfidence = 0d;
		
		// check if the values in the author fields are equal
		if (from.get(FieldKey.AUTHOR).equals(to.get(FieldKey.AUTHOR))) {
			localConfidence = getConfidence();
		}
		
		addFeature(score, localConfidence, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR),
		           from.get(FieldKey.AUTHOR), FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
	}
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.AUTHOR), new Atom(Index.TO, FieldKey.AUTHOR));
	}
	
}
