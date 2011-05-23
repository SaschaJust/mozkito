/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.History;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class AuthorEqualityEngine extends MappingEngine {
	
	private double scoreAuthorEquality = 0.2d;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores according to the equality of committer and person who closes the bug (at some time in the history).";
	}
	
	/**
	 * @return the scoreAuthorEquality
	 */
	private double getScoreAuthorEquality() {
		return this.scoreAuthorEquality;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreAuthorEquality((Double) getSettings().getSetting(getOptionName("confidence")).getValue());
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
		arguments.addArgument(new DoubleArgument(settings, getOptionName("confidence"),
		                                         "Score for equal authors in transaction and report comments.", "0.2",
		                                         isRequired));
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
		
		addFeature(score, value, "author", transaction.getAuthor(), transaction.getAuthor(), "resolver",
		           report.getResolver(), report.getResolver());
	}
	
	/**
	 * @param scoreAuthorEquality the scoreAuthorEquality to set
	 */
	private void setScoreAuthorEquality(final double scoreAuthorEquality) {
		this.scoreAuthorEquality = scoreAuthorEquality;
	}
	
}
