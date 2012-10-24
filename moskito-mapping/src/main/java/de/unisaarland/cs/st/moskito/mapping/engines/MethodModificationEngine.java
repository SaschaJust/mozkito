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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.mapping.model.Relation;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MethodModificationEngine extends MappingEngine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<MethodModificationEngine, ArgumentSet<MethodModificationEngine, Options>> {
		
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
			super(argumentSet, MethodModificationEngine.class.getSimpleName(), "...", requirements); //$NON-NLS-1$
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public MethodModificationEngine init() {
			// PRECONDITIONS
			
			try {
				final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
				return new MethodModificationEngine(confidenceArgument.getValue());
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
				                                                   Messages.getString("MethodModificationEngine.confidenceDescription"), //$NON-NLS-1$
				                                                   getDefaultConfidence(), Requirement.required);
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
	private static final String DESCRIPTION        = Messages.getString("MethodModificationEngine.description"); //$NON-NLS-1$
	                                                                                                             
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
			                  MethodModificationEngine.class.getSimpleName());
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
	public MethodModificationEngine(final double confidence) {
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
		int matches = 0;
		final StringBuilder builder = new StringBuilder();
		double localConfidence = 0d;
		// TODO get real PersistenceUtil
		final PersistenceUtil util = null;
		final Set<String> subjects = new HashSet<>();
		
		final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(util,
		                                                                                               ((MappableTransaction) from).getTransaction());
		
		for (final JavaChangeOperation operation : changeOperations) {
			if (operation.getChangeType().equals(ChangeType.Modified)) {
				
				final JavaElement javaElement = operation.getChangedElementLocation().getElement();
				if (javaElement instanceof JavaMethodDefinition) {
					final String fullQualifiedName = javaElement.getFullQualifiedName();
					subjects.add(fullQualifiedName);
				}
			}
		}
		
		for (final String subject : subjects) {
			final String bodyText = (String) to.get(FieldKey.BODY);
			if (StringUtils.containsIgnoreCase(bodyText, subject)) {
				++matches;
				if (builder.length() > 0) {
					builder.append(',');
				}
				builder.append(subject.toUpperCase());
			}
		}
		
		localConfidence = ((double) matches) / ((double) subjects.size());
		
		addFeature(score, localConfidence, "JAVA_CHANGE_OPERATION", "", //$NON-NLS-1$ //$NON-NLS-2$
		           JavaUtils.collectionToString(subjects), FieldKey.BODY.name(), "", builder.toString()); //$NON-NLS-1$
	}
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.FROM, RCSTransaction.class), new Atom(Index.TO, FieldKey.BODY));
	}
	
}
