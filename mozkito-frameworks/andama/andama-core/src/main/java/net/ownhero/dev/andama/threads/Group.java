/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package net.ownhero.dev.andama.threads;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.CrashHandler;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The {@link Group} is an extension of the {@link ThreadGroup} and takes care on the internal management of Nodes. The
 * primary reasons for this class are the internal managed thread list and the uncaught exception handling.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Group extends ThreadGroup {
	
	/** The settings. */
	private final Settings          settings;
	
	/** The threads. */
	private final List<INode<?, ?>> threads = new LinkedList<INode<?, ?>>();
	
	/** The toolchain. */
	private final Chain<?>          toolchain;
	
	/**
	 * The only valid constructor of {@link Group}.
	 * 
	 * @param name
	 *            the name of the thread group. In general, this should be the simple class name of the calling tool
	 *            chain.
	 * @param toolchain
	 *            the toolchain
	 */
	public Group(final String name, final Chain<?> toolchain) {
		super(name);
		CrashHandler.init(toolchain);
		this.toolchain = toolchain;
		this.settings = toolchain.getSettings();
	}
	
	/**
	 * Adds a new Node to the managed thread group.
	 * 
	 * @param thread
	 *            the Node that shall be managed.
	 * @return the integer
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
	 * Gets the repo suite settings.
	 * 
	 * @return the repo suite settings
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
	 * Gets the toolchain.
	 * 
	 * @return the toolchain
	 */
	public final Chain<?> getToolchain() {
		return this.toolchain;
	}
	
	/**
	 * Gets the tool information.
	 * 
	 * @return the tool information
	 */
	protected String getToolInformation() {
		return this.settings.getInformation();
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
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t,
	                              final Throwable e) {
		super.uncaughtException(t, e);
		
		if (Logger.logError()) {
			Logger.error(e, "Thread '%s' terminated with uncaught exception '%s'.", t.getName(), e.getClass().getName());
			Logger.error("Shutting down.");
		}
		shutdown();
	}
	
}
