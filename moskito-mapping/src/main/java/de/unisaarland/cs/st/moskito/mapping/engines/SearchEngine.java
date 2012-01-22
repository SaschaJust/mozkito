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
package de.unisaarland.cs.st.moskito.mapping.engines;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.exceptions.NoSuchConstructorError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.moskito.mapping.storages.LuceneStorage;
import de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class SearchEngine extends MappingEngine {
	
	private LuceneStorage storage;
	
	/**
	 * @param queryString
	 * @param queryParser
	 * @return
	 */
	protected Query buildQuery(String queryString,
	                           final QueryParser queryParser) {
		Query query = null;
		queryString = queryString.replaceAll("[^a-zA-Z0-9]", " ");
		
		if (queryString.replaceAll("[^a-zA-Z0-9]", "").length() < 8) {
			return null;
		}
		
		try {
			// FIXME remove truncate and fix query string accordingly
			query = queryParser.parse(truncate(queryString));
			
			final Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			
			if (terms.size() < (Long) getOption("minTokens").getSecond().getValue()) {
				return null;
			}
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	/**
	 * @return
	 */
	public LuceneStorage getStorage() {
		return this.storage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#provideStorage
	 * (de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage)
	 */
	@Override
	public void provideStorage(final MappingStorage storage) {
		super.provideStorage(storage);
		this.storage = getStorage(LuceneStorage.class);
		
		if (storage != null) {
			final String value = (String) getOption("language").getSecond().getValue();
			final String[] split = value.split(":");
			Class<?> clazz = null;
			Constructor<?> constructor = null;
			final String className = "org.apache.lucene.analysis." + split[0] + "." + split[1] + "Analyzer";
			try {
				if (this.storage.getAnalyzer() == null) {
					clazz = Class.forName(className);
					constructor = clazz.getConstructor(Version.class);
					final Analyzer newInstance = (Analyzer) constructor.newInstance(Version.LUCENE_31);
					this.storage.setAnalyzer(newInstance);
				}
			} catch (final ClassNotFoundException e) {
				throw new ClassLoadingError(e, className);
			} catch (final SecurityException e) {
				throw new UnrecoverableError(e);
			} catch (final NoSuchMethodException e) {
				throw new NoSuchConstructorError(e, constructor, Version.class);
			} catch (final IllegalArgumentException e) {
				throw new UnrecoverableError(e);
			} catch (final InstantiationException e) {
				throw new net.ownhero.dev.andama.exceptions.InstantiationError(e, clazz, constructor, Version.LUCENE_31);
			} catch (final IllegalAccessException e) {
				throw new UnrecoverableError(e);
			} catch (final InvocationTargetException e) {
				throw new net.ownhero.dev.andama.exceptions.InstantiationError(e, clazz, constructor, Version.LUCENE_31);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments, boolean)
	 */
	@Override
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet arguments) {
		super.register(settings, arguments);
		registerLongOption(settings, arguments, "minTokens", "Minimum number of tokens required for a search.", "3",
		                   true);
		registerStringOption(settings, arguments, "language", "Language used for stemming.", "en:English", true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#
	 * storageDependency()
	 */
	@Override
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>() {
			
			private static final long serialVersionUID = 8360079171674014391L;
			
			{
				add(LuceneStorage.class);
			}
		};
	}
}
