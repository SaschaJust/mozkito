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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.persistence.model.Person;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used. This engine requires mozkito-persons to be run.s
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class AuthorEqualityEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 0.2d;
	
	/** The Constant TAG. */
	public static final String TAG                = "author";                                              //$NON-NLS-1$
	                                                                                                        
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("AuthorEqualityEngine.description"); //$NON-NLS-1$
	                                                                                                        
	/** The confidence. */
	private Double             confidence         = DEFAULT_CONFIDENCE;
	
	/**
	 * Instantiates a new author equality engine that scores with the default confidence, if requirements are met.
	 */
	public AuthorEqualityEngine() {
		this.confidence = DEFAULT_CONFIDENCE;
	}
	
	/**
	 * Instantiates a new author equality engine that scores with the given confidence, if requirements are met.
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
	 * Gets the confidence. This method is for internal use only and returns the confidence that is used to score if
	 * requirements are met.
	 * 
	 * @return the confidence
	 */
	private Double getConfidence() {
		// PRECONDITIONS
		
		try {
			return this.confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public final String getDescription() {
		return AuthorEqualityEngine.DESCRIPTION;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final @NotNull Relation relation) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final MappableEntity from = relation.getFrom();
			final MappableEntity to = relation.getTo();
			double localConfidence = 0d;
			
			SANITY: {
				assert from != null;
				assert to != null;
			}
			
			final Person fromAuthor = from.<Person> get(FieldKey.AUTHOR);
			final Person toAuthor = to.<Person> get(FieldKey.AUTHOR);
			
			SANITY: {
				assert fromAuthor != null;
				assert fromAuthor != null;
			}
			
			// check if the values in the author fields are equal
			if (fromAuthor.equals(toAuthor)) {
				localConfidence = getConfidence();
			}
			
			// add result
			addFeature(relation, localConfidence, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR),
			           from.get(FieldKey.AUTHOR), FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR),
			           to.get(FieldKey.AUTHOR));
		} finally {
			POSTCONDITIONS: {
				assert CollectionUtils.exists(relation.getFeatures(), new Predicate() {
					
					/**
					 * {@inheritDoc}
					 * 
					 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
					 */
					@Override
					public boolean evaluate(final Object object) {
						return ((Feature) object).getEngine().equals(AuthorEqualityEngine.class);
					}
				});
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#supported()
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.AUTHOR), new Atom(Index.TO, FieldKey.AUTHOR));
	}
	
}
