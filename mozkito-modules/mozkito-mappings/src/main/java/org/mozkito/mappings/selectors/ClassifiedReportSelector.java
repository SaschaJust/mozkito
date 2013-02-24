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
package org.mozkito.mappings.selectors;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.EnhancedReport;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ClassifiedReportSelector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ClassifiedReportSelector extends Selector {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("ClassifiedReportSelector.description"); //$NON-NLS-1$
	                                                                                                     
	/** The Constant TAG. */
	public static final String TAG         = "classifiedReport";                                        //$NON-NLS-1$
	                                                                                                     
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return ClassifiedReportSelector.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.Selector#parse(org.mozkito.mappings.mappable.model.MappableEntity,
	 * java.lang.Class, org.mozkito.persistence.PersistenceUtil)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity entity,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		// PRECONDITIONS
		
		try {
			final List<T> list = new LinkedList<T>();
			final List<String> ids = new LinkedList<>();
			final Criteria<EnhancedReport> criteria = util.createCriteria(EnhancedReport.class);
			final List<EnhancedReport> loadedList = util.load(criteria);
			
			for (final EnhancedReport report : loadedList) {
				final Type type = report.getClassifiedType();
				if (Type.BUG.equals(type)) {
					ids.add(report.getId());
				}
			}
			
			final Criteria<Report> rCriteria = util.createCriteria(Report.class);
			rCriteria.in("id", ids); //$NON-NLS-1$
			
			final List<Report> load = util.load(rCriteria);
			list.addAll(CollectionUtils.collect(load, new Transformer() {
				
				@Override
				public MappableReport transform(final Object input) {
					return new MappableReport((Report) input);
				}
			}));
			
			return list;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.Selector#supports(java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		// PRECONDITIONS
		
		try {
			return from.equals(ChangeSet.class) && to.equals(Report.class);
		} finally {
			// POSTCONDITIONS
		}
	}
}
