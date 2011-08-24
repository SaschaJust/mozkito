package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.ioda.JavaUtils;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Or;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.settings.DoubleArgument;
import de.unisaarland.cs.st.reposuite.settings.EnumArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportTypeEngine extends MappingEngine {
	
	private double confidence;
	
	private Type   type;
	
	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report isn't of the specified type.";
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		
		setConfidence((Double) getSettings().getSetting("mapping.engine." + getHandle().toLowerCase() + ".confidence")
		        .getValue());
		setType((Type) getSettings().getSetting("mapping.engine." + getHandle().toLowerCase() + ".type").getValue());
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
		arguments
		        .addArgument(new DoubleArgument(settings,
		                "mapping.engine." + getHandle().toLowerCase() + ".confidence",
		                "Confidence that is used if the report isn't of the specified type.", "-1", isRequired));
		arguments.addArgument(new EnumArgument(settings, "mapping.engine." + getHandle().toLowerCase() + ".type",
		        "Type the report has to match, e.g. BUG.", null, isRequired, JavaUtils.enumToArray(Type.BUG)));
	}
	
	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Or(new Atom(Index.FROM, Report.class), new Atom(Index.TO, Report.class));
		
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
	public void score(MappableEntity element1, MappableEntity element2, MapScore score) {
		// FIXME this should work on reports only
		if (element2.get(FieldKey.TYPE) != getType()) {
			score.addFeature(getConfidence(), unused, unknown, unknown, FieldKey.TYPE.name(),
			        element2.get(FieldKey.TYPE).toString(), element2.get(FieldKey.TYPE).toString(), this.getClass());
		}
	}
	
}
