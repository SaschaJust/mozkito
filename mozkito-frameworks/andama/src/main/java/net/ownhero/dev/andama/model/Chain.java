/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.settings.Settings;

/**
 * The {@link Chain} is a wrapper to a tool chain consisting of {@link AndamaThread}s. It is used to extend
 * {@link Thread}s for parallel tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Chain extends Thread {
	
	private Pool           pool;
	private final Settings settings;
	
	/**
	 * @param settings
	 */
	public Chain(final Settings settings) {
		this.settings = settings;
		setName(this.getClass().getSimpleName());
		CrashHandler.init(this);
	}
	
	public Chain(final Settings settings, final String chainName) {
		this.settings = settings;
		setName(chainName);
		CrashHandler.init(this);
	}
	
	public Pool getPool() {
		return this.pool;
	}
	
	/**
	 * @return
	 */
	public Settings getSettings() {
		return this.settings;
	}
	
	void setPool(final Pool pool) {
		this.pool = pool;
	}
	
	/**
	 * Setup the environment. Configure the settings/arguments and instantiate base entities.
	 */
	public abstract void setup();
	
	/**
	 * Calls shutdown on all components and shuts down all related threads.
	 */
	public void shutdown() {
		getPool().shutdown();
	}
}
