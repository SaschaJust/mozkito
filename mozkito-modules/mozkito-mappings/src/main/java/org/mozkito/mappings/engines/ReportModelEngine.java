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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.issues.model.EnhancedReport;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

/**
 * The Class ReportModelEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportModelEngine extends Engine {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("ReportModelEngine.description"); //$NON-NLS-1$
	                                                                                              
	/** The Constant TAG. */
	public static final String TAG         = "reportModel";                                      //$NON-NLS-1$
	                                                                                              
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return ReportModelEngine.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
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
						return ((Feature) object).getEngine().equals(ReportModelEngine.class);
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
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			return new Atom(Index.ONE, EnhancedReport.class);
		} finally {
			// POSTCONDITIONS
		}
	}
}
