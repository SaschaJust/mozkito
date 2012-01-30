/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * The {@link AndamaChain} is a wrapper to a tool chain consisting of {@link AndamaThread}s. It is used to extend
 * {@link Thread}s for parallel tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class AndamaChain extends Thread {
	
	private AndamaPool           pool;
	private final AndamaSettings settings;
	
	/**
	 * @param settings
	 */
	public AndamaChain(final AndamaSettings settings) {
		this.settings = settings;
		setName(this.getClass().getSimpleName());
		AndamaCrashHandler.init(this);
	}
	
	public AndamaChain(final AndamaSettings settings, final String chainName) {
		this.settings = settings;
		setName(chainName);
		AndamaCrashHandler.init(this);
	}
	
	public AndamaPool getPool() {
		return this.pool;
	}
	
	/**
	 * @return
	 */
	public AndamaSettings getSettings() {
		return this.settings;
	}
	
	void setPool(final AndamaPool pool) {
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
