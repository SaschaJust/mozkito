/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
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
package org.mozkito.mappings.engines;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.mozkito.mappings.utils.Information;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class SearchEngine extends Engine {
	
	public static class Options extends ArgumentSetOptions<Set<SearchEngine>, ArgumentSet<Set<SearchEngine>, Options>> {
		
		static final String TAG = "search"; //$NON-NLS-1$
		                                    
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, "...", requirements); //$NON-NLS-1$
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public Set<SearchEngine> init() {
			// PRECONDITIONS
			
			try {
				// TODO Auto-generated method stub
				return null;
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
				// TODO Auto-generated method stub
				return null;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * Builds the query.
	 * 
	 * @param queryString
	 *            the query string
	 * @param queryParser
	 *            the query parser
	 * @return the query
	 */
	protected Query buildQuery(final String queryString,
	                           final QueryParser queryParser) {
		Query query = null;
		
		// replace all non-alphanumeric characters by spaces
		String modifiedQuery = queryString.replaceAll("[^a-zA-Z0-9]", " "); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (modifiedQuery.replaceAll("[^a-zA-Z0-9]", "").length() < 8) { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		try {
			// remove stopwords from the query string
			final String[] tokens = Information.removeStopwords(Information.tokenize(modifiedQuery));
			
			// stem the tokens
			final String[] stemmedTokens = Information.stem(tokens);
			
			// get ten tokens with highest term frequency
			final Map<String, Integer> items = new HashMap<String, Integer>();
			
			for (final String stemmedToken : stemmedTokens) {
				if (!items.containsKey(stemmedToken)) {
					items.put(stemmedToken, 1);
				} else {
					items.put(stemmedToken, items.get(stemmedToken) + 1);
				}
			}
			
			final Map<Integer, List<String>> inverseItems = new TreeMap<>(new Comparator<Integer>() {
				
				@Override
				public int compare(final Integer o1,
				                   final Integer o2) {
					return o2.compareTo(o1);
				}
			});
			
			for (final String key : items.keySet()) {
				final int tf = items.get(key);
				if (!inverseItems.containsKey(tf)) {
					inverseItems.put(tf, new LinkedList<String>() {
						
						private static final long serialVersionUID = 1L;
						
						{
							add(key);
						}
					});
				} else {
					inverseItems.get(tf).add(key);
				}
			}
			
			final List<String> list = new ArrayList<>(10);
			int i = 0;
			int j = 0;
			
			ENOUGH: for (final Integer key : inverseItems.keySet()) {
				for (final String token : inverseItems.get(key)) {
					j += token.length();
					
					// do not build queries with >255 characters
					if (j > 255) {
						break ENOUGH;
					}
					
					list.add(token);
					++i;
					
					if (i == list.size()) {
						break ENOUGH;
					}
				}
			}
			
			// recreate query string
			modifiedQuery = org.apache.commons.lang.StringUtils.join(list, ' ');
			
			query = queryParser.parse(modifiedQuery);
			
			final Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			
			if (terms.size() < getMinTokens()) {
				return null;
			}
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/**
	 * @return
	 */
	private int getMinTokens() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
