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
public class AuthorEqualityEngine extends MappingEngine {
	
	private double scoreAuthorEquality = 0.2d;
	
	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreAuthorEquality((Double) getSettings().getSetting("mapping.score.AuthorEquality").getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings, final MappingArguments arguments, final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new DoubleArgument(settings, getOptionName("confidence"),
		        "Score for equal authors in transaction and report comments.", "0.2", isRequired));
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
	public void score(final MappableEntity from, MappableEntity to, final MapScore score) {
		if (from.get(FieldKey.AUTHOR).equals(to.get(FieldKey.AUTHOR))) {
			addFeature(score, getScoreAuthorEquality(), FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR),
			        from.get(FieldKey.AUTHOR), FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
		} else {
			addFeature(score, 0d, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR), from.get(FieldKey.AUTHOR),
			        FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
		}
	}
	
	/**
	 * @param scoreAuthorEquality
	 *            the scoreAuthorEquality to set
	 */
	private void setScoreAuthorEquality(final double scoreAuthorEquality) {
		this.scoreAuthorEquality = scoreAuthorEquality;
	}
	
	@Override
	public Expression supported() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
