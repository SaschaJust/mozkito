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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.LuceneStorage;
import org.mozkito.persistence.FieldKey;

/**
 * The Class SummarySearchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SummarySearchEngine extends SearchEngine {
	
	/** The Constant description. */
	public static final String DESCRIPTION = Messages.getString("SummarySearchEngine.description"); //$NON-NLS-1$
	                                                                                                
	/** The Constant TAG. */
	public static final String TAG         = "summary";                                            //$NON-NLS-1$
	                                                                                                
	/** The top x hits. */
	private final int          TOP_X_HITS  = 1000;
	
	/**
	 * Instantiates a new summary search engine.
	 */
	public SummarySearchEngine() {
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final @NotNull Relation relation) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final org.mozkito.persistence.Entity from = relation.getFrom();
			final org.mozkito.persistence.Entity to = relation.getTo();
			
			SANITY: {
				assert from != null;
				assert to != null;
			}
			
			double confidence = 0d;
			String toContent = null;
			String toSubstring = null;
			
			final LuceneStorage luceneStorage = getStorage(LuceneStorage.class);
			
			try {
				final QueryParser parser = new QueryParser(Version.LUCENE_42, "summary", luceneStorage.getAnalyzer()); //$NON-NLS-1$
				final Query query = buildQuery(from.get(FieldKey.BODY).toString(), parser);
				
				if (query != null) {
					final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
					
					if (indexSearcher != null) {
						final ScoreDoc[] hits = indexSearcher.search(query, null, this.TOP_X_HITS).scoreDocs;
						// Iterate through the results:
						for (final ScoreDoc hit : hits) {
							final Document hitDoc = luceneStorage.getIsearcherReports().doc(hit.doc);
							// TODO change hardcoded strings
							final Long bugId = Long.parseLong(hitDoc.get("bugid")); //$NON-NLS-1$
							
							if ((bugId + "").compareTo(to.get(FieldKey.ID).toString()) == 0) { //$NON-NLS-1$
								confidence = hit.score;
								toContent = to.get(FieldKey.SUMMARY).toString();
								toSubstring = to.get(FieldKey.SUMMARY).toString();
								
								break;
							}
						}
					}
				}
				addFeature(relation, confidence, "message", from.get(FieldKey.BODY), from.get(FieldKey.BODY), //$NON-NLS-1$
				           "summary", toContent, toSubstring); //$NON-NLS-1$
			} catch (final Exception e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			POSTCONDITIONS: {
				assert CollectionUtils.exists(relation.getFeatures(), new Predicate() {
					
					/**
					 * {@inheritDoc}
					 * 
					 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
					 */
					@Override
					public boolean evaluate(final Object object) {
						return ((Feature) object).getEngine().equals(SummarySearchEngine.class);
					}
				});
			}
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
