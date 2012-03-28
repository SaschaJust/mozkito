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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.CompareCondition;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.mapping.storages.LuceneStorage;

/**
 * The Class CommentSearchEngine.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CommentSearchEngine extends SearchEngine {
	
	/** The parser. */
	private QueryParser         parser      = null;
	
	/** The Constant description. */
	private static final String description = Messages.getString("CommentSearchEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return description;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.SearchEngine#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		
		try {
			super.init();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.SearchEngine#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			return super.provide(root);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Mapping score) {
		CompareCondition.equals(to.getBaseType(), Report.class, "The target type has to be a report, but is %s.", //$NON-NLS-1$
		                        to.getBaseType());
		double confidence = 0d;
		String toContent = null;
		String toSubstring = null;
		try {
			final String fromBody = from.get(FieldKey.BODY).toString();
			final String toId = to.get(FieldKey.ID).toString();
			
			this.parser = new QueryParser(Version.LUCENE_31, "comment", getStorage().getAnalyzer()); //$NON-NLS-1$
			final Query query = buildQuery(fromBody, this.parser);
			
			if (query != null) {
				final LuceneStorage luceneStorage = getStorage();
				
				final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
				
				if (indexSearcher != null) {
					final ScoreDoc[] hits = indexSearcher.search(query, null, 1000).scoreDocs;
					// Iterate through the results:
					for (final ScoreDoc hit : hits) {
						final Document hitDoc = getStorage().getIsearcherReports().doc(hit.doc);
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
			
			addFeature(score, confidence, FieldKey.BODY.name(), fromBody, query, FieldKey.COMMENT.name(), toContent,
			           toSubstring);
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
		return new And(new And(new Atom(Index.TO, MappableReport.class), new Atom(Index.TO, FieldKey.ID)),
		               new Atom(Index.FROM, FieldKey.BODY));
	}
	
}
