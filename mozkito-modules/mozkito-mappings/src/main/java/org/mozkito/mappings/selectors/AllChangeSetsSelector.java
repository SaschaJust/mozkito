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

import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ReportRegexSelector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class AllChangeSetsSelector extends Selector {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("AllChangeSetsSelector.description"); //$NON-NLS-1$
	                                                                                                  
	/** The Constant TAG. */
	public static final String TAG         = "allChangeSets";                                        //$NON-NLS-1$
	                                                                                                  
	/** The tag format. */
	private String             tagFormat   = null;
	
	/**
	 * Instantiates a new all report selector.
	 * 
	 * @param tagFormat
	 *            the tag format
	 */
	public AllChangeSetsSelector(final String tagFormat) {
		setTagFormat(tagFormat);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.MappingSelector# getDescription()
	 */
	@Override
	public String getDescription() {
		return AllChangeSetsSelector.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.MappingSelector#parse (java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity element,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		final List<T> list = new LinkedList<T>();
		final Criteria<ChangeSet> criteria = util.createCriteria(ChangeSet.class);
		final List<ChangeSet> loadedList = util.load(criteria);
		
		list.addAll(CollectionUtils.collect(loadedList, new Transformer() {
			
			@Override
			public MappableChangeSet transform(final Object input) {
				return new MappableChangeSet((ChangeSet) input);
			}
		}));
		
		return list;
	}
	
	/**
	 * Sets the tag format.
	 * 
	 * @param tagFormat
	 *            the tagFormat to set
	 */
	private final void setTagFormat(final String tagFormat) {
		// PRECONDITIONS
		Condition.notNull(tagFormat, "Argument '%s' in '%s'.", "tagFormat", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.tagFormat = tagFormat;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.tagFormat, tagFormat,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return to.equals(ChangeSet.class);
	}
	
}
