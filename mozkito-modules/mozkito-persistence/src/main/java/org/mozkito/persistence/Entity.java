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

package org.mozkito.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mozkito.utilities.io.FileUtils;

/**
 * The Interface Entity.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface Entity extends Annotated {
	
	/**
	 * The Class A.
	 */
	public static class Static {
		
		/**
		 * Gets the.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param entity
		 *            the entity
		 * @param key
		 *            the key
		 * @param index
		 *            the index
		 * @return the t
		 */
		@SuppressWarnings ("unchecked")
		public static <T> T get(final Entity entity,
		                        final IteratableFieldKey key,
		                        final int index) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (key == null) {
					throw new NullPointerException();
				}
				
				final Set<IteratableFieldKey> supportedFields = entity.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				if (!supportedFields.contains(key)) {
					throw new IllegalArgumentException(key.name());
				}
			}
			
			final Collection<Object> collection = entity.get(key);
			if (collection.isEmpty() || (index < collection.size())) {
				throw new ArrayIndexOutOfBoundsException();
			}
			
			final Iterator<Object> it = collection.iterator();
			for (int i = 0; i < index; ++i) {
				it.next();
			}
			
			return (T) it.next();
		}
		
		/**
		 * Gets the all.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the all
		 */
		public static Map<FieldKey, Object> getAll(final Entity entity,
		                                           final FieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = entity.supportedFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final FieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			final Map<FieldKey, Object> ret = new HashMap<FieldKey, Object>();
			
			for (final FieldKey key : keys) {
				ret.put(key, entity.get(key));
			}
			
			return ret;
		}
		
		/**
		 * Gets the all.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the all
		 */
		public static Map<IteratableFieldKey, Object> getAll(final Entity entity,
		                                                     final IteratableFieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IteratableFieldKey> supportedFields = entity.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IteratableFieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			final Map<IteratableFieldKey, Object> ret = new HashMap<IteratableFieldKey, Object>();
			
			for (final IteratableFieldKey key : keys) {
				ret.put(key, entity.get(key));
			}
			
			return ret;
		}
		
		/**
		 * Gets the any.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the any
		 */
		public static Object getAny(final Entity entity,
		                            final FieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = entity.supportedFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final FieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			Object ret = null;
			
			for (final FieldKey key : keys) {
				ret = entity.get(key);
				if (ret != null) {
					return ret;
				}
			}
			
			return null;
		}
		
		/**
		 * Gets the any.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the any
		 */
		public static Object getAny(final Entity entity,
		                            final IteratableFieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IteratableFieldKey> supportedFields = entity.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IteratableFieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			Object ret = null;
			
			for (final IteratableFieldKey key : keys) {
				ret = entity.get(key);
				if (ret != null) {
					return ret;
				}
			}
			
			return null;
		}
		
		/**
		 * Gets the as one string.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the as one string
		 */
		public static String getAsOneString(final Entity entity,
		                                    final FieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = entity.supportedFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final FieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			final StringBuilder builder = new StringBuilder();
			Object o = null;
			
			for (final FieldKey key : keys) {
				if ((o = entity.get(key)) != null) {
					builder.append(o.toString());
					builder.append(FileUtils.lineSeparator);
				}
			}
			
			return builder.toString();
		}
		
		/**
		 * Gets the as one string.
		 * 
		 * @param entity
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the as one string
		 */
		public static String getAsOneString(final Entity entity,
		                                    final IteratableFieldKey... keys) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IteratableFieldKey> supportedFields = entity.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IteratableFieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			final StringBuilder builder = new StringBuilder();
			Collection<Object> objectCollection = null;
			
			for (final IteratableFieldKey key : keys) {
				if ((objectCollection = entity.get(key)) != null) {
					for (final Object o : objectCollection) {
						builder.append(o.toString());
						builder.append(FileUtils.lineSeparator);
					}
				}
			}
			
			return builder.toString();
		}
		
		/**
		 * Gets the size.
		 * 
		 * @param entity
		 *            the entity
		 * @param key
		 *            the key
		 * @return the size
		 */
		public static int getSize(final Entity entity,
		                          final IteratableFieldKey key) {
			PRECONDITIONS: {
				if (entity == null) {
					throw new NullPointerException();
				}
				
				if (key == null) {
					throw new NullPointerException();
				}
				
				final Set<IteratableFieldKey> supportedFields = entity.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				if (!supportedFields.contains(key)) {
					throw new IllegalArgumentException(key.name());
				}
			}
			
			final Collection<Object> collection = entity.get(key);
			SANITY: {
				assert collection != null;
			}
			
			return collection.size();
		}
	}
	
	// interface to be used when dynamically loading annotated/persistent classes.
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @return the t
	 */
	<T> T get(FieldKey key);
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @return the collection
	 */
	<T> Collection<T> get(IteratableFieldKey key);
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param index
	 *            the index
	 * @return the t
	 */
	<T> T get(IteratableFieldKey key,
	          int index);
	
	/**
	 * Gets the all.
	 * 
	 * @param keys
	 *            the keys
	 * @return the all
	 */
	Map<FieldKey, Object> getAll(final FieldKey... keys);
	
	/**
	 * Gets the all.
	 * 
	 * @param keys
	 *            the keys
	 * @return the all
	 */
	Map<IteratableFieldKey, Object> getAll(final IteratableFieldKey... keys);
	
	/**
	 * Gets the any.
	 * 
	 * @param keys
	 *            the keys
	 * @return the any
	 */
	Object getAny(final FieldKey... keys);
	
	/**
	 * Gets the any.
	 * 
	 * @param keys
	 *            the keys
	 * @return the any
	 */
	Object getAny(final IteratableFieldKey... keys);
	
	/**
	 * Gets the as one string.
	 * 
	 * @param fKeys
	 *            the f keys
	 * @return the as one string
	 */
	String getAsOneString(final FieldKey... fKeys);
	
	/**
	 * Gets the as one string.
	 * 
	 * @param iKeys
	 *            the i keys
	 * @return the as one string
	 */
	String getAsOneString(final IteratableFieldKey iKeys);
	
	/**
	 * Gets the iD string.
	 * 
	 * @return the iD string
	 */
	String getIDString();
	
	/**
	 * Gets the size.
	 * 
	 * @param key
	 *            the key
	 * @return the size
	 */
	int getSize(final IteratableFieldKey key);
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	String getText();
	
	/**
	 * Supported fields.
	 * 
	 * @return the sets the
	 */
	Set<FieldKey> supportedFields();
	
	/**
	 * Supported iteratable fields.
	 * 
	 * @return the sets the
	 */
	Set<IteratableFieldKey> supportedIteratableFields();
}
