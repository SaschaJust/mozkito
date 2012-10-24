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
package de.unisaarland.cs.st.moskito.mapping.selectors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * The Class ReportRegexSelector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class AllReportSelector extends Selector {
	
	public static final class Options extends
	        ArgumentSetOptions<AllReportSelector, ArgumentSet<AllReportSelector, Options>> {
		
		private static final String                                   TAG         = "allReport";
		private static final String                                   DESCRIPTION = "...";
		private net.ownhero.dev.hiari.settings.StringArgument.Options tagOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public AllReportSelector init() {
			// PRECONDITIONS
			
			try {
				final AllReportSelector selector = new AllReportSelector();
				final StringArgument tagArgument = getSettings().getArgument(this.tagOption);
				
				final String tag = tagArgument.getValue();
				if (tag != null) {
					selector.setTagFormat(tag);
				}
				return selector;
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
				
				this.tagOption = new StringArgument.Options(
				                                            argumentSet,
				                                            "tag",
				                                            "Format string like 'XSTR-%s' that determines how the match from the regex should be used when querying the database.",
				                                            null, Requirement.optional);
				map.put(this.tagOption.getName(), this.tagOption);
				
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "Looks up all regular matches of the specified pattern and returns possible (report) candidates from the database.";
	
	private String              tagFormat   = null;
	
	/**
	 * @param value
	 */
	public AllReportSelector() {
		// PRECONDITIONS
		
		try {
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector# getDescription()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#parse (java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity element,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		final List<T> list = new LinkedList<T>();
		final Criteria<Report> criteria = util.createCriteria(Report.class);
		final List<Report> loadedList = util.load(criteria);
		
		list.addAll(CollectionUtils.collect(loadedList, new Transformer() {
			
			@Override
			public MappableReport transform(final Object input) {
				return new MappableReport((Report) input);
			}
		}));
		
		return list;
	}
	
	/**
	 * @param tagFormat
	 *            the tagFormat to set
	 */
	final void setTagFormat(final String tagFormat) {
		// PRECONDITIONS
		Condition.notNull(tagFormat, "Argument '%s' in '%s'.", "tagFormat", getClass().getSimpleName());
		
		try {
			this.tagFormat = tagFormat;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.tagFormat, tagFormat,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return to.equals(Report.class);
	}
	
}
