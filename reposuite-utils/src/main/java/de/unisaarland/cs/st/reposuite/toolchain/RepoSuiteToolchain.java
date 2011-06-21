/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * The {@link RepoSuiteToolchain} is a wrapper to a tool chain consisting of
 * {@link RepoSuiteThread}s. It is used to extend {@link Thread}s for parallel
 * tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteToolchain extends Thread {
	
	private final RepoSuiteSettings settings;
	
	public RepoSuiteToolchain(final RepoSuiteSettings settings) {
		this.settings = settings;
		CrashHandler.init(this);
	}
	
	public RepoSuiteSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * Setup the environment. Configure the settings/arguments and instantiate
	 * base entities.
	 */
	public abstract void setup();
	
	/**
	 * Calls shutdown on all components and shuts down all related threads.
	 */
	public abstract void shutdown();
}
