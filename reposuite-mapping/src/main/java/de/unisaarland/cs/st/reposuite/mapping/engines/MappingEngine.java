/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingEngine {
	
	private MappingSettings                                            settings;
	private boolean                                                    registered  = false;
	private boolean                                                    initialized = false;
	private final Map<Class<? extends MappingStorage>, MappingStorage> storages    = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	public MappingEngine() {
		
	}
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * @return
	 */
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return the settings
	 */
	public MappingSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public <T extends MappingStorage> T getStorage(final Class<T> key) {
		return (T) this.storages.get(key);
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The engine has to be registered before it is initialized. Engine: %s",
		                this.getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param storage
	 */
	public void provideStorage(final MappingStorage storage) {
		this.storages.put(storage.getClass(), storage);
	}
	
	/**
	 * @param storages
	 */
	public void provideStorages(final Set<? extends MappingStorage> storages) {
		for (MappingStorage storage : storages) {
			this.storages.put(storage.getClass(), storage);
		}
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		setSettings(settings);
		setRegistered(true);
	}
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public abstract void score(final RCSTransaction transaction,
	                           final Report report,
	                           final MapScore score);
	
	/**
	 * @param initialized the initialized to set
	 */
	void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered the registered to set
	 */
	void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings the settings to set
	 */
	public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
	/**
	 * @return
	 */
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingEngine [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected String truncate(final String string) {
		return string.substring(0, Math.min(string.length() - 1, 254));
	}
	
}
