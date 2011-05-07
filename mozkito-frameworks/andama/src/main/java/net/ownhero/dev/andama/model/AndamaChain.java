/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.settings.AndamaSettings;

/**
 * The {@link AndamaChain} is a wrapper to a tool chain consisting of
 * {@link AndamaThread}s. It is used to extend {@link Thread}s for parallel
 * tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class AndamaChain extends Thread {
	
	private final AndamaSettings settings;
	
	/**
	 * @param settings
	 */
	public AndamaChain(final AndamaSettings settings) {
		this.settings = settings;
		AndamaCrashHandler.init(this);
	}
	
	/**
	 * @return
	 */
	public AndamaSettings getSettings() {
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
