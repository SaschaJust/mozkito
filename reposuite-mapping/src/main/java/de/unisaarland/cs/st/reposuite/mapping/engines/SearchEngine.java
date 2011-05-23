/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.mapping.storages.LuceneStorage;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.reposuite.settings.LongArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class SearchEngine extends MappingEngine {
	
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
			
			if (terms.size() < (Long) getSettings().getSetting(getOptionName("minTokens")).getValue()) {
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
	public final LuceneStorage getStorage() {
		return getStorage(LuceneStorage.class);
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
		arguments.addArgument(new LongArgument(settings, getOptionName("minTokens"),
		                                       "minimum number of tokens required for a search.", "3", isRequired));
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
