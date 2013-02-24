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

import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.requirements.Or;

/**
 * The Class ReportTypeEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportTypeEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The default type. */
	public static final Type   DEFAULT_TYPE       = Type.BUG;
	
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("ReportTypeEngine.description"); //$NON-NLS-1$
	                                                                                                    
	/** The Constant TAG. */
	public static final String TAG                = "reportType";                                      //$NON-NLS-1$
	                                                                                                    
	/** The confidence. */
	private Double             confidence;
	
	/** The type. */
	private Type               type;
	
	/**
	 * Instantiates a new report type engine.
	 * 
	 * @param confidence
	 *            the confidence
	 * @param type
	 *            the type
	 */
	public ReportTypeEngine(final Double confidence, final Type type) {
		// PRECONDITIONS
		
		try {
			this.type = type;
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
	public String getDescription() {
		return ReportTypeEngine.DESCRIPTION;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return this.type;
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
			
			if (from instanceof MappableReport) {
				if (from.get(FieldKey.TYPE) == getType()) {
					addFeature(relation, getConfidence(), FieldKey.TYPE.name(), from.get(FieldKey.TYPE).toString(),
					           from.get(FieldKey.TYPE).toString(), Engine.getUnused(), Engine.getUnknown(),
					           Engine.getUnknown());
				}
			} else if (to instanceof MappableReport) {
				if (to.get(FieldKey.TYPE) == getType()) {
					addFeature(relation, getConfidence(), FieldKey.TYPE.name(), to.get(FieldKey.TYPE).toString(),
					           to.get(FieldKey.TYPE).toString(), Engine.getUnused(), Engine.getUnknown(),
					           Engine.getUnknown());
				}
			} else {
				addFeature(relation, -getConfidence(), Engine.getUnused(), Engine.getUnknown(), Engine.getUnknown(),
				           Engine.getUnused(), Engine.getUnknown(), Engine.getUnknown());
			}
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
						return ((Feature) object).getEngine().equals(ReportTypeEngine.class);
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
		return new Or(new Atom(Index.FROM, Report.class), new Atom(Index.TO, Report.class));
		
	}
	
}
