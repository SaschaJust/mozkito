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
package org.mozkito.mappings.selectors;

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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;


/**
 * The Class ReportRegexSelector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportRegexSelector extends Selector {
	
	public static final class Options extends
	        ArgumentSetOptions<ReportRegexSelector, ArgumentSet<ReportRegexSelector, Options>> {
		
		private static final String                                   TAG         = "reportRegex";
		private static final String                                   DESCRIPTION = "...";
		private StringArgument.Options                                patternOption;
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
		public ReportRegexSelector init() {
			// PRECONDITIONS
			
			try {
				final StringArgument patternArgument = getSettings().getArgument(this.patternOption);
				final ReportRegexSelector selector = new ReportRegexSelector(patternArgument.getValue());
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
				this.patternOption = new StringArgument.Options(argumentSet, "pattern",
				                                                "Pattern of report ids to scan for.", DEFAULT_PATTERN,
				                                                Requirement.required);
				map.put(this.patternOption.getName(), this.patternOption);
				
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
	private static final String DESCRIPTION     = "Looks up all regular matches of the specified pattern and returns possible (report) candidates from the database.";
	private static final String DEFAULT_PATTERN = "(\\p{Digit}{2,})";
	/** The pattern. */
	private final String        pattern;
	
	private String              tagFormat       = null;
	
	@Deprecated
	public ReportRegexSelector() {
		this.pattern = DEFAULT_PATTERN;
	}
	
	/**
	 * @param value
	 */
	public ReportRegexSelector(final String pattern) {
		// PRECONDITIONS
		
		try {
			// TODO warn if pattern does not contain groups
			// TODO error if pattern contains more than 1 group and does not have a named group 'id'
			this.pattern = pattern;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.MappingSelector# getDescription()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/**
	 * Gets the pattern.
	 * 
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
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
		final List<String> ids = new LinkedList<>();
		final Regex regex = new Regex(this.pattern);
		
		final Criteria<Report> criteria = util.createCriteria(Report.class);
		
		final MultiMatch multiMatch = regex.findAll(element.get(FieldKey.BODY).toString());
		if (Logger.logDebug()) {
			Logger.debug("Parsing commit message '" + element.get(FieldKey.BODY).toString() + "' and found "
			        + (multiMatch != null
			                             ? multiMatch.size()
			                             : 0) + " matches for regex '" + this.pattern + "'.");
		}
		
		if (multiMatch != null) {
			for (final Match match : multiMatch) {
				if (Logger.logDebug()) {
					Logger.debug("While parsings " + element.get(FieldKey.ID).toString()
					        + " i stumbled upon this match: " + match.getGroup(1).getMatch());
				}
				ids.add(this.tagFormat != null
				                              ? String.format(this.tagFormat, match.getGroup(1).getMatch())
				                              : match.getGroup(1).getMatch());
			}
		}
		criteria.in("id", ids);
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
	 * @see org.mozkito.mappings.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return from.equals(RCSTransaction.class) && to.equals(Report.class);
	}
	
}
