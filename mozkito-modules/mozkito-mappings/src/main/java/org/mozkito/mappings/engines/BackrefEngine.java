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

/**
 * This engine scores if the 'to' entity contains a reference to the 'from' entity in the body text.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class BackrefEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("BackrefEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/** The confidence. */
	private Double             confidence         = DEFAULT_CONFIDENCE;
	
	/**
	 * Instantiates a new backref engine.
	 */
	public BackrefEngine() {
		// PRECONDITIONS
		
		try {
			this.confidence = DEFAULT_CONFIDENCE;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Instantiates a new backref engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	public BackrefEngine(final Double confidence) {
		// PRECONDITIONS
		
		try {
			this.confidence = confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(confidence, "Field '%s' in '%s'.", "confidence", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
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
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public final String getDescription() {
		return BackrefEngine.DESCRIPTION;
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
			
			SANITY: {
				assert from != null;
				assert to != null;
			}
			
			final String fullText = to.getText();
			final String id = from.getId();
			
			double localConfidence = 0d;
			
			// check if the text representation of the elements contain the others ID
			if (to.getText().contains(from.getId()) && from.getText().contains(to.getId())) {
				localConfidence = getConfidence();
				
			}
			
			// add results
			addFeature(relation, localConfidence, FieldKey.ID.name(), id, id, "FULLTEXT", fullText, fullText); //$NON-NLS-1$
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
						return ((Feature) object).getEngine().equals(getClass());
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
		return new And(new Atom(Index.FROM, FieldKey.ID), new Atom(Index.TO, FieldKey.ID));
	}
}
