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

import java.io.IOException;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
 * The Class DescriptionSearchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DescriptionSearchEngine extends SearchEngine {
	
	/** The Constant description. */
	private static final String DESCRIPTION = Messages.getString("SummarySearchEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/** The Constant TAG. */
	private static final String TAG         = "report";                                             //$NON-NLS-1$
	                                                                                                 
	/** The parser. */
	private QueryParser         parser      = null;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return DescriptionSearchEngine.DESCRIPTION;
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
			final LuceneStorage luceneStorage = getStorage(LuceneStorage.class);
			
			SANITY: {
				assert from != null;
				assert to != null;
				CompareCondition.equals(to, DescriptionSearchEngine.TAG,
				                        "The target type has to be a report, but is %s.", //$NON-NLS-1$
				                        to);
				Condition.notNull(luceneStorage,
				                  "Storage 'lucene' must be available when using this engine,  but got null.");
				Condition.notNull(luceneStorage.getAnalyzer(), "Analyzer must never be null in a lucene storage.");
			}
			
			double confidence = 0d;
			String toContent = null;
			String toSubstring = null;
			
			try {
				final String fromBody = from.get(FieldKey.BODY).toString();
				final String toId = to.get(FieldKey.ID).toString();
				
				this.parser = new QueryParser(Version.LUCENE_42, "description", luceneStorage.getAnalyzer()); //$NON-NLS-1$
				final Query query = buildQuery(fromBody, this.parser);
				
				if (query != null) {
					final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
					if (indexSearcher != null) {
						final TopDocs topDocs = indexSearcher.search(query, null, 1000);
						
						if (topDocs != null) {
							final ScoreDoc[] hits = topDocs.scoreDocs;
							// Iterate through the results:
							for (final ScoreDoc hit : hits) {
								final Document hitDoc = luceneStorage.getIsearcherReports().doc(hit.doc);
								final String bugId = hitDoc.get("bugid"); //$NON-NLS-1$
								
								if (bugId.compareTo(toId) == 0) {
									confidence = hit.score;
									toContent = hitDoc.get("description"); //$NON-NLS-1$
									toSubstring = hitDoc.get("description"); //$NON-NLS-1$
									break;
								}
							}
						}
					}
				}
				
				addFeature(relation, confidence, FieldKey.BODY.name(), fromBody, query, FieldKey.BODY.name(),
				           toContent, toSubstring);
			} catch (final IOException e) {
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
						return ((Feature) object).getEngine().equals(DescriptionSearchEngine.class);
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
