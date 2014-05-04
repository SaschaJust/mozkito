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

package org.mozkito.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class DBEntityCache.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class EntityCache {
	
	/**
	 * The Class DoubleValueMap.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V1>
	 *            the generic type
	 * @param <V2>
	 *            the generic type
	 */
	public static final class DoubleValueMap<K, V1, V2> implements Map<K, Tuple<V1, V2>> {
		
		/**
		 * The Class Entry.
		 * 
		 * @param <K>
		 *            the key type
		 * @param <V1>
		 *            the generic type
		 * @param <V2>
		 *            the generic type
		 */
		public static final class Entry<K, V1, V2> {
			
			/** The key. */
			private final K key;
			
			/** The value1. */
			private V1      value1;
			
			/** The value2. */
			private V2      value2;
			
			/**
			 * @param key
			 * @param value1
			 * @param value2
			 */
			public Entry(final K key, final V1 value1, final V2 value2) {
				this.key = key;
				this.value1 = value1;
				this.value2 = value2;
			}
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see java.lang.Object#equals(java.lang.Object)
			 */
			@Override
			public boolean equals(final Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				final Entry<?, ?, ?> other = (Entry<?, ?, ?>) obj;
				if (this.key == null) {
					if (other.key != null) {
						return false;
					}
				} else if (!this.key.equals(other.key)) {
					return false;
				}
				return true;
			}
			
			/**
			 * Gets the key.
			 * 
			 * @return the key
			 */
			public K getKey() {
				return this.key;
			}
			
			/**
			 * Gets the value1.
			 * 
			 * @return the value1
			 */
			public V1 getValue1() {
				return this.value1;
			}
			
			/**
			 * Gets the value2.
			 * 
			 * @return the value2
			 */
			public V2 getValue2() {
				return this.value2;
			}
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see java.lang.Object#hashCode()
			 */
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = (prime * result) + ((this.key == null)
				                                               ? 0
				                                               : this.key.hashCode());
				return result;
			}
			
			/**
			 * Sets the value1.
			 * 
			 * @param value1
			 *            the new value1
			 */
			public void setValue1(final V1 value1) {
				this.value1 = value1;
			}
			
			/**
			 * Sets the value2.
			 * 
			 * @param value2
			 *            the new value2
			 */
			public void setValue2(final V2 value2) {
				this.value2 = value2;
			}
			
		}
		
		/** The map. */
		private final Map<K, Tuple<V1, V2>> map = new HashMap<>();
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#clear()
		 */
		@Override
		public void clear() {
			this.map.clear();
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		@Override
		public boolean containsKey(final Object key) {
			return this.map.containsKey(key);
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		@Override
		public boolean containsValue(final Object value) {
			return CollectionUtils.exists(this.map.values(), new Predicate() {
				
				@SuppressWarnings ("unchecked")
				@Override
				public boolean evaluate(final Object object) {
					PRECONDITIONS: {
						if (object == null) {
							throw new NullPointerException();
						}
					}
					
					final Tuple<V1, V2> tuple = ((Tuple<V1, V2>) object);
					
					SANITY: {
						assert tuple.getFirst() != null;
						assert tuple.getSecond() != null;
					}
					
					return tuple.getFirst().equals(value) || tuple.getSecond().equals(value);
				}
			});
		}
		
		/**
		 * Contains value1.
		 * 
		 * @param value1
		 *            the value
		 * @return true, if successful
		 */
		public boolean containsValue1(final Object value1) {
			return CollectionUtils.exists(this.map.values(), new Predicate() {
				
				@SuppressWarnings ("unchecked")
				@Override
				public boolean evaluate(final Object object) {
					PRECONDITIONS: {
						if (object == null) {
							throw new NullPointerException();
						}
					}
					
					return ((Tuple<V1, V2>) object).getFirst().equals(value1);
				}
			});
			
		}
		
		/**
		 * Contains value2.
		 * 
		 * @param value2
		 *            the value2
		 * @return true, if successful
		 */
		public boolean containsValue2(final Object value2) {
			return CollectionUtils.exists(this.map.values(), new Predicate() {
				
				@SuppressWarnings ("unchecked")
				@Override
				public boolean evaluate(final Object object) {
					PRECONDITIONS: {
						if (object == null) {
							throw new NullPointerException();
						}
					}
					
					return ((Tuple<V1, V2>) object).getSecond().equals(value2);
				}
			});
			
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#entrySet()
		 */
		@Override
		public Set<java.util.Map.Entry<K, Tuple<V1, V2>>> entrySet() {
			return this.map.entrySet();
		}
		
		/**
		 * Returns a set containing the triples in the map. Be aware that the entry set is generated every time this
		 * function is called.
		 * 
		 * @return the sets the
		 */
		public Set<Entry<K, V1, V2>> entryTripleSet() {
			final Set<Entry<K, V1, V2>> set = new HashSet<>();
			for (final Map.Entry<K, Tuple<V1, V2>> entry : this.map.entrySet()) {
				set.add(new Entry<K, V1, V2>(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond()));
			}
			return set;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#get(java.lang.Object)
		 */
		@Override
		public Tuple<V1, V2> get(final Object key) {
			return this.map.get(key);
		}
		
		/**
		 * Finds all keys the value pair is mapped by.
		 * 
		 * @param value1
		 *            the value1
		 * @param value2
		 *            the value2
		 * @return the keys
		 */
		public Set<K> getKeys(final V1 value1,
		                      final V2 value2) {
			final Set<K> set = new HashSet<>();
			
			for (final Map.Entry<K, Tuple<V1, V2>> entry : this.map.entrySet()) {
				SANITY: {
					assert entry.getValue() != null;
					assert entry.getValue().getFirst() != null;
					assert entry.getValue().getSecond() != null;
					if (entry.getValue().getFirst().equals(value1) && entry.getValue().getSecond().equals(value2)) {
						set.add(entry.getKey());
					}
				}
			}
			
			return set;
		}
		
		/**
		 * Gets the value1.
		 * 
		 * @param key
		 *            the key
		 * @return the value1
		 */
		public V1 getValue1(final K key) {
			return this.map.containsKey(key)
			                                ? this.map.get(key).getFirst()
			                                : null;
		}
		
		/**
		 * Gets the value2.
		 * 
		 * @param key
		 *            the key
		 * @return the value2
		 */
		public V2 getValue2(final K key) {
			return this.map.containsKey(key)
			                                ? this.map.get(key).getSecond()
			                                : null;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			return this.map.isEmpty();
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#keySet()
		 */
		@Override
		public Set<K> keySet() {
			return this.map.keySet();
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Tuple<V1, V2> put(final K key,
		                         final Tuple<V1, V2> value) {
			return this.map.put(key, value);
		}
		
		/**
		 * Put.
		 * 
		 * @param key
		 *            the key
		 * @param value1
		 *            the value1
		 * @param value2
		 *            the value2
		 * @return the tuple
		 */
		public Tuple<V1, V2> put(final K key,
		                         final V1 value1,
		                         final V2 value2) {
			return this.map.put(key, new Tuple<V1, V2>(value1, value2));
		}
		
		/**
		 * Put all.
		 * 
		 * @param map
		 *            the map
		 */
		public void putAll(final DoubleValueMap<K, V1, V2> map) {
			for (final Map.Entry<K, Tuple<V1, V2>> entry : map.entrySet()) {
				this.map.put(entry.getKey(), entry.getValue());
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		@Override
		public void putAll(final Map<? extends K, ? extends Tuple<V1, V2>> m) {
			this.map.putAll(m);
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		@Override
		public Tuple<V1, V2> remove(final Object key) {
			return this.map.remove(key);
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#size()
		 */
		@Override
		public int size() {
			return this.map.size();
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Map#values()
		 */
		@Override
		public Collection<Tuple<V1, V2>> values() {
			return this.map.values();
		}
		
	}
	
	/** The cache. We map the class of the entity to a map from the id to the entity and the number of references. */
	private final Map<Class<? extends Entity>, DoubleValueMap<Object, Entity, Integer>> cache = new HashMap<>();
	
	/**
	 * Contains.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	public boolean contains(final Class<? extends Entity> clazz,
	                        final Object id) {
		if (this.cache.containsKey(clazz)) {
			final DoubleValueMap<Object, Entity, Integer> valueMap = this.cache.get(clazz);
			return valueMap.containsKey(id);
		}
		
		return false;
	}
	
	/**
	 * Fetch.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return the entity
	 */
	public Entity fetch(final Class<? extends Entity> clazz,
	                    final Object id) {
		PRECONDITIONS: {
			if (clazz == null) {
				throw new NullPointerException();
			}
			if (id == null) {
				throw new NullPointerException();
			}
			
		}
		
		if (!this.cache.containsKey(clazz)) {
			throw new IllegalArgumentException("There is no object of class " + clazz.getSimpleName()
			        + " in the cache.");
		}
		
		final DoubleValueMap<Object, Entity, Integer> theMap = this.cache.get(clazz);
		
		if (!theMap.containsKey(id)) {
			return null;
		}
		
		final Tuple<Entity, Integer> tuple = theMap.get(id);
		tuple.setSecond(tuple.getSecond() + 1);
		return tuple.getFirst();
		
	}
	
	/**
	 * Register an entity with the cache. If the entity is already in the cache, we increase the reference counter.
	 * Otherwise, we add the entity to the cache. If the referenced database entity is already in the cache, but
	 * referenced by a different object, this causes an IllegalArgumentException.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void register(final Entity entity) {
		PRECONDITIONS: {
			if (entity == null) {
				throw new NullPointerException();
			}
		}
		
		DoubleValueMap<Object, Entity, Integer> theMap = null;
		
		SANITY: {
			assert this.cache != null;
		}
		
		// check if we already have a cache for this class
		if (!this.cache.containsKey(entity.getClass())) {
			theMap = this.cache.put(entity.getClass(), new DoubleValueMap<Object, Entity, Integer>());
		} else {
			theMap = this.cache.get(entity.getClass());
		}
		
		// lookup entity
		Tuple<Entity, Integer> tuple = null;
		if (!theMap.containsKey(entity.getId())) {
			tuple = theMap.put(entity.getId(), entity, 0);
		} else {
			// at this point, there was already a mapping in the cache
			tuple = theMap.get(entity.getId());
		}
		
		// yes, this is comparison by reference and intended. we want to avoid having multiple objects referring to the
		// same entity in the database
		if (tuple.getFirst() != entity) {
			throw new IllegalArgumentException("Cannot have multiple objects referencing the same database entity.");
		}
		
		// increase reference counter
		tuple.setSecond(tuple.getSecond() + 1);
	}
	
	/**
	 * Unregister.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void unregister(final Entity entity) {
		PRECONDITIONS: {
			if (entity == null) {
				throw new NullPointerException();
			}
			assert this.cache != null;
			if (!this.cache.containsKey(entity.getClass())) {
				throw new IllegalArgumentException();
			}
		}
		final Tuple<Entity, Integer> tuple = this.cache.get(entity.getClass()).get(entity.getId());
		
		// yes, this is comparison by reference and intended. we want to avoid having multiple objects referring to the
		// same entity in the database
		if (entity != tuple.getFirst()) {
			throw new IllegalArgumentException("Cannot have multiple objects referencing the same database entity.");
		}
		
		if (tuple.getSecond() == 1) {
			this.cache.get(entity.getClass()).remove(entity.getId());
			if (this.cache.get(entity.getClass()).isEmpty()) {
				this.cache.remove(entity.getClass());
			}
		} else {
			tuple.setSecond(tuple.getSecond() - 1);
		}
	}
	
}
