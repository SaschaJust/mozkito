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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AuthorEqualityEngine extends MappingEngine {
	
	/** The constant description. */
	private static final String DESCRIPTION        = Messages.getString("AuthorEqualityEngine.description"); //$NON-NLS-1$
	                                                                                                         
	/** The constant defaultConfidence. */
	private static final Double DEFAULT_CONFIDENCE = 0.2d;
	
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
			Condition.notNull(DEFAULT_CONFIDENCE, "Field '%s' in '%s'.", "DEFAULT_CONFIDENCE", //$NON-NLS-1$ //$NON-NLS-2$
			                  AuthorEqualityEngine.class.getSimpleName());
		}
	}
	
	/** The confidence option. */
	private DoubleArgument.Options confidenceOption;
	
	/** The confidence argument. */
	private DoubleArgument         confidenceArgument;
	
	/** The confidence. */
	private Double                 confidence;
	
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
	 * Gets the confidence argument.
	 * 
	 * @return the confidenceArgument
	 */
	private final DoubleArgument getConfidenceArgument() {
		// PRECONDITIONS
		
		try {
			return this.confidenceArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidenceArgument, "Field '%s' in '%s'.", "confidenceArgument", //$NON-NLS-1$ //$NON-NLS-2$
			                  getHandle());
		}
	}
	
	/**
	 * Gets the confidence option.
	 * 
	 * @return the confidenceOption
	 */
	private final DoubleArgument.Options getConfidenceOption() {
		// PRECONDITIONS
		
		try {
			return this.confidenceOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidenceOption, "Field '%s' in '%s'.", "confidenceOption", //$NON-NLS-1$ //$NON-NLS-2$
			                  getHandle());
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/**
	 * After parse.
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		Condition.notNull(this.confidenceOption, "Field '%s' in '%s'.", "confidenceOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			setConfidenceArgument(getSettings().getArgument(getConfidenceOption()));
			setConfidence(getConfidenceArgument().getValue());
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidenceArgument, "Field '%s' in '%s'.", "confidenceArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Provide.
	 * 
	 * @param anchorSet
	 *            the anchor set
	 * @return the argument set
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public ArgumentSet<?, ?> provide(@NotNull final ArgumentSet<?, ?> anchorSet) throws ArgumentRegistrationException,
	                                                                            ArgumentSetRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		setSettings(anchorSet.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// request the mapping.engines anchor
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			
			setConfidenceOption(new DoubleArgument.Options(
			                                               anchor,
			                                               "confidence", //$NON-NLS-1$
			                                               Messages.getString("AuthorEqualityEngine.confidenceDescription"), //$NON-NLS-1$
			                                               getDefaultConfidence(),
			                                               Requirement.contains(getOptions(getSettings()),
			                                                                    getClass().getSimpleName())));
			
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.confidenceOption, "Field '%s' in '%s'.", "confidenceOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(anchor, "Return value '%s' in '%s'.", "anchor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Mapping score) {
		double confidence = 0d;
		
		// check if the values in the author fields are equal
		if (from.get(FieldKey.AUTHOR).equals(to.get(FieldKey.AUTHOR))) {
			confidence = getConfidence();
		}
		
		addFeature(score, confidence, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR), from.get(FieldKey.AUTHOR),
		           FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
	}
	
	/**
	 * Sets the confidence.
	 * 
	 * @param confidence
	 *            the confidence to set
	 */
	private final void setConfidence(@NotNull final Double confidence) {
		// PRECONDITIONS
		
		try {
			this.confidence = confidence;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.confidence, confidence,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the confidence argument.
	 * 
	 * @param confidenceArgument
	 *            the confidenceArgument to set
	 */
	private final void setConfidenceArgument(@NotNull final DoubleArgument confidenceArgument) {
		// PRECONDITIONS
		
		try {
			this.confidenceArgument = confidenceArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.confidenceArgument, confidenceArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the confidence option.
	 * 
	 * @param confidenceOption
	 *            the confidenceOption to set
	 */
	private final void setConfidenceOption(@NotNull final DoubleArgument.Options confidenceOption) {
		// PRECONDITIONS
		
		try {
			this.confidenceOption = confidenceOption;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.confidenceOption, confidenceOption,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.AUTHOR), new Atom(Index.TO, FieldKey.AUTHOR));
	}
	
}
