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

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.IteratableFieldKey;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionRegexSelector.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TransactionRegexSelector extends Selector {
	
	/** The Constant TAG. */
	public static final String TAG             = "transactionRegex";                                        //$NON-NLS-1$
	                                                                                                         
	/** The Constant DEFAULT_PATTERN. */
	public static final String DEFAULT_PATTERN = "(\\p{XDigit}{7,})";                                       //$NON-NLS-1$
	                                                                                                         
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION     = Messages.getString("TransactionRegexSelector.description"); //$NON-NLS-1$
	                                                                                                         
	/** The pattern. */
	private final String       pattern;
	
	/**
	 * Instantiates a new transaction regex selector.
	 * 
	 */
	public TransactionRegexSelector() {
		this(TransactionRegexSelector.DEFAULT_PATTERN);
	}
	
	/**
	 * Instantiates a new transaction regex selector.
	 * 
	 * @param pattern
	 *            the pattern
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
	public <T extends org.mozkito.persistence.Entity> List<T> parse(final org.mozkito.persistence.Entity element,
	                                                                final Class<T> targetType,
	                                                                final PersistenceUtil util) {
		final List<T> list = new LinkedList<T>();
		final List<String> ids = new LinkedList<String>();
		final Regex regex = new Regex(this.pattern);
		
		try {
			
			final Criteria<?> criteria = util.createCriteria(targetType);
			
			for (int i = 0; i < element.getSize(IteratableFieldKey.COMMENTS); ++i) {
				final Comment comment = (Comment) element.get(IteratableFieldKey.COMMENTS, i);
				final MultiMatch multiMatch = regex.findAll(comment.getMessage());
				
				if (multiMatch != null) {
					for (final Match match : multiMatch) {
						
						ids.add(match.getGroup(1).getMatch());
					}
				}
			}
			
			criteria.in("id", ids); //$NON-NLS-1$
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
		return from.equals(Report.class) && to.equals(ChangeSet.class);
	}
}
