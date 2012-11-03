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

import java.lang.reflect.Constructor;
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
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class TransactionRegexSelector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TransactionRegexSelector extends Selector {
	
	public static final class Options extends
	        ArgumentSetOptions<TransactionRegexSelector, ArgumentSet<TransactionRegexSelector, Options>> {
		
		private static final String    DESCRIPTION = "...";
		private static final String    TAG         = "transactionRegex";
		private StringArgument.Options patternOption;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, Options.TAG, Options.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public TransactionRegexSelector init() {
			// PRECONDITIONS
			
			try {
				final StringArgument patternArgument = getSettings().getArgument(this.patternOption);
				return new TransactionRegexSelector(patternArgument.getValue());
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
				                                                "Pattern of report ids to scan for.",
				                                                TransactionRegexSelector.DEFAULT_PATTERN,
				                                                Requirement.required);
				map.put(this.patternOption.getName(), this.patternOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DEFAULT_PATTERN. */
	private static final String DEFAULT_PATTERN = "(\\p{XDigit}{7,})";
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION     = "Looks up all regular matches of the specified pattern and returns possible (transaction) candidates from the database.";
	
	/** The pattern. */
	private final String        pattern;
	
	@Deprecated
	/**
	 * 
	 */
	public TransactionRegexSelector() {
		// PRECONDITIONS
		
		try {
			this.pattern = TransactionRegexSelector.DEFAULT_PATTERN;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param pattern
	 */
	public TransactionRegexSelector(final String pattern) {
		// PRECONDITIONS
		
		try {
			this.pattern = pattern;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the default pattern.
	 * 
	 * @return the defaultPattern
	 */
	public final String getDefaultPattern() {
		// PRECONDITIONS
		
		try {
			return TransactionRegexSelector.DEFAULT_PATTERN;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(TransactionRegexSelector.DEFAULT_PATTERN,
			                  "Field '%s' in '%s'.", "DEFAULT_PATTERN", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		// PRECONDITIONS
		
		try {
			return TransactionRegexSelector.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(TransactionRegexSelector.DESCRIPTION,
			                  "Field '%s' in '%s'.", "DESCRIPTION", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity element,
	                                                final Class<T> targetType,
	                                                final PersistenceUtil util) {
		final List<T> list = new LinkedList<T>();
		final List<String> ids = new LinkedList<String>();
		final Regex regex = new Regex(this.pattern);
		
		try {
			
			final Criteria<?> criteria = util.createCriteria(targetType.newInstance().getBaseType());
			
			for (int i = 0; i < element.getSize(FieldKey.COMMENT); ++i) {
				final Comment comment = (Comment) element.get(FieldKey.COMMENT, i);
				final MultiMatch multiMatch = regex.findAll(comment.getMessage());
				
				if (multiMatch != null) {
					for (final Match match : multiMatch) {
						
						ids.add(match.getGroup(1).getMatch());
					}
				}
			}
			
			criteria.in("id", ids);
			final List<?> load = util.load(criteria);
			
			for (final Object instance : load) {
				try {
					final Constructor<T> constructor = targetType.getConstructor(instance.getClass());
					list.add(constructor.newInstance(instance));
				} catch (final Exception e) {
					throw new UnrecoverableError(e);
				}
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
		
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return from.equals(Report.class) && to.equals(RCSTransaction.class);
	}
}
