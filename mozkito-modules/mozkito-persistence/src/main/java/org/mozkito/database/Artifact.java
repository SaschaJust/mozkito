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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.IterableFieldKey;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Interface Entity.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class Artifact implements Entity {
	
	/**
	 * The Class Static.
	 */
	public static class Static {
		
		/**
		 * Gets the.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param key
		 *            the key
		 * @param index
		 *            the index
		 * @return the t
		 */
		@SuppressWarnings ("unchecked")
		public static <T> T get(final PersistenceUtil util,
		                        final Artifact artifact,
		                        final IterableFieldKey key,
		                        final int index) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (key == null) {
					throw new NullPointerException();
				}
				
				final Set<IterableFieldKey> supportedFields = artifact.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				if (!supportedFields.contains(key)) {
					throw new IllegalArgumentException(key.name());
				}
			}
			
			final Collection<Object> collection = artifact.get(util, key);
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
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the all
		 */
		public static Map<FieldKey, Object> getAll(final PersistenceUtil util,
		                                           final Artifact artifact,
		                                           final FieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = artifact.supportedFields();
				
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
				ret.put(key, artifact.get(util, key));
			}
			
			return ret;
		}
		
		/**
		 * Gets the all.
		 * 
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the all
		 */
		public static Map<IterableFieldKey, Object> getAll(final PersistenceUtil util,
		                                                   final Artifact artifact,
		                                                   final IterableFieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IterableFieldKey> supportedFields = artifact.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IterableFieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			final Map<IterableFieldKey, Object> ret = new HashMap<IterableFieldKey, Object>();
			
			for (final IterableFieldKey key : keys) {
				ret.put(key, artifact.get(util, key));
			}
			
			return ret;
		}
		
		/**
		 * Gets the any.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the any
		 */
		@SuppressWarnings ("unchecked")
		public static <T> T getAny(final PersistenceUtil util,
		                           final Artifact artifact,
		                           final FieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = artifact.supportedFields();
				
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
				ret = artifact.get(util, key);
				if (ret != null) {
					return ((T) ret);
				}
			}
			
			return null;
		}
		
		/**
		 * Gets the any.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the any
		 */
		@SuppressWarnings ("unchecked")
		public static <T> T getAny(final PersistenceUtil util,
		                           final Artifact artifact,
		                           final IterableFieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IterableFieldKey> supportedFields = artifact.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IterableFieldKey key : keys) {
					if (key == null) {
						throw new NullPointerException();
					}
					
					if (!supportedFields.contains(key)) {
						throw new IllegalArgumentException(key.name());
					}
				}
			}
			
			Object ret = null;
			
			for (final IterableFieldKey key : keys) {
				ret = artifact.get(util, key);
				if (ret != null) {
					try {
						final T t = (T) ret;
						return t;
					} catch (final ClassCastException e) {
						throw new RuntimeException(
						                           String.format("The field '%s' of type '%s' cannot be casted to the requested type.",
						                                         key, ret.getClass().getSimpleName()), e);
					}
				}
			}
			
			return null;
		}
		
		/**
		 * Gets the as one string.
		 * 
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the as one string
		 */
		public static String getAsOneString(final PersistenceUtil util,
		                                    final Artifact artifact,
		                                    final FieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<FieldKey> supportedFields = artifact.supportedFields();
				
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
				if ((o = artifact.get(util, key)) != null) {
					builder.append(o.toString());
					builder.append(FileUtils.lineSeparator);
				}
			}
			
			return builder.toString();
		}
		
		/**
		 * Gets the as one string.
		 * 
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param keys
		 *            the keys
		 * @return the as one string
		 */
		public static String getAsOneString(final PersistenceUtil util,
		                                    final Artifact artifact,
		                                    final IterableFieldKey... keys) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (keys == null) {
					throw new NullPointerException();
				}
				
				final Set<IterableFieldKey> supportedFields = artifact.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				for (final IterableFieldKey key : keys) {
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
			
			for (final IterableFieldKey key : keys) {
				if ((objectCollection = artifact.get(util, key)) != null) {
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
		 * @param util
		 *            the util
		 * @param artifact
		 *            the entity
		 * @param key
		 *            the key
		 * @return the size
		 */
		public static int getSize(final PersistenceUtil util,
		                          final Artifact artifact,
		                          final IterableFieldKey key) {
			PRECONDITIONS: {
				if (artifact == null) {
					throw new NullPointerException();
				}
				
				if (key == null) {
					throw new NullPointerException();
				}
				
				final Set<IterableFieldKey> supportedFields = artifact.supportedIteratableFields();
				
				SANITY: {
					assert supportedFields != null;
				}
				
				if (!supportedFields.contains(key)) {
					throw new IllegalArgumentException(key.name());
				}
			}
			
			final Collection<Object> collection = artifact.get(util, key);
			
			SANITY: {
				assert collection != null;
			}
			
			return collection.size();
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5698323771226962732L;
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param util
	 *            the util
	 * @param key
	 *            the key
	 * @return the t
	 */
	public abstract <T> T get(final PersistenceUtil util,
	                          FieldKey key);
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param util
	 *            the util
	 * @param key
	 *            the key
	 * @return the collection
	 */
	public abstract <T> Collection<T> get(final PersistenceUtil util,
	                                      IterableFieldKey key);
	
	/**
	 * Gets the.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param util
	 *            the util
	 * @param key
	 *            the key
	 * @param index
	 *            the index
	 * @return the t
	 */
	public <T> T get(final PersistenceUtil util,
	                 final IterableFieldKey key,
	                 final int index) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.get(util, this, key, index);
	}
	
	/**
	 * Gets the all.
	 * 
	 * @param util
	 *            the util
	 * @param keys
	 *            the keys
	 * @return the all
	 */
	public Map<FieldKey, Object> getAll(final PersistenceUtil util,
	                                    final FieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAll(util, this, keys);
	}
	
	/**
	 * Gets the all.
	 * 
	 * @param util
	 *            the util
	 * @param keys
	 *            the keys
	 * @return the all
	 */
	public Map<IterableFieldKey, Object> getAll(final PersistenceUtil util,
	                                            final IterableFieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAll(util, this, keys);
	}
	
	/**
	 * Gets the any.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param util
	 *            the util
	 * @param keys
	 *            the keys
	 * @return the any
	 */
	public <T> T getAny(final PersistenceUtil util,
	                    final FieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAny(util, this, keys);
	}
	
	/**
	 * Gets the any.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param util
	 *            the util
	 * @param keys
	 *            the keys
	 * @return the any
	 */
	public <T> T getAny(final PersistenceUtil util,
	                    final IterableFieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAny(util, this, keys);
	}
	
	/**
	 * Gets the as one string.
	 * 
	 * @param util
	 *            the util
	 * @param fKeys
	 *            the f keys
	 * @return the as one string
	 */
	public String getAsOneString(final PersistenceUtil util,
	                             final FieldKey... fKeys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAsOneString(util, this, fKeys);
	}
	
	/**
	 * Gets the as one string.
	 * 
	 * @param util
	 *            the util
	 * @param iKeys
	 *            the i keys
	 * @return the as one string
	 */
	public String getAsOneString(final PersistenceUtil util,
	                             final IterableFieldKey iKeys) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getAsOneString(util, this, iKeys);
	}
	
	/**
	 * Gets the iD string.
	 * 
	 * @return the iD string
	 */
	public abstract String getIDString();
	
	/**
	 * Gets the size.
	 * 
	 * @param util
	 *            the util
	 * @param key
	 *            the key
	 * @return the size
	 */
	public int getSize(final PersistenceUtil util,
	                   final IterableFieldKey key) {
		PRECONDITIONS: {
			// none
		}
		
		return Artifact.Static.getSize(util, this, key);
	}
	
	/**
	 * Gets the text.
	 * 
	 * @param util
	 *            the util
	 * @return the text
	 */
	public abstract String getText(final PersistenceUtil util);
	
	/**
	 * Supported fields.
	 * 
	 * @return the sets the
	 */
	public abstract Set<FieldKey> supportedFields();
	
	/**
	 * Supported iteratable fields.
	 * 
	 * @return the sets the
	 */
	public abstract Set<IterableFieldKey> supportedIteratableFields();
}
