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
public class TrackerXMLTransformer extends RepoSuiteTransformerThread<Tuple<String, String>, Tuple<String, Document>> {
	
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
			
			Tuple<String, String> rawReport = null;
			
			while (!isShutdown() && ((rawReport = this.inputStorage.read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Converting " + rawReport + " to XML.");
				}
				this.outputStorage.write(new Tuple<String, Document>(rawReport.getFirst(), this.tracker
				        .createDocument(rawReport.getSecond())));
			}
			
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
