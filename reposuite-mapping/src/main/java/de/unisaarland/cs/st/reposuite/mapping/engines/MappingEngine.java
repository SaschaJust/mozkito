/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingEngine extends Registered {
	
	public static final String unused          = "(unused)";
	public static final String unknown         = "(unknown)";
	public static final String defaultNegative = "-1";
	public static final String defaultPositive = "1";
	
	/**
	 * @param score
	 * @param confidence
	 * @param transactionFieldName
	 * @param transactionFieldContent
	 * @param transactionSubstring
	 * @param reportFieldName
	 * @param reportFieldContent
	 * @param reportSubstring
	 */
	
	public void addFeature(@NotNull final MapScore score,
	                       final double confidence,
	                       @NotNull @NotEmpty final String transactionFieldName,
	                       final Object transactionFieldContent,
	                       final Object transactionSubstring,
	                       @NotNull @NotEmpty final String reportFieldName,
	                       final Object reportFieldContent,
	                       final Object reportSubstring) {
		score.addFeature(confidence,
		                 truncate(transactionFieldName),
		                 truncate(transactionFieldContent != null
		                                                         ? transactionFieldContent.toString()
		                                                         : unused),
		                 truncate(transactionSubstring != null
		                                                      ? transactionSubstring.toString()
		                                                      : truncate(transactionFieldContent != null
		                                                                                                ? transactionFieldContent.toString()
		                                                                                                : unused)),
		                 truncate(reportFieldName),
		                 truncate(reportFieldContent != null
		                                                    ? reportFieldContent.toString()
		                                                    : unused),
		                 truncate(reportSubstring != null
		                                                 ? reportSubstring.toString()
		                                                 : truncate(reportFieldContent != null
		                                                                                      ? reportFieldContent.toString()
		                                                                                      : unused)), getClass());
	}
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract void score(final RCSTransaction transaction,
	                           final Report report,
	                           final MapScore score);
}
