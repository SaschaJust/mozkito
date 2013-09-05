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
import org.joda.time.DateTime;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.model.Artifact;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class CreationOrderEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CreationOrderEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("CreationOrderEngine.description"); //$NON-NLS-1$
	                                                                                                       
	/** The Constant TAG. */
	public static final String TAG                = "creationOrder";                                      //$NON-NLS-1$
	                                                                                                       
	/** The confidence. */
	private Double             confidence;
	
	/**
	 * Instantiates a new creation order engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	public CreationOrderEngine(final Double confidence) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.confidence = confidence;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	private Double getConfidence() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.confidence;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		return CreationOrderEngine.DESCRIPTION;
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
			final Artifact from = relation.getFrom();
			final Artifact to = relation.getTo();
			
			SANITY: {
				assert from != null;
				assert to != null;
				assert from.get(FieldKey.CREATION_TIMESTAMP) != null;
				assert to.get(FieldKey.CREATION_TIMESTAMP) != null;
			}
			
			final double localConfidence;
			if (((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).isBefore(((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)))) {
				localConfidence = getConfidence();
			} else {
				localConfidence = 0d;
			}
			
			// add result
			addFeature(relation, localConfidence, FieldKey.CREATION_TIMESTAMP.name(),
			           ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			           ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			           FieldKey.CREATION_TIMESTAMP.name(), ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			           ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString());
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
						return ((Feature) object).getEngine().equals(CreationOrderEngine.class);
					}
				});
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, Report.class), new Atom(Index.TO, ChangeSet.class));
	}
	
}
