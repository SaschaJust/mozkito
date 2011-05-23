package de.unisaarland.cs.st.reposuite.mapping.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;

public abstract class Registered {
	
	/**
	 * @param clazz
	 * @return
	 */
	private static final String findRegisteredSuper(final Class<? extends Registered> clazz) {
		String[] superTag = new String[] { "" };
		return findRegisteredSuper(clazz, superTag);
	}
	
	/**
	 * @param clazz
	 * @param superTag
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private static String findRegisteredSuper(final Class<? extends Registered> clazz,
	                                          final String[] superTag) {
		if (clazz.getSuperclass() == Registered.class) {
			System.err.println("Settings superTag from " + superTag[0] + " to "
			        + clazz.getSimpleName().replace("Mapping", ""));
			superTag[0] = clazz.getSimpleName().replace("Mapping", "");
			return superTag[0].toLowerCase();
		} else if (clazz == Registered.class) {
			throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
			        + " tries to register config option.");
		} else {
			Class<? extends Registered> c = clazz;
			if (Registered.class.isAssignableFrom(c.getSuperclass()) && (c.getSuperclass() != Registered.class)) {
				c = (Class<? extends Registered>) c.getSuperclass();
				System.err.println("processing class: " + clazz);
				String string = findRegisteredSuper(c, superTag);
				System.err.println("receiving registeredSuper string: " + string);
				System.err.println("receiving superTag: " + superTag[0]);
				String retval = string + "." + clazz.getSimpleName().replace(superTag[0], "").toLowerCase();
				System.err.println("Settings superTag from " + superTag[0] + " to " + clazz.getSimpleName());
				superTag[0] = clazz.getSimpleName();
				return retval;
			}
		}
		throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
		        + " tries to register config option.");
	}
	
	private MappingSettings                                            settings;
	
	private boolean                                                    registered  = false;
	private boolean                                                    initialized = false;
	private final Map<Class<? extends MappingStorage>, MappingStorage> storages    = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	/**
	 * 
	 */
	public Registered() {
		super();
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
	 * @param settings
	 * @param arguments
	 * @param option
	 * @return 
	 */
	public final String getOptionName(final String option) {
		StringBuilder builder = new StringBuilder();
		builder.append("mapping.");
		builder.append(findRegisteredSuper(this.getClass()).toLowerCase()).append('.');
		builder.append(option);
		return builder.toString();
	}
	
	/**
	 * @return the settings
	 */
	public final MappingSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public final <T extends MappingStorage> T getStorage(final Class<T> key) {
		return (T) this.storages.get(key);
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The " + this.getClass().getSuperclass().getSimpleName()
		        + " has to be registered before it is initialized: %s", this.getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return the initialized
	 */
	public final boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public final boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param storage
	 */
	public final void provideStorage(final MappingStorage storage) {
		this.storages.put(storage.getClass(), storage);
	}
	
	/**
	 * @param storages
	 */
	public final void provideStorages(final Set<? extends MappingStorage> storages) {
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
	 * @param initialized the initialized to set
	 */
	final void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered the registered to set
	 */
	final void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings the settings to set
	 */
	final public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
	/**
	 * @return
	 */
	public boolean singleton() {
		return false;
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
		builder.append(this.getClass().getSuperclass().getSimpleName() + " [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append(", dependencies=");
		builder.append(JavaUtils.collectionToString(storageDependency()));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected final String truncate(final String string) {
		return string.substring(0, Math.min(string.length() - 1, 254));
	}
	
}
