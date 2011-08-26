package de.unisaarland.cs.st.reposuite.mapping.engines;

import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BackrefEngine extends MappingEngine {
	
	private double scoreBackRef;
	
	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreBackRef((Double) getSettings().getSetting("mapping.score.BackRef").getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, "mapping.score.BackRef",
		        "Score for backreference in transaction and report.", "0.5", isRequired));
	}
	
	/**
	 * @param scoreBackRef
	 *            the scoreBackRef to set
	 */
	public void setScoreBackRef(final double scoreBackRef) {
		this.scoreBackRef = scoreBackRef;
	}
	
	@Override
	public Expression supported() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void score(MappableEntity element1, MappableEntity element2, MapScore score) {
		String fullText = element2.getText();
		String id = element1.get(FieldKey.ID).toString();
		
		if (fullText.contains(id.toString())) {
			score.addFeature(getScoreBackRef(), FieldKey.ID.name(), id, id, "FULLTEXT", truncate(fullText),
			        truncate(fullText), this.getClass());
			//		} else {
			//			boolean found = false;
			//			for (Comment comment : report.getComments()) {
			//				if (comment.getMessage().contains(transaction.getId())) {
			//					found = true;
			//					score.addFeature(getScoreBackRef(), "id", transaction.getId(), "comment" + comment.getId()
			//					        + ":message", truncate(comment.getMessage()), this.getClass());
			//					break;
			//				}
			//			}
			//			if (!found) {
			//				score.addFeature(0, "id", transaction.getId(), "report", "description|comments", this.getClass());
			//			}
		}
	}
}
