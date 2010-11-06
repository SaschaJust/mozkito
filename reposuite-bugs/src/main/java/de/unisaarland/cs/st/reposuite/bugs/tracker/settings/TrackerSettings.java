/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.settings;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerSettings extends RepoSuiteSettings {
	
	public TrackerArguments setTrackerArgs(final boolean isRequired) {
		TrackerArguments trackerArguments = new TrackerArguments(this, isRequired);
		return trackerArguments;
	}
	
}
