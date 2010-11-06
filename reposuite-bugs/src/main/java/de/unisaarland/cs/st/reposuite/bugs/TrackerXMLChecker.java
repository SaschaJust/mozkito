/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import org.dom4j.Document;

import de.unisaarland.cs.st.reposuite.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerXMLChecker extends RepoSuiteTransformerThread<Tuple<String, Document>, Tuple<String, Document>> {
	
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
			
			Tuple<String, Document> rawReport = null;
			
			while (!isShutdown() && ((rawReport = this.inputStorage.read()) != null)) {
				if (this.tracker.checkXML(rawReport.getSecond())) {
					if (Logger.logDebug()) {
						Logger.debug("Report " + rawReport.getFirst() + " passed XML check.");
					}
					this.outputStorage.write(rawReport);
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report " + rawReport.getFirst() + " due to errors in raw string.");
					}
				}
			}
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
