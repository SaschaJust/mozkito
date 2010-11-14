/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerXMLTransformer extends RepoSuiteTransformerThread<RawReport, XmlReport> {
	
	private final Tracker tracker;
	
	public TrackerXMLTransformer(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings,
	        final Tracker tracker) {
		super(threadGroup, TrackerXMLTransformer.class.getSimpleName(), settings);
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
				if (Logger.logDebug()) {
					Logger.debug("Converting " + rawReport + " to XML.");
				}
				write(this.tracker.createDocument(rawReport));
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
