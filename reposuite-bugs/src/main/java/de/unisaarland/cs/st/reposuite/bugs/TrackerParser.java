/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerParser extends RepoSuiteTransformerThread<XmlReport, Report> {
	
	private final Tracker tracker;
	
	/**
	 * @param threadGroup
	 * @param tracker
	 */
	public TrackerParser(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, TrackerParser.class.getSimpleName(), settings);
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
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + rawReport + ".");
				}
				Report report = this.tracker.parse(rawReport);
				write(report);
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
