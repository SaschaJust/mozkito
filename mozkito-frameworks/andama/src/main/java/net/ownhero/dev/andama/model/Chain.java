/**
 * 
 */
package net.ownhero.dev.andama.model;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.messages.EventBus;
import net.ownhero.dev.andama.messages.IEventListener;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The {@link Chain} is a wrapper to a tool chain consisting of {@link AndamaThread}s. It is used to extend
 * 
 * @param <T>
 *            the generic type {@link Thread}s for parallel tasks.
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public abstract class Chain<T extends Settings> extends Thread {
	
	/** The pool. */
	private Pool           pool;
	
	/** The settings. */
	private final T        settings;
	
	/** The shutdown. */
	private boolean        shutdown;
	
	/** The event bus. */
	private final EventBus eventBus = new EventBus();
	
	/**
	 * Instantiates a new chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public Chain(final T settings) {
		this.settings = settings;
		setName(this.getClass().getSimpleName());
		CrashHandler.init(this);
	}
	
	/**
	 * Instantiates a new chain.
	 * 
	 * @param settings
	 *            the settings
	 * @param chainName
	 *            the chain name
	 */
	public Chain(final T settings, final String chainName) {
		this.settings = settings;
		setName(chainName);
		CrashHandler.init(this);
	}
	
	/**
	 * @return the eventBus
	 */
	public final EventBus getEventBus() {
		// PRECONDITIONS
		
		try {
			return this.eventBus;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.eventBus, "Field '%s' in '%s'.", "eventBus", getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the pool.
	 * 
	 * @return the pool
	 */
	public final Pool getPool() {
		return this.pool;
	}
	
	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	public final T getSettings() {
		return this.settings;
	}
	
	/**
	 * Register event listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void registerEventListener(final IEventListener listener) {
		this.eventBus.addListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		if (!this.shutdown) {
			if (getSettings().helpRequested()) {
				System.err.println(getSettings().getHelpString());
				throw new Shutdown("help requested"); //$NON-NLS-1$
			}
			setup();
			if (!this.shutdown) {
				getPool().execute();
			}
		}
	}
	
	/**
	 * Sets the pool.
	 * 
	 * @param pool
	 *            the new pool
	 */
	final void setPool(final Pool pool) {
		this.pool = pool;
	}
	
	/**
	 * Setup the environment. Configure the settings/arguments and instantiate base entities.
	 */
	public abstract void setup();
	
	/**
	 * Shutdown.
	 */
	public final void shutdown() {
		
		if (Logger.logInfo()) {
			Logger.info("Toolchain shutdown.");
		}
		
		getPool().shutdown();
		this.shutdown = true;
	}
	
}
