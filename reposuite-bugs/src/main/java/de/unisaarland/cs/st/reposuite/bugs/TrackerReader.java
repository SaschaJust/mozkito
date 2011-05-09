/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import java.net.URI;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSourceThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerReader extends RepoSuiteSourceThread<RawReport> {
	
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
			
			Long bugId = null;
			
			while (!isShutdown() && ((bugId = this.tracker.getNextId()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Fetching " + bugId + ".");
				}
				URI newURI = this.tracker.getLinkFromId(bugId);
				RawReport source = this.tracker.fetchSource(newURI);
				if (source == null) {
					
					if (Logger.logWarn()) {
						Logger.warn("Skipping: " + bugId + ". Fetch returned null.");
					}
				} else {
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + bugId + ".");
					}
					write(source);
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
