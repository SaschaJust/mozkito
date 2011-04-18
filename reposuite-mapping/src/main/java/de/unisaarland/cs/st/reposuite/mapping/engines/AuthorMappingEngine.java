/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.History;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class AuthorMappingEngine extends MappingEngine {
	
	private static double scoreAuthorEquality = 0.2d;
	private static double scoreAuthorInequality;
	
	/**
	 * @return the scoreAuthorEquality
	 */
	private static double getScoreAuthorEquality() {
		return scoreAuthorEquality;
	}
	
	/**
	 * @return the scoreAuthorInequality
	 */
	public static double getScoreAuthorInequality() {
		return scoreAuthorInequality;
	}
	
	/**
	 * @param scoreAuthorEquality the scoreAuthorEquality to set
	 */
	private static void setScoreAuthorEquality(final double scoreAuthorEquality) {
		AuthorMappingEngine.scoreAuthorEquality = scoreAuthorEquality;
	}
	
	/**
	 * @param scoreAuthorInequality the scoreAuthorInequality to set
	 */
	public static void setScoreAuthorInequality(final double scoreAuthorInequality) {
		AuthorMappingEngine.scoreAuthorInequality = scoreAuthorInequality;
	}
	
	/**
	 * @param settings
	 */
	public AuthorMappingEngine(final MappingSettings settings) {
		super(settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreAuthorEquality((Double) getSettings().getSetting("mapping.score.AuthorEquality").getValue());
		setScoreAuthorInequality((Double) getSettings().getSetting("mapping.score.AuthorInequality").getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.AuthorEquality",
		                                         "Score for equal authors in transaction and report comments.", "0.2",
		                                         isRequired));
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.AuthorInequality",
		                                         "Score for not equal authors in transaction and report comments.",
		                                         "-0.8", isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		double value = 0d;
		boolean resolved = false;
		
		if (Logger.logDebug()) {
			Logger.debug("Looking up changes in resolution for report " + report.getId() + ".");
		}
		
		History history = report.getHistory().get(Resolution.class.getSimpleName().toLowerCase());
		if (!history.isEmpty()) {
			if (Logger.logDebug()) {
				Logger.debug("Found " + history.size() + " changes of resolution in " + report.getId() + ".");
			}
			
			for (HistoryElement element : history.getElements()) {
				if (((Tuple<Resolution, Resolution>) element.get(Resolution.class.getSimpleName().toLowerCase())).getSecond() == Resolution.RESOLVED) {
					if (Logger.logDebug()) {
						Logger.debug("Found history entry that marks the report as " + Resolution.RESOLVED.name() + ".");
					}
					
					resolved = true;
					
					if (element.getAuthor().equals(transaction.getAuthor())) {
						value += getScoreAuthorEquality();
						if (Logger.logDebug()) {
							Logger.debug("Authors of transaction and resolution match: " + transaction.getAuthor());
						}
						break;
					} else {
						if (Logger.logDebug()) {
							Logger.debug("But author " + element.getAuthor() + " does not match transaction author "
							        + transaction.getAuthor());
						}
					}
				}
			}
		}
		
		// in case the report was marked as resolved but not by the same author
		// score inquality
		if (resolved && (Double.compare(value, 0d) == 0)) {
			value += getScoreAuthorInequality();
		}
		
		score.addFeature(value, "author", transaction.getAuthor().toString(), this.getClass());
	}
	
}
