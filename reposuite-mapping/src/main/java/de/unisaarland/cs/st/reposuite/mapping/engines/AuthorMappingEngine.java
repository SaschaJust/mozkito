/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class AuthorMappingEngine extends MappingEngine {
	
	private static double scoreAuthorEquality = 0.2d;
	
	/**
	 * @return the scoreAuthorEquality
	 */
	private static double getScoreAuthorEquality() {
		return scoreAuthorEquality;
	}
	
	/**
	 * @param scoreAuthorEquality the scoreAuthorEquality to set
	 */
	private static void setScoreAuthorEquality(final double scoreAuthorEquality) {
		AuthorMappingEngine.scoreAuthorEquality = scoreAuthorEquality;
	}
	
	/**
	 * @param settings
	 */
	public AuthorMappingEngine(final MappingSettings settings) {
		super(settings);
		setScoreAuthorEquality((Double) getSettings().getSetting("mapping.score.AuthorEquality").getValue());
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
		double value = 0d;
		
		for (Comment comment : report.getComments()) {
			if (comment.getAuthor().equals(transaction.getAuthor())) {
				value += getScoreAuthorEquality();
				break;
			}
		}
		
		score.addFeature(value, "author", transaction.getAuthor().toString(), this.getClass());
	}
	
}
