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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ReportRegexSelector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ReportRegexSelector extends MappingSelector {
	
	public static final class Options extends
	        ArgumentSetOptions<ReportRegexSelector, ArgumentSet<ReportRegexSelector, Options>> {
		
		private static final String    TAG         = "reportRegex";
		private static final String    DESCRIPTION = "...";
		private StringArgument.Options patternOption;
		
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
				return new ReportRegexSelector(patternArgument.getValue());
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
			this.pattern = pattern;
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
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#parse (java.lang.Object)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity element,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		final List<T> list = new LinkedList<T>();
		final List<Long> ids = new LinkedList<Long>();
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
				ids.add(Long.parseLong(match.getGroup(1).getMatch()));
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return from.equals(RCSTransaction.class) && to.equals(Report.class);
	}
	
}
