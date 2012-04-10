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

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ReportRegexSelector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ReportRegexSelector extends MappingSelector {
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "Looks up all regular matches of the specified pattern and returns possible (transaction) candidates from the database.";
	
	/** The pattern. */
	private String              pattern;
	
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
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#parse (java.lang.Object)
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
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			new StringArgument.Options(root, "pattern", "Pattern of report ids to scan for.", "(\\p{XDigit}{7,})",
			                           Requirement.required);
			return root;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the pattern.
	 * 
	 * @param pattern
	 *            the pattern to set
	 */
	@SuppressWarnings ("unused")
	private void setPattern(final String pattern) {
		this.pattern = pattern;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#supports (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return from.equals(Report.class) && to.equals(RCSTransaction.class);
	}
	
}
