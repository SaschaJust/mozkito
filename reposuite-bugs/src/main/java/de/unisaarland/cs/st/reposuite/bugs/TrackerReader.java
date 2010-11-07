/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import java.net.URI;

import de.unisaarland.cs.st.reposuite.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerReader extends RepoSuiteSourceThread<Tuple<String, String>> {
	
	private final Tracker tracker;
	
	/**
	 * @param threadGroup
	 * @param tracker
	 */
	public TrackerReader(final RepoSuiteThreadGroup threadGroup, final TrackerSettings settings, final Tracker tracker) {
		super(threadGroup, TrackerReader.class.getSimpleName(), settings);
		this.tracker = tracker;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			String bugId = null;
			
			while (!isShutdown() && ((bugId = this.tracker.getNextId()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Fetching " + bugId + ".");
				}
				URI newURI = this.tracker.getLinkFromId(bugId);
				Tuple<String, String> source = this.tracker.fetchSource(newURI);
				write(new Tuple<String, String>(bugId, source.getSecond()));
			}
			
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
	
}
