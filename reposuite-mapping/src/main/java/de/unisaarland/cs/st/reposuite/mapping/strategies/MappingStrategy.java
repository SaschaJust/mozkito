/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingStrategy {
	
	private MappingSettings settings;
	
	/**
	 * @param settings
	 */
	public MappingStrategy(final MappingSettings settings) {
		setSettings(settings);
	}
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * @return
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return
	 */
	public MappingSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * 
	 */
	public abstract void init();
	
	/**
	 * @param mapping
	 * @return
	 */
	public abstract RCSBugMapping map(RCSBugMapping mapping);
	
	/**
	 * @param settings
	 * @param mappingArguments
	 * @param isRequired
	 */
	public abstract void register(MappingSettings settings,
	                              MappingArguments mappingArguments,
	                              boolean isRequired);
	
	/**
	 * @param settings
	 */
	public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
}
