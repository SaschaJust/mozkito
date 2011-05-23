/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.ioda.FileUtils;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
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
public class BackrefEngine extends MappingEngine {
	
	private double scoreBackRef;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores if the report contains a comment including a reference to the transaction id.";
	}
	
	/**
	 * @return the scoreBackRef
	 */
	public double getScoreBackRef() {
		return this.scoreBackRef;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreBackRef((Double) getSettings().getSetting(getOptionName("confidence")).getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, getOptionName("confidence"),
		                                         "Score for backreference in transaction and report.", "0.5",
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
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		if (report.getDescription().contains(transaction.getId())) {
			addFeature(score, getScoreBackRef(), "id", transaction.getId(), transaction.getId(), "description",
			           report.getDescription(), transaction.getId());
		} else {
			boolean found = false;
			for (Comment comment : report.getComments()) {
				if (comment.getMessage().contains(transaction.getId())) {
					found = true;
					addFeature(score, getScoreBackRef(), "id", transaction.getId(), transaction.getId(), "comments["
					        + comment.getId() + "]:message", comment.getMessage(), transaction.getId());
					break;
				}
			}
			if (!found) {
				StringBuilder builder = new StringBuilder();
				builder.append(report.getDescription());
				for (Comment comment : report.getComments()) {
					builder.append(FileUtils.lineSeparator);
					builder.append(comment.getMessage());
				}
				
				addFeature(score, getScoreBackRef(), "id", transaction.getId(), transaction.getId(),
				           "description,comments[*]", builder, transaction.getId());
			}
		}
	}
	
	/**
	 * @param scoreBackRef the scoreBackRef to set
	 */
	public void setScoreBackRef(final double scoreBackRef) {
		this.scoreBackRef = scoreBackRef;
	}
}
