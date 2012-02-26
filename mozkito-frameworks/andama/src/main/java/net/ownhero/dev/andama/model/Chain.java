/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kisa.Logger;

/**
 * The {@link Chain} is a wrapper to a tool chain consisting of {@link AndamaThread}s. It is used to extend
 * {@link Thread}s for parallel tasks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Chain<T extends Settings> extends Thread {
	
	private Pool    pool;
	private final T settings;
	private boolean shutdown;
	
	/**
	 * @param settings
	 */
	public Chain(final T settings) {
		this.settings = settings;
		setName(this.getClass().getSimpleName());
		CrashHandler.init(this);
	}
	
	/**
	 * @param settings
	 * @param chainName
	 */
	public Chain(final T settings, final String chainName) {
		this.settings = settings;
		setName(chainName);
		CrashHandler.init(this);
	}
	
	/**
	 * @return
	 */
	public final Pool getPool() {
		return this.pool;
	}
	
	/**
	 * @return
	 */
	public final T getSettings() {
		return this.settings;
	}
	
	public final void parseSettings() {
		try {
			getSettings().parse();
		} catch (final SettingsParseError e) {
			throw new Shutdown(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		if (!this.shutdown) {
			parseSettings();
			setup();
			if (!this.shutdown) {
				getPool().execute();
			}
		}
	}
	
	/**
	 * @param pool
	 */
	final void setPool(final Pool pool) {
		this.pool = pool;
	}
	
	/**
	 * Setup the environment. Configure the settings/arguments and instantiate base entities.
	 */
	public abstract void setup();
	
	/**
	 * 
	 */
	public final void shutdown() {
		
		if (Logger.logInfo()) {
			Logger.info("Toolchain shutdown.");
		}
		
		getPool().shutdown();
		this.shutdown = true;
	}
	
}
