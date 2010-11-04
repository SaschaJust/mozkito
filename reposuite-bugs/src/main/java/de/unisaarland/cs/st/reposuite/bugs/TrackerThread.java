/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs;

import de.unisaarland.cs.st.reposuite.utils.Logger;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class TrackerThread extends Thread {
	
	protected boolean shutdown;
	
	public TrackerThread(final ThreadGroup threadGroup, final String name) {
		super(threadGroup, name);
		this.shutdown = false;
	}
	
	public final boolean isShutdown() {
		return this.shutdown;
	}
	
	public synchronized void shutdown() {
		if (!this.shutdown) {
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			this.shutdown = true;
		}
	}
	
	protected synchronized void wake() {
		notifyAll();
	}
	
}
