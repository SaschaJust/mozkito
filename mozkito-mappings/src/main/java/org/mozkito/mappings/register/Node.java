/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.mappings.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozkito.mappings.storages.Storage;

/**
 * The Class Node.
 */
public abstract class Node {
	
	/** The storages. */
	private final Map<Class<? extends Storage>, Storage> storages = new HashMap<Class<? extends Storage>, Storage>();
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getHandle() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Gets the storage.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @return the storage
	 */
	@SuppressWarnings ("unchecked")
	public final <T extends Storage> T getStorage(final Class<T> key) {
		return (T) this.storages.get(key);
	}
	
	/**
	 * Provide storage.
	 * 
	 * @param storage
	 *            the storage
	 */
	public void provideStorage(final Storage storage) {
		this.storages.put(storage.getClass(), storage);
	}
	
	/**
	 * Provide storages.
	 * 
	 * @param storages
	 *            the storages
	 */
	public final void provideStorages(final Set<? extends Storage> mappingStorages) {
		for (final Storage storage : mappingStorages) {
			this.storages.put(storage.getClass(), storage);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.register.Registered#storageDependency ()
	 */
	/**
	 * Storage dependency.
	 * 
	 * @return the set< class<? extends mapping storage>>
	 */
	public Set<Class<? extends Storage>> storageDependency() {
		return new HashSet<Class<? extends Storage>>();
	}
	
}
