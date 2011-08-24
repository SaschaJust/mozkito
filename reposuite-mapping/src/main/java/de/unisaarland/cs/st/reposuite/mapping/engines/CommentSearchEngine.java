/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.kanuni.conditions.CompareCondition;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CommentSearchEngine extends SearchEngine {
	
	private QueryParser parser = null;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores based on document similarity/relevance based on commit message and report comments.";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new And(new Atom(Index.TO, MappableReport.class), new Atom(Index.TO, FieldKey.ID)), new Atom(
		        Index.FROM, FieldKey.BODY));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(MappableEntity from, MappableEntity to, MapScore score) {
		CompareCondition.equals(to.getBaseType(), Report.class, "The target type has to be a report, but is %s.",
		        to.getBaseType());
		
		try {
			String fromBody = from.get(FieldKey.BODY).toString();
			String toId = to.get(FieldKey.ID).toString();
			
			this.parser = new QueryParser(Version.LUCENE_31, "comment", getStorage().getAnalyzer());
			Query query = buildQuery(fromBody, this.parser);
			
			ScoreDoc[] hits = getStorage().getIsearcherReports().search(query, null, 1000).scoreDocs;
			// Iterate through the results:
			for (ScoreDoc hit : hits) {
				Document hitDoc = getStorage().getIsearcherReports().doc(hit.doc);
				String bugId = hitDoc.get("bugid");
				
				if (bugId.compareTo(toId) == 0) {
					score.addFeature(hit.score, FieldKey.BODY.name(), truncate(fromBody), truncate(query.toString()),
					        FieldKey.COMMENT.name(), truncate(hitDoc.get("comment")), truncate(hitDoc.get("comment")),
					        this.getClass());
					break;
				}
			}
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
