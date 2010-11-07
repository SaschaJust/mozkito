/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

/**
 * The {@link RepoSuiteToolchain} is a wrapper to a tool chain consisting of
 * {@link RepoSuiteThread}s. It is used to extend {@link Thread}s for parallel
 * tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface RepoSuiteToolchain {
	
	/**
	 * Setup the environment. Configure the settings/arguments and instantiate
	 * base entities.
	 */
	public void setup();
	
	/**
	 * Calls shutdown on all components and shuts down all related threads.
	 */
	public void shutdown();
}
