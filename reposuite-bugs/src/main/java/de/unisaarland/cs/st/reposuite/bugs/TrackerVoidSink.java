/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerVoidSink extends RepoSuiteSinkThread<Report> {
	
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
