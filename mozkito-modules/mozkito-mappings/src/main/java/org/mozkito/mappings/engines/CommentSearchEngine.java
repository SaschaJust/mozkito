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
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.LuceneStorage;

/**
 * The Class CommentSearchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CommentSearchEngine extends SearchEngine {
	
	/** The Constant description. */
	public static final String DESCRIPTION = Messages.getString("SummarySearchEngine.description"); //$NON-NLS-1$
	                                                                                                
	/** The Constant TAG. */
	public static final String TAG         = "comment";                                            //$NON-NLS-1$
	                                                                                                
	/** The parser. */
	private QueryParser        parser      = null;
	
	/** The Constant TOP_X. */
	private static final int   TOP_X       = 1000;
	
	/**
	 * Instantiates a new comment search engine.
	 * 
	 */
	public CommentSearchEngine() {
		// PRECONDITIONS
		
		try {
			// should only be used when created through settings or for testing purposes
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return CommentSearchEngine.DESCRIPTION;
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
			final MappableEntity from = relation.getFrom();
			final MappableEntity to = relation.getTo();
			double confidence = 0d;
			String toContent = null;
			String toSubstring = null;
			final LuceneStorage luceneStorage = getStorage(LuceneStorage.class);
			
			SANITY: {
				assert from != null;
				assert to != null;
				CompareCondition.equals(to.getBaseType(), Report.class,
				                        "The target type has to be a report, but is %s.", //$NON-NLS-1$
				                        to.getBaseType());
				Condition.notNull(luceneStorage,
				                  "Storage 'lucene' must be available when using this engine,  but got null.");
				Condition.notNull(luceneStorage.getAnalyzer(), "Analyzer must never be null in a lucene storage.");
			}
			
			try {
				final String fromBody = from.get(FieldKey.BODY).toString();
				final String toId = to.get(FieldKey.ID).toString();
				
				this.parser = new QueryParser(Version.LUCENE_31, "comment", luceneStorage.getAnalyzer()); //$NON-NLS-1$
				final Query query = buildQuery(fromBody, this.parser);
				
				if (query != null) {
					
					final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
					
					if (indexSearcher != null) {
						final ScoreDoc[] hits = indexSearcher.search(query, null, CommentSearchEngine.TOP_X).scoreDocs;
						// Iterate through the results:
						for (final ScoreDoc hit : hits) {
							final Document hitDoc = luceneStorage.getIsearcherReports().doc(hit.doc);
							final String bugId = hitDoc.get("bugid"); //$NON-NLS-1$
							
							if (bugId.compareTo(toId) == 0) {
								confidence = hit.score;
								toContent = hitDoc.get("comment"); //$NON-NLS-1$
								toSubstring = hitDoc.get("comment"); //$NON-NLS-1$
								break;
							}
						}
					}
				}
				
				addFeature(relation, confidence, FieldKey.BODY.name(), fromBody, query, FieldKey.COMMENT.name(),
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
						return ((Feature) object).getEngine().equals(CommentSearchEngine.class);
					}
				});
			}
		}
	}
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	@Override
	public Expression supported() {
		return new And(new And(new Atom(Index.TO, FieldKey.SUMMARY), new Atom(Index.TO, FieldKey.ID)),
		               new Atom(Index.FROM, FieldKey.BODY));
	}
	
}
