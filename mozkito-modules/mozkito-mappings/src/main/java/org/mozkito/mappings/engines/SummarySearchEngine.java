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
package org.mozkito.mappings.engines;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.LuceneStorage;

/**
 * The Class SummarySearchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SummarySearchEngine extends SearchEngine {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<SummarySearchEngine, ArgumentSet<SummarySearchEngine, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, SummarySearchEngine.TAG, SummarySearchEngine.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public SummarySearchEngine init() {
			// PRECONDITIONS
			
			try {
				return new SummarySearchEngine();
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
				return new HashMap<>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant description. */
	private static final String DESCRIPTION = Messages.getString("SummarySearchEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/** The Constant TAG. */
	private static final String TAG         = "summary";                                            //$NON-NLS-1$
	                                                                                                 
	/** The parser. */
	private QueryParser         parser      = null;
	
	/** The top x hits. */
	private final int           TOP_X_HITS  = 1000;
	
	/**
	 * Instantiates a new summary search engine.
	 */
	SummarySearchEngine() {
		// should only be called by settings or for testing purposes.
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return SummarySearchEngine.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Relation score) {
		
		double confidence = 0d;
		String toContent = null;
		String toSubstring = null;
		
		final LuceneStorage luceneStorage = getStorage(LuceneStorage.class);
		
		try {
			this.parser = new QueryParser(Version.LUCENE_31, "summary", luceneStorage.getAnalyzer()); //$NON-NLS-1$
			final Query query = buildQuery(element1.get(FieldKey.BODY).toString(), this.parser);
			
			if (query != null) {
				final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
				
				if (indexSearcher != null) {
					final ScoreDoc[] hits = indexSearcher.search(query, null, this.TOP_X_HITS).scoreDocs;
					// Iterate through the results:
					for (final ScoreDoc hit : hits) {
						final Document hitDoc = luceneStorage.getIsearcherReports().doc(hit.doc);
						// TODO change hardcoded strings
						final Long bugId = Long.parseLong(hitDoc.get("bugid")); //$NON-NLS-1$
						
						if ((bugId + "").compareTo(element2.get(FieldKey.ID).toString()) == 0) { //$NON-NLS-1$
							confidence = hit.score;
							toContent = element2.get(FieldKey.SUMMARY).toString();
							toSubstring = element2.get(FieldKey.SUMMARY).toString();
							
							break;
						}
					}
				}
			}
			addFeature(score, confidence, "message", element1.get(FieldKey.BODY), element1.get(FieldKey.BODY), //$NON-NLS-1$
			           "summary", toContent, toSubstring); //$NON-NLS-1$
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new And(new Atom(Index.TO, FieldKey.SUMMARY), new Atom(Index.TO, FieldKey.ID)),
		               new Atom(Index.FROM, FieldKey.BODY));
	}
	
}
