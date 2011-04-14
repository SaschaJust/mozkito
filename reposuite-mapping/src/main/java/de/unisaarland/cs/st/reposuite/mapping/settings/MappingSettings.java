/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.settings;

import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingSettings extends TrackerSettings {
	
	/**
	 * @param isRequired
	 * @return
	 */
	public MappingArguments setMappingArgs(final boolean isRequired) {
		MappingArguments mappingArguments = new MappingArguments(this, isRequired);
		return mappingArguments;
	}
	
}
