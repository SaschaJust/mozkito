/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteFilterThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerRAWChecker extends RepoSuiteFilterThread<RawReport> {
	
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
			
			RawReport rawReport = null;
			
			while (!isShutdown() && ((rawReport = read()) != null)) {
				if (this.tracker.checkRAW(rawReport)) {
					if (Logger.logDebug()) {
						Logger.debug("RAW report " + rawReport + " passed analysis.");
					}
					write(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport + " due to errors in raw string.");
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
