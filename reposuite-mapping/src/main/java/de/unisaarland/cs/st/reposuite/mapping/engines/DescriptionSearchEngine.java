/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class DescriptionSearchEngine extends SearchEngine {
	
	private QueryParser parser;
	
	@Override
	public String getDescription() {
		return "Scores based on document similarity/relevance based on commit message and report description.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		
		try {
			this.parser = new QueryParser(Version.LUCENE_31, "description", getStorage().getAnalyzer());
			Query query = buildQuery(transaction.getMessage(), this.parser);
			
			ScoreDoc[] hits = getStorage().getIsearcherReports().search(query, null, 1000).scoreDocs;
			// Iterate through the results:
			for (ScoreDoc hit : hits) {
				Document hitDoc = getStorage().getIsearcherReports().doc(hit.doc);
				Long bugId = Long.parseLong(hitDoc.get("bugid"));
				
				if (bugId.compareTo(report.getId()) == 0) {
					score.addFeature(hit.score, "message", truncate(transaction.getMessage()), "description",
					                 truncate(report.getSummary()), this.getClass());
					break;
				}
			}
		} catch (Exception e) {
			throw new UnrecoverableError(e);
		}
	}
	
}
