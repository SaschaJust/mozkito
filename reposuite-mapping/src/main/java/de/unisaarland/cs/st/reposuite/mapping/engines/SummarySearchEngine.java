package de.unisaarland.cs.st.reposuite.mapping.engines;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SummarySearchEngine extends SearchEngine {
	
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
		return "Scores based on document similarity/relevance based on commit message and report summary.";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new And(new Atom(Index.TO, FieldKey.SUMMARY), new Atom(Index.TO, FieldKey.ID)), new Atom(
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
	public void score(MappableEntity element1, MappableEntity element2, MapScore score) {
		try {
			this.parser = new QueryParser(Version.LUCENE_31, "summary", getStorage().getAnalyzer());
			Query query = buildQuery(element1.get(FieldKey.BODY).toString(), this.parser);
			
			ScoreDoc[] hits = getStorage().getIsearcherReports().search(query, null, 1000).scoreDocs;
			// Iterate through the results:
			for (ScoreDoc hit : hits) {
				Document hitDoc = getStorage().getIsearcherReports().doc(hit.doc);
				// TODO change hardcoded strings
				Long bugId = Long.parseLong(hitDoc.get("bugid"));
				
				if ((bugId + "").compareTo(element2.get(FieldKey.ID).toString()) == 0) {
					score.addFeature(hit.score, "message", truncate(element1.get(FieldKey.BODY).toString()),
					        truncate(element1.get(FieldKey.BODY).toString()), "summary",
					        truncate(element2.get(FieldKey.SUMMARY).toString()), truncate(element2
					                .get(FieldKey.SUMMARY).toString()), this.getClass());
					break;
				}
			}
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
