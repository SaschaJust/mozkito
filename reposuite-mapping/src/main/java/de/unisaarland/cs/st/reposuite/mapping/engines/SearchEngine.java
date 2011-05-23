/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.mapping.storages.LuceneStorage;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;

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
			query = queryParser.parse(queryString);
			
			Set<Term> terms = new HashSet<Term>();
			query.extractTerms(terms);
			
			if (terms.size() < (Long) getSettings().getSetting("mapping.config.minTokens").getValue()) {
				return null;
			}
		} catch (ParseException e) {
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
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#provideStorage
	 * (de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage)
	 */
	@Override
	public void provideStorage(final MappingStorage storage) {
		super.provideStorage(storage);
		this.storage = getStorage(LuceneStorage.class);
		String value = (String) getSettings().getSetting("mapping.config.language").getValue();
		String[] split = value.split(":");
		try {
			if (this.storage.getAnalyzer() == null) {
				Class<?> clazz = Class.forName("org.apache.lucene.analysis." + split[0] + "." + split[1] + "Analyzer");
				Constructor<?> constructor = clazz.getConstructor(Version.class);
				Analyzer newInstance = (Analyzer) constructor.newInstance(Version.LUCENE_31);
				this.storage.setAnalyzer(newInstance);
			}
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new LongArgument(settings, "mapping.config.minTokens",
		                                       "minimum number of tokens required for a search.", "3", isRequired));
		arguments.addArgument(new StringArgument(settings, "mapping.config.language",
		                                         "minimum number of tokens required for a search.", "en:English",
		                                         isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#
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
