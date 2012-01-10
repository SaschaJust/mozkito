package de.unisaarland.cs.st.moskito.mapping.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.settings.registerable.Registered;
import de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage;

public abstract class StorageAccessor extends Registered {
	
	private final Map<Class<? extends MappingStorage>, MappingStorage> storages = new HashMap<Class<? extends MappingStorage>, MappingStorage>();
	
	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public final <T extends MappingStorage> T getStorage(final Class<T> key) {
		return (T) this.storages.get(key);
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
	public final void provideStorages(final Set<? extends MappingStorage> storages) {
		for (final MappingStorage storage : storages) {
			this.storages.put(storage.getClass(), storage);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.register.Registered#storageDependency
	 * ()
	 */
	public Set<Class<? extends MappingStorage>> storageDependency() {
		return new HashSet<Class<? extends MappingStorage>>();
	}
	
}
