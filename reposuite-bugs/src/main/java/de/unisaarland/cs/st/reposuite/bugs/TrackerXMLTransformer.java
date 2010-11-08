/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import org.jdom.Document;

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
public class TrackerXMLTransformer extends RepoSuiteTransformerThread<Tuple<Long, String>, Tuple<Long, Document>> {
	
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
			
			Tuple<Long, String> rawReport = null;
			
			while (!isShutdown() && ((rawReport = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Converting " + rawReport.getFirst() + " to XML.");
				}
				write(new Tuple<Long, Document>(rawReport.getFirst(),
				        this.tracker.createDocument(rawReport.getSecond())));
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
