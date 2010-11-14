/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.settings;

import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerSettings extends RepositorySettings {
	
	public TrackerArguments setTrackerArgs(final boolean isRequired) {
		TrackerArguments trackerArguments = new TrackerArguments(this, isRequired);
		return trackerArguments;
	}
	
}
