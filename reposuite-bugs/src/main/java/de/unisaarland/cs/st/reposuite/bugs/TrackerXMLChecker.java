/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteFilterThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerXMLChecker extends RepoSuiteFilterThread<XmlReport> {
	
	private final Tracker tracker;
	
	public TrackerXMLChecker(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings,
	        final Tracker tracker) {
		super(threadGroup, TrackerXMLChecker.class.getSimpleName(), settings);
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
			
			XmlReport rawReport = null;
			
			while (!isShutdown() && ((rawReport = read()) != null)) {
				if (this.tracker.checkXML(rawReport)) {
					if (Logger.logDebug()) {
						Logger.debug("Report " + rawReport + " passed XML check.");
					}
					write(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport + " due to errors in XML document.");
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
