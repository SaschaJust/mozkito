/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.settings;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.TrackerFactory;
import de.unisaarland.cs.st.reposuite.bugs.tracker.TrackerType;
import de.unisaarland.cs.st.reposuite.settings.EnumArgument;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.settings.StringArgument;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TrackerArguments extends RepoSuiteArgumentSet {
	
	
	protected TrackerArguments(final RepoSuiteSettings settings, final boolean isRequired) {
		super();
		
		addArgument(new StringArgument(settings, "tracker.uri", "Where to find the data.", null, isRequired));
		addArgument(new StringArgument(settings, "tracker.baseUrl", "Base url of the tracker", null, isRequired));
		addArgument(new ListArgument(settings, "tracker.pattern",
				"The filename pattern the bugs have to match to be accepted", "*", isRequired));
		addArgument(new EnumArgument(settings, "tracker.pattern",
				"The filename pattern the bugs have to match to be accepted", null, isRequired,
				JavaUtils.enumToArray(TrackerType.BUGZILLA)));
		addArgument(new StringArgument(settings, "tracker.user", "Username to access tracker", null, false));
		addArgument(new StringArgument(settings, "tracker.password", "Password to access tracker", null, false));
		addArgument(new StringArgument(settings, "tracker.start", "BugID to start with", null, false));
		addArgument(new StringArgument(settings, "tracker.stop", "BugID to stop at", null, false));
	}
	
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Object getValue() {
		Map<String, RepoSuiteArgument> arguments = getArguments();
		
		if (JavaUtils.AnyNull(arguments.get("tracker.uri").getValue(), arguments.get("tracker.baseUrl").getValue(), arguments.get("tracker.pattern").getValue(), arguments.get("tracker.type").getValue())) {
			return null;
		}
		
		// TODO check for username ^ password != null

		TrackerType trackerType = TrackerType.valueOf(arguments.get("tracker.type").getValue().toString().toUpperCase());
		try {
			Class<? extends Tracker> trackerHandler = TrackerFactory.getTrackerHandler(trackerType);
			Tracker tracker = trackerHandler.newInstance();
			return tracker;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
}
