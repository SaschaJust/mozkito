/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.BugReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerVoidSink extends RepoSuiteSinkThread<BugReport> {
	
	public TrackerVoidSink(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings) {
		super(threadGroup, TrackerVoidSink.class.getSimpleName(), settings);
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
			
			Object bugReport;
			
			while (!isShutdown() && ((bugReport = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Void sinking " + bugReport + ".");
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
