package de.unisaarland.cs.st.reposuite.mapping.strategies;

import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SVMStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the trained model on known mappings with the given feature vectors from the MappingEngines.";
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void loadModel() {
		// REMARK
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
