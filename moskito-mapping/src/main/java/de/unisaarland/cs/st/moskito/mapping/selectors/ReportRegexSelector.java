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

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import net.ownhero.dev.hiari.settings.arguments.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportRegexSelector extends MappingSelector {
	
	private String         pattern;
	private StringArgument patternArgument;
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void afterParse() {
		setPattern(this.patternArgument.getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector# getDescription()
	 */
	@Override
	public String getDescription() {
		return "Looks up all regular matches of the specified pattern and returns possible (transaction) candidates from the database.";
	}
	
	/**
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
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws ArgumentRegistrationException {
		this.patternArgument = new StringArgument(set, "pattern", "Pattern of report ids to scan for.",
		                                          "(\\p{XDigit}{7,})", set.getRequirements());
		return true;
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
				final List<List<RegexGroup>> findAll = regex.findAll(comment.getMessage());
				
				if (findAll != null) {
					for (final List<RegexGroup> match : findAll) {
						
						ids.add(match.get(0).getMatch());
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
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
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
