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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.ioda.Tuple;

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
		setScoreAuthorEquality((Double) getSettings().getSetting("mapping.score.AuthorEquality").getValue());
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
		
		score.addFeature(value, "author", transaction.getAuthor().toString(), "author",
		                 report.getResolver() != null
		                                             ? report.getResolver().toString()
		                                             : "(null)", this.getClass());
	}
	
	/**
	 * @param scoreAuthorEquality the scoreAuthorEquality to set
	 */
	private void setScoreAuthorEquality(final double scoreAuthorEquality) {
		this.scoreAuthorEquality = scoreAuthorEquality;
	}
	
}
