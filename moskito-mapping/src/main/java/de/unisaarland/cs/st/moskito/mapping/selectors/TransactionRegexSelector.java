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

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
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

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class TransactionRegexSelector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TransactionRegexSelector extends MappingSelector {
	
	/** The pattern. */
	private String                 pattern;
	
	/** The pattern argument. */
	private StringArgument         patternArgument;
	
	/** The pattern option. */
	private StringArgument.Options patternOption;
	
	/** The Constant DESCRIPTION. */
	private static final String    DESCRIPTION     = "Looks up all regular matches of the specified pattern and returns possible (report) candidates from the database.";
	
	/** The Constant DEFAULT_PATTERN. */
	private static final String    DEFAULT_PATTERN = "(\\d{2,})";                                                                                                        //$NON-NLS-1$
	                                                                                                                                                                      
	/**
	 * Gets the default pattern.
	 * 
	 * @return the defaultPattern
	 */
	public final String getDefaultPattern() {
		// PRECONDITIONS
		
		try {
			return DEFAULT_PATTERN;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(DEFAULT_PATTERN, "Field '%s' in '%s'.", "DEFAULT_PATTERN", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
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
			return DESCRIPTION;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(DESCRIPTION, "Field '%s' in '%s'.", "DESCRIPTION", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	/**
	 * Gets the pattern argument.
	 * 
	 * @return the patternArgument
	 */
	private final StringArgument getPatternArgument() {
		// PRECONDITIONS
		
		try {
			return this.patternArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.patternArgument, "Field '%s' in '%s'.", "patternArgument", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the pattern option.
	 * 
	 * @return the patternOption
	 */
	private final StringArgument.Options getPatternOption() {
		// PRECONDITIONS
		
		try {
			return this.patternOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.patternOption, "Field '%s' in '%s'.", "patternOption", getClass().getSimpleName()); //$NON-NLS-1$//$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		Condition.notNull(this.patternOption, "Field '%s' in '%s'.", "patternOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			setPatternArgument(getSettings().getArgument(getPatternOption()));
			Condition.notNull(this.patternArgument, "Field '%s' in '%s'.", "patternArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			
			setPattern(getPatternArgument().getValue());
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.pattern, "Field '%s' in '%s'.", "pattern", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
					Logger.debug("While parsing transaction " + element.get(FieldKey.ID).toString()
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
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		setSettings(root.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// request the mapping.engines anchor
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			setPatternOption(new StringArgument.Options(root, "pattern", "Pattern of transaction ids to scan for.", //$NON-NLS-1$
			                                            getDefaultPattern(), Requirement.required));
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.patternOption, "Field '%s' in '%s'.", "patternOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(anchor, "Return value '%s' in '%s'.", "anchor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Sets the pattern.
	 * 
	 * @param pattern
	 *            the pattern to set
	 */
	private void setPattern(final String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * Sets the pattern argument.
	 * 
	 * @param patternArgument
	 *            the patternArgument to set
	 */
	private final void setPatternArgument(final StringArgument patternArgument) {
		// PRECONDITIONS
		Condition.notNull(patternArgument, "Argument '%s' in '%s'.", "patternArgument", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.patternArgument = patternArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.patternArgument, patternArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * Sets the pattern option.
	 * 
	 * @param patternOption
	 *            the patternOption to set
	 */
	private final void setPatternOption(final StringArgument.Options patternOption) {
		// PRECONDITIONS
		Condition.notNull(patternOption, "Argument '%s' in '%s'.", "patternOsption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.patternOption = patternOption;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.patternOption, patternOption,
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
		return to.equals(Report.class) && from.equals(RCSTransaction.class);
	}
}
