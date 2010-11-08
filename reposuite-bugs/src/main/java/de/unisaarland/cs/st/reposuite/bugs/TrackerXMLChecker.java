/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import org.jdom.Document;

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
public class TrackerXMLChecker extends RepoSuiteFilterThread<Tuple<Long, Document>> {
	
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
			
			Tuple<Long, Document> rawReport = null;
			
			while (!isShutdown() && ((rawReport = read()) != null)) {
				if (this.tracker.checkXML(rawReport.getSecond())) {
					if (Logger.logDebug()) {
						Logger.debug("Report " + rawReport.getFirst() + " passed XML check.");
					}
					write(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport.getFirst() + " due to errors in XML document.");
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
