/**
 * 
 */
package net.ownhero.dev.andama.model;

import java.util.Collection;
import java.util.LinkedList;

import net.ownhero.dev.andama.settings.AndamaSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AndamaGroup} is an extension of the {@link ThreadGroup}
 * and takes care on the internal management of {@link AndamaThread}s. The
 * primary reasons for this class are the internal managed thread list and the
 * uncaught exception handling.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaGroup extends ThreadGroup {
	
	final Logger                                 logger  = LoggerFactory.getLogger(this.getClass());
	
	private final Collection<AndamaThread<?, ?>> threads = new LinkedList<AndamaThread<?, ?>>();
	private final AndamaSettings                 settings;
	private final AndamaChain                    toolchain;
	
	/**
	 * The only valid constructor of {@link AndamaGroup}
	 * 
	 * @param name
	 *            the name of the thread group. In general, this should be the
	 *            simple class name of the calling tool chain.
	 */
	public AndamaGroup(final String name, final AndamaChain toolchain) {
		super(name);
		AndamaCrashHandler.init(toolchain);
		this.toolchain = toolchain;
		this.settings = toolchain.getSettings();
	}
	
	/**
	 * Adds a new {@link AndamaThread} to the managed thread group.
	 * 
	 * @param thread
	 *            the {@link AndamaThread} that shall be managed.
	 */
	public void addThread(final AndamaThread<?, ?> thread) {
		this.getThreads().add(thread);
	}
	
	/**
	 * @return
	 */
	protected String getRepoSuiteSettings() {
		return this.settings.toString();
	}
	
	/**
	 * Getter for a collection containing all managed threads.
	 * 
	 * @return the threads under surveillance.
	 */
	public final Collection<AndamaThread<?, ?>> getThreads() {
		return this.threads;
	}
	
	/**
	 * @return the toolchain
	 */
	public final AndamaChain getToolchain() {
		return this.toolchain;
	}
	
	/**
	 * @return
	 */
	protected String getToolInformation() {
		return this.settings.getToolInformation();
	}
	
	/**
	 * Shuts down all managed threads.
	 */
	public void shutdown() {
		for (AndamaThread<?, ?> thread : this.getThreads()) {
			thread.shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread,
	 * java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t,
	                              final Throwable e) {
		super.uncaughtException(t, e);
		if (this.logger.isErrorEnabled()) {
			this.logger.error("Thread " + t.getName() + " terminated with uncaught exception " + e.getClass().getName()
			        + ". Message: " + e.getMessage(), e);
			this.logger.error("Shutting down.");
		}
		shutdown();
	}
	
}
