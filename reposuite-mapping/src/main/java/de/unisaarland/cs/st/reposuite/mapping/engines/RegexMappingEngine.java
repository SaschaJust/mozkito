/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.regex.Pattern;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class RegexMappingEngine extends MappingEngine {
	
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getSimpleName() + " checking " + transaction);
		}
		
		Regex regex = new Regex("({match}JAXEN-" + report.getId() + ")", Pattern.CASE_INSENSITIVE);
		
		if (regex.find(transaction.getMessage()) != null) {
			score.addFeature(1.0d, "message", regex.getGroup("match"), this.getClass());
		}
	}
	
}
