/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;

/**
 * The Class ReMapSet.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReMapSet<K, V> {
	
	/** The from map. */
	private final Map<K, Set<V>> fromMap = new HashMap<>();
	
	/** The to map. */
	private final Map<V, Set<K>> toMap   = new HashMap<>();
	
	/** The k class. */
	@SuppressWarnings ("rawtypes")
	private Class<? extends Set> kClass;
	
	/** The v class. */
	@SuppressWarnings ("rawtypes")
	private Class<? extends Set> vClass;
	
	/**
     * 
     */
	public ReMapSet() {
		this(HashSet.class);
	}
	
	/**
	 * Instantiates a new re map set.
	 * 
	 * @param <X>
	 *            the generic type
	 * @param setClass
	 *            the set class
	 */
	@SuppressWarnings ("rawtypes")
	public <X extends Set> ReMapSet(final Class<X> setClass) {
		this(setClass, setClass);
	}
	
	/**
	 * Instantiates a new re map set.
	 * 
	 * @param <X>
	 *            the generic type
	 * @param <Y>
	 *            the generic type
	 * @param kClass
	 *            the k class
	 * @param vClass
	 *            the v class
	 */
	@SuppressWarnings ("rawtypes")
	public <X extends Set, Y extends Set> ReMapSet(final Class<X> kClass, final Class<Y> vClass) {
		// PRECONDITIONS
		
		try {
			this.kClass = kClass;
			this.vClass = vClass;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	/**
	 * Clear.
	 */
	public void clear() {
		// PRECONDITIONS
		
		try {
			this.fromMap.clear();
			this.toMap.clear();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Contains key.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsFrom(final K key) {
		// PRECONDITIONS
		
		try {
			return this.fromMap.containsKey(key);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Contains value.
	 * 
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	public boolean containsTo(final V value) {
		// PRECONDITIONS
		
		try {
			return this.toMap.containsKey(value);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * From entry set.
	 * 
	 * @return the sets the
	 */
	public Set<java.util.Map.Entry<K, Set<V>>> fromEntrySet() {
		// PRECONDITIONS
		
		try {
			return this.fromMap.entrySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * From key set.
	 * 
	 * @return the sets the
	 */
	public Set<K> fromKeySet() {
		// PRECONDITIONS
		
		try {
			return this.fromMap.keySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * From size.
	 * 
	 * @return the int
	 */
	public int fromSize() {
		// PRECONDITIONS
		
		try {
			return this.fromMap.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * From values.
	 * 
	 * @return the collection
	 */
	public Collection<Set<V>> fromValues() {
		// PRECONDITIONS
		
		try {
			return this.fromMap.values();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the froms.
	 * 
	 * @param value
	 *            the value
	 * @return the froms
	 */
	public Set<K> getFroms(final V value) {
		try {
			return this.toMap.get(value);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(ReMapSet.class);
	}
	
	/**
	 * Gets the tos.
	 * 
	 * @param key
	 *            the key
	 * @return the tos
	 */
	public Set<V> getTos(final K key) {
		// PRECONDITIONS
		
		try {
			return this.fromMap.get(key);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		// PRECONDITIONS
		
		try {
			return this.fromMap.isEmpty();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Put.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	@SuppressWarnings ("unchecked")
	public boolean put(final K key,
	                   final V value) {
		// PRECONDITIONS
		
		try {
			try {
				if (!this.fromMap.containsKey(key)) {
					this.fromMap.put(key, this.vClass.newInstance());
					
				}
				
				final boolean add = this.fromMap.get(key).add(value);
				if (!add) {
					return false;
				}
				
				if (!this.toMap.containsKey(value)) {
					this.toMap.put(value, this.kClass.newInstance());
				}
				
				return this.toMap.get(value).add(key);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Put all.
	 * 
	 * @param m
	 *            the m
	 */
	@SuppressWarnings ("unchecked")
	public void putAll(final ReMapSet<K, V> m) {
		// PRECONDITIONS
		
		try {
			
			try {
				for (final Entry<K, Set<V>> entry : m.fromEntrySet()) {
					if (this.fromMap.containsKey(entry.getKey())) {
						this.fromMap.put(entry.getKey(), this.kClass.newInstance());
					}
					
					this.fromMap.get(entry.getKey()).addAll(entry.getValue());
				}
				
				for (final Entry<V, Set<K>> entry : m.toEntrySet()) {
					if (this.toMap.containsKey(entry.getKey())) {
						this.toMap.put(entry.getKey(), this.vClass.newInstance());
					}
					
					this.toMap.get(entry.getKey()).addAll(entry.getValue());
				}
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	/**
	 * Removes the.
	 * 
	 * @param key
	 *            the key
	 * @return the v
	 */
	public Set<V> removeFrom(final K key) {
		// PRECONDITIONS
		
		try {
			
			final Set<V> set = this.fromMap.get(key);
			for (final V value : set) {
				final Set<K> set2 = this.toMap.get(value);
				if (set2.size() == 1) {
					this.toMap.remove(value);
				} else {
					set2.remove(key);
				}
			}
			
			this.fromMap.remove(key);
			
			return set;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Removes the to.
	 * 
	 * @param value
	 *            the value
	 * @return the sets the
	 */
	public Set<K> removeTo(final V value) {
		// PRECONDITIONS
		
		try {
			
			final Set<K> set = this.toMap.get(value);
			for (final K key : set) {
				final Set<V> set2 = this.fromMap.get(key);
				if (set2.size() == 1) {
					this.fromMap.remove(key);
				} else {
					set2.remove(value);
				}
			}
			
			this.toMap.remove(value);
			
			return set;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * To entry set.
	 * 
	 * @return the sets the
	 */
	public Set<java.util.Map.Entry<V, Set<K>>> toEntrySet() {
		// PRECONDITIONS
		
		try {
			return this.toMap.entrySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * To key set.
	 * 
	 * @return the sets the
	 */
	public Set<V> toKeySet() {
		// PRECONDITIONS
		
		try {
			return this.toMap.keySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * To size.
	 * 
	 * @return the int
	 */
	public int toSize() {
		// PRECONDITIONS
		
		try {
			return this.toMap.size();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * To values.
	 * 
	 * @return the collection
	 */
	public Collection<Set<K>> toValues() {
		// PRECONDITIONS
		
		try {
			return this.toMap.values();
		} finally {
			// POSTCONDITIONS
		}
	}
}
