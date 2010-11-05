/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Collection;
import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteThread extends Thread implements RepoSuiteGeneralThread {
	
	protected boolean shutdown;
	protected Collection<RepoSuiteGeneralThread> knownThreads = new LinkedList<RepoSuiteGeneralThread>();
	protected RepoSuiteThreadGroup               threadGroup;
	
	public RepoSuiteThread(final RepoSuiteThreadGroup threadGroup, final String name) {
		super(threadGroup, name);
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.shutdown = false;
	}
	
	@Override
	public boolean checkConnections() {
		boolean retval = true;
		if (!isInputConnected()) {
			if (Logger.logError()) {
				Logger.error(getHandle() + " is not input connected (required to run this task).");
			}
			retval = false;
		}
		
		if (!isOutputConnected()) {
			if (Logger.logError()) {
				Logger.error(getHandle() + " is not out connected (required to run this task).");
			}
			retval = false;
		}
		
		if (retval && this.knownThreads.isEmpty()) {
			if (Logger.logError()) {
				Logger.error(getHandle()
						+ " has known connections, but knownThreads is empty. This should never happen.");
			}
			retval = false;
		}
		
		if (!retval) {
			
			if (Logger.logError()) {
				Logger.error("Shutting all threads down.");
				this.threadGroup.shutdown();
			}
		}
		return retval;
	}
	
	@Override
	public boolean checkNotShutdown() {
		if (isShutdown()) {
			
			if (Logger.logError()) {
				Logger.error("Thread already shut down. Won't run again.");
			}
		}
		return !isShutdown();
	}
	
	@Override
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	public final synchronized boolean isShutdown() {
		return this.shutdown;
	}
	
	public void shutdown() {
		if (!this.shutdown) {
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			this.shutdown = true;
			
			for (RepoSuiteGeneralThread thread : this.knownThreads) {
				thread.shutdown();
			}
		}
	}
	
	public synchronized void wake() {
		notifyAll();
	}
	
}
