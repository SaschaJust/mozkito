/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Collection;
import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepoSuiteThreadGroup} is an extension of the {@link ThreadGroup}
 * and takes care on the internal management of {@link RepoSuiteThread}s. The
 * primary reasons for this class are the internal managed thread list and the
 * uncaught exception handling.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteThreadGroup extends ThreadGroup {
	
	private final Collection<RepoSuiteThread<?, ?>> threads = new LinkedList<RepoSuiteThread<?, ?>>();
	
	/**
	 * The only valid constructor of {@link RepoSuiteThreadGroup}
	 * 
	 * @param name
	 *            the name of the thread group. In general, this should be the
	 *            simple class name of the calling tool chain.
	 */
	public RepoSuiteThreadGroup(final String name) {
		super(name);
	}
	
	/**
	 * Adds a new {@link RepoSuiteThread} to the managed thread group.
	 * 
	 * @param thread
	 *            the {@link RepoSuiteThread} that shall be managed.
	 */
	public void addThread(final RepoSuiteThread<?, ?> thread) {
		this.getThreads().add(thread);
	}
	
	/**
	 * Getter for a collection containing all managed threads.
	 * 
	 * @return the threads under surveillance.
	 */
	public final Collection<RepoSuiteThread<?, ?>> getThreads() {
		return this.threads;
	}
	
	/**
	 * Shuts down all managed threads.
	 */
	public void shutdown() {
		for (RepoSuiteThread<?, ?> thread : this.getThreads()) {
			thread.shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread,
	 * java.lang.Throwable)
	 */
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
