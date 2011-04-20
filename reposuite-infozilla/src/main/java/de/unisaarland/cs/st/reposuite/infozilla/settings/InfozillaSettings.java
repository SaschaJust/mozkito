/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.settings;

import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class InfozillaSettings extends TrackerSettings {
	
	/**
	 * 
	 */
	public InfozillaArguments setInfozillaArgs(final boolean isRequired) {
		InfozillaArguments infozillaArguments = new InfozillaArguments(this, isRequired);
		return infozillaArguments;
	}
	
}
