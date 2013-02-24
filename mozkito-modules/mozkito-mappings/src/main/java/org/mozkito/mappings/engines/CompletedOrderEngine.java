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
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class CompletedOrderEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CompletedOrderEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("CompletedOrderEngine.description"); //$NON-NLS-1$
	                                                                                                        
	/** The confidence. */
	private Double             confidence;
	
	/**
	 * Instantiates a new completed order engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	public CompletedOrderEngine(final Double confidence) {
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
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
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
			
			final ChangeSet changeset = ((MappableChangeSet) from).getChangeSet();
			final Report report = ((MappableReport) to).getReport();
			final double localConfidence;
			
			if ((report.getResolutionTimestamp() != null)
			        && changeset.getTimestamp().isBefore(report.getResolutionTimestamp())) {
				if (Logger.logDebug()) {
					Logger.debug("Transaction was committed before report got marked as resolved."); //$NON-NLS-1$
				}
				localConfidence = getConfidence();
			} else {
				localConfidence = 0d;
			}
			
			addFeature(relation, localConfidence, FieldKey.CREATION_TIMESTAMP.name(), changeset.getTimestamp(),
			           changeset.getTimestamp(), FieldKey.CREATION_TIMESTAMP.name(), report.getResolutionTimestamp(),
			           report.getResolutionTimestamp());
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
						return ((Feature) object).getEngine().equals(CompletedOrderEngine.class);
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
	public final Expression supported() {
		return new And(new Atom(Index.TO, Report.class), new Atom(Index.FROM, ChangeSet.class));
	}
	
}
