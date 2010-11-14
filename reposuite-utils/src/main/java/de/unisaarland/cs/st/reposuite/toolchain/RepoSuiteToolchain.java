/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.CrashHandler;

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
