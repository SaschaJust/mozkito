/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.selectors;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportRegexSelector extends MappingSelector {
	
	private String pattern = "(\\p{XDigit}{7,})";
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Looks up all regular matches of the specified pattern and returns possible (report) candidates from the database.";
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#init()
	 */
	@Override
	public void init() {
		super.init();
		setPattern((String) getSettings().getSetting(getOptionName("pattern")).getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#parse
	 * (java.lang.Object)
	 */
	@Override
	public <T extends MappableEntity> List<T> parse(final MappableEntity element,
	                                                final Class<T> targetType) {
		List<T> list = new LinkedList<T>();
		try {
			List<String> ids = new LinkedList<String>();
			Regex regex = new Regex(this.pattern);
			PersistenceUtil util;
			
			util = PersistenceManager.getUtil();
			
			try {
				
				Criteria<?> criteria = util.createCriteria(targetType.newInstance().getBaseType());
				
				for (int i = 0; i < element.getSize(FieldKey.COMMENT); ++i) {
					Comment comment = (Comment) element.get(FieldKey.COMMENT, i);
					List<List<RegexGroup>> findAll = regex.findAll(comment.getMessage());
					
					if (findAll != null) {
						for (List<RegexGroup> match : findAll) {
							
							ids.add(match.get(0).getMatch());
						}
					}
				}
				
				criteria.in("id", ids);
				List<?> load = util.load(criteria);
				
				for (Object instance : load) {
					try {
						Constructor<T> constructor = targetType.getConstructor(instance.getClass());
						list.add(constructor.newInstance(instance));
					} catch (Exception e) {
						throw new UnrecoverableError(e);
					}
				}
			} catch (Exception e) {
				throw new UnrecoverableError(e);
			}
			
		} catch (UninitializedDatabaseException e) {
			throw new Shutdown(e.getMessage(), e);
		}
		
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#register(de
	 * .unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments, boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new StringArgument(settings, getOptionName("pattern"),
		                                         "Pattern of report ids to scan for.", "\\p{XDigit}{7,}", isRequired));
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector#supports
	 * (java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> from,
	                        final Class<?> to) {
		return from.equals(Report.class) && to.equals(RCSTransaction.class);
	}
	
}
