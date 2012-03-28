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
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

// TODO: Auto-generated Javadoc
/**
 * This engine scores if the 'to' entity contains a reference to the 'from' entity in the body text.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BackrefEngine extends MappingEngine {
	
	/** The constant description. */
	private static final String description       = Messages.getString("BackrefEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/** The constant defaultConfidence. */
	private static final Double defaultConfidence = 1d;
	
	/**
	 * Gets the default confidence.
	 * 
	 * @return the defaultConfidences
	 */
	private static final Double getDefaultConfidence() {
		// PRECONDITIONS
		
		try {
			return defaultConfidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(defaultConfidence, "Field '%s' in '%s'.", "defaultConfidence", //$NON-NLS-1$ //$NON-NLS-2$
			                  BackrefEngine.class.getSimpleName());
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
			                  getClass().getSimpleName());
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
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		setSettings(root.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			
			setConfidenceOption(new DoubleArgument.Options(anchor, "confidence", //$NON-NLS-1$
			                                               Messages.getString("BackrefEngine.confidenceDescription"), //$NON-NLS-1$
			                                               getDefaultConfidence(),
			                                               Requirement.contains(getOptions(getSettings()),
			                                                                    getClass().getSimpleName())));
			
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.confidenceOption, "Field '%s' in '%s'.", "confidenceOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(anchor, "Field '%s' in '%s'.", "anchor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Mapping score) {
		final String fullText = element2.getText();
		final String id = element1.get(FieldKey.ID).toString();
		
		double confidence = 0d;
		if (fullText.contains(id.toString())) {
			confidence = getConfidence();
			
		}
		addFeature(score, confidence, FieldKey.ID.name(), id, id, "FULLTEXT", fullText, fullText); //$NON-NLS-1$
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, FieldKey.ID);
	}
}
