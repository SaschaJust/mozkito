package de.unisaarland.cs.st.reposuite.mapping.engines;

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
public class EssentialChangeEngine extends MappingEngine {
	
	private double confidence;
	
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
		return "Scores negative if the changes done in the transaction aren't essential changes (no interface changes, renamings etc...)";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		// TODO set the values of your registered config options
		setConfidence((Double) getSettings().getSetting("mapping.engine." + getHandle().toLowerCase() + ".confidence")
		        .getValue());
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
		// TODO register further config options if you need some
		arguments.addArgument(new DoubleArgument(settings, "mapping.engine." + getHandle().toLowerCase()
		        + ".confidence", "Confidence that is used if the changes done in the transaction arent essential.",
		        "-1", isRequired));
	}
	
	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	@Override
	public Expression supported() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void score(MappableEntity from, MappableEntity to, MapScore score) {
		// TODO Auto-generated method stub
		
	}
	
}
