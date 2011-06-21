/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.util.Collection;
import java.util.LinkedList;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

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
	private final RepoSuiteSettings                 settings;
	private final RepoSuiteToolchain                toolchain;
	
	/**
	 * The only valid constructor of {@link RepoSuiteThreadGroup}
	 * 
	 * @param name
	 *            the name of the thread group. In general, this should be the
	 *            simple class name of the calling tool chain.
	 */
	public RepoSuiteThreadGroup(final String name, final RepoSuiteToolchain toolchain) {
		super(name);
		CrashHandler.init(toolchain);
		this.toolchain = toolchain;
		this.settings = toolchain.getSettings();
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
	
	protected String getRepoSuiteSettings() {
		return this.settings.toString();
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
	 * @return the toolchain
	 */
	public final RepoSuiteToolchain getToolchain() {
		return this.toolchain;
	}
	
	protected String getToolInformation() {
		return this.settings.getToolInformation();
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
