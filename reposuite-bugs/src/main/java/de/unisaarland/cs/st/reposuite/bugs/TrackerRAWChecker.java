/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.RepoSuiteFilterThread;
import de.unisaarland.cs.st.reposuite.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerRAWChecker extends RepoSuiteFilterThread<Tuple<Long, String>> {
	
	private final Tracker tracker;
	
	public TrackerRAWChecker(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings,
	        final Tracker tracker) {
		super(threadGroup, TrackerRAWChecker.class.getSimpleName(), settings);
		this.tracker = tracker;
	}
	
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			Tuple<Long, String> rawReport = null;
			
			while (!isShutdown() && ((rawReport = read()) != null)) {
				if (this.tracker.checkRAW(rawReport.getSecond())) {
					if (Logger.logDebug()) {
						Logger.debug("RAW report " + rawReport.getFirst() + " passed analysis.");
					}
					write(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport.getFirst() + " due to errors in raw string.");
					}
				}
			}
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
