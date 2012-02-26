/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.CrashHandler;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * The {@link Group} is an extension of the {@link ThreadGroup} and takes care on the internal management of
 * {@link Node}s. The primary reasons for this class are the internal managed thread list and the uncaught exception
 * handling.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Group extends ThreadGroup {
	
	private final Settings          settings;
	private final List<INode<?, ?>> threads = new LinkedList<INode<?, ?>>();
	private final Chain<?>          toolchain;
	
	/**
	 * The only valid constructor of {@link Group}
	 * 
	 * @param name
	 *            the name of the thread group. In general, this should be the simple class name of the calling tool
	 *            chain.
	 */
	public Group(final String name, final Chain<?> toolchain) {
		super(name);
		CrashHandler.init(toolchain);
		this.toolchain = toolchain;
		this.settings = toolchain.getSettings();
	}
	
	/**
	 * Adds a new {@link Node} to the managed thread group.
	 * 
	 * @param thread
	 *            the {@link Node} that shall be managed.
	 */
	public Integer addThread(final Node<?, ?> thread) {
		if (getThreads().add(thread)) {
			return getThreads().indexOf(thread);
		} else {
			throw new UnrecoverableError("Could not add thread " + thread.toString() + " to threadGroup " + getName()
			        + ".");
		}
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
	public final List<INode<?, ?>> getThreads() {
		return this.threads;
	}
	
	/**
	 * @return the toolchain
	 */
	public final Chain<?> getToolchain() {
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
		for (final INode<?, ?> thread : getThreads()) {
			thread.shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t,
	                              final Throwable e) {
		super.uncaughtException(t, e);
		
		if (Logger.logError()) {
			Logger.error("Thread " + t.getName() + " terminated with uncaught exception " + e.getClass().getName()
			        + ". Message: " + e.getMessage(), e);
			Logger.error("Shutting down.");
		}
		shutdown();
	}
	
}
