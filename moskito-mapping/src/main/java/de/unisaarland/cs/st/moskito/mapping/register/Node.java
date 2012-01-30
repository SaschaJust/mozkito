/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.unisaarland.cs.st.moskito.mapping.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.settings.registerable.Registered;
import de.unisaarland.cs.st.moskito.mapping.storages.MappingStorage;

public abstract class Node extends Registered {
	
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
