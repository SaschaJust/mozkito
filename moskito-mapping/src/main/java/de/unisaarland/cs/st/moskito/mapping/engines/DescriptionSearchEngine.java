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

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.CompareCondition;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.MapScore;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.mapping.storages.LuceneStorage;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class DescriptionSearchEngine extends SearchEngine {
	
	private QueryParser parser;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.register.Registered#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores based on document similarity/relevance based on commit message and report description.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.model.MapScore)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final MapScore score) {
		CompareCondition.equals(to.getBaseType(), Report.class, "The target type has to be a report, but is %s.",
		                        to.getBaseType());
		
		try {
			final String fromBody = from.get(FieldKey.BODY).toString();
			final String toId = to.get(FieldKey.ID).toString();
			
			this.parser = new QueryParser(Version.LUCENE_31, "description", getStorage().getAnalyzer());
			final Query query = buildQuery(fromBody, this.parser);
			
			final LuceneStorage luceneStorage = getStorage();
			final IndexSearcher indexSearcher = luceneStorage.getIsearcherReports();
			if (indexSearcher != null) {
				final TopDocs topDocs = indexSearcher.search(query, null, 1000);
				
				if (topDocs != null) {
					final ScoreDoc[] hits = topDocs.scoreDocs;
					// Iterate through the results:
					for (final ScoreDoc hit : hits) {
						final Document hitDoc = getStorage().getIsearcherReports().doc(hit.doc);
						final String bugId = hitDoc.get("bugid");
						
						if (bugId.compareTo(toId) == 0) {
							score.addFeature(hit.score, FieldKey.BODY.name(), truncate(fromBody),
							                 truncate(query.toString()), FieldKey.BODY.name(),
							                 truncate(hitDoc.get("comment")), truncate(hitDoc.get("comment")),
							                 this.getClass());
							break;
						}
					}
				}
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new And(new Atom(Index.TO, MappableReport.class), new Atom(Index.TO, FieldKey.ID)),
		               new Atom(Index.FROM, FieldKey.BODY));
	}
	
}
