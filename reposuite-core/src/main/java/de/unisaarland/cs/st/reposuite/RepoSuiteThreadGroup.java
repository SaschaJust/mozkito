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
public class RepoSuiteThreadGroup extends ThreadGroup {
	
	private final Collection<RepoSuiteThread> threads = new LinkedList<RepoSuiteThread>();
	
	public RepoSuiteThreadGroup(final String name) {
		super(name);
	}
	
	public void addThread(final RepoSuiteThread thread) {
		this.getThreads().add(thread);
	}
	
	/**
	 * @return the threads
	 */
	public final  Collection<RepoSuiteThread> getThreads() {
		return this.threads;
	}
	
	public void shutdown() {
		for (RepoSuiteThread thread : this.getThreads()) {
			thread.shutdown();
		}
	}
	
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		
		if (Logger.logError()) {
			Logger.error("Thread " + t.getName() + " terminated with uncaught exception " + e.getClass().getName()
					+ ". Message: " + e.getMessage(), e);
			Logger.error("Shutting down.");
		}
		shutdown();
	}
}
