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

package org.mozkito.graphs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Parameter;

import net.ownhero.dev.kisa.Logger;

import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class GraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class GraphManager {
	
	/** The Constant map. */
	private static final Map<GraphType, Class<? extends GraphManager>> map = new HashMap<>();
	
	static {
		try {
			final Collection<Class<? extends GraphManager>> managerClasses = ClassFinder.getClassesExtendingClass(GraphManager.class.getPackage(),
			                                                                                                      GraphManager.class,
			                                                                                                      Modifier.ABSTRACT
			                                                                                                              | Modifier.INTERFACE);
			for (final Class<? extends GraphManager> managerClass : managerClasses) {
				try {
					try {
						managerClass.getDeclaredConstructor(new Class<?>[0]);
					} catch (NoSuchMethodException | SecurityException e) {
						if (Logger.logError()) {
							Logger.error("GraphManagers need a default constructor that can be used with reflections. Skipping: %s",
							             managerClass);
						}
						continue;
					}
					
					final GraphManager graphManager = managerClass.newInstance();
					
					SANITY: {
						assert graphManager != null;
						assert graphManager.provides() != null;
					}
					
					// check if GraphType knows about this manager class
					final String managerIdentifier = managerClass.getSimpleName()
					                                             .replace(GraphManager.class.getSimpleName(), "")
					                                             .toUpperCase();
					if (GraphType.valueOf(managerIdentifier) == null) {
						if (Logger.logError()) {
							Logger.error("Found GraphManager (%s) for unknown type: %s.", managerClass.getSimpleName(),
							             managerIdentifier);
						}
					} else {
						map.put(graphManager.provides(), managerClass);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
		} catch (ClassNotFoundException | WrongClassSearchMethodException | IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
	
	/**
	 * Creates the local file db graph manager.
	 * 
	 * @param directory
	 *            the directory
	 * @return the local file db graph manager
	 * @throws NoClassDefFoundError
	 *             the no class def found error
	 * @throws InstantiationException
	 *             the instantiation exception
	 */
	public static LocalFileDBGraphManager createLocalFileDBGraphManager(final File directory) throws NoClassDefFoundError,
	                                                                                         InstantiationException {
		return createLocalFileDBGraphManager(directory, null);
	}
	
	/**
	 * Traverses the classpath uses the first implementation of {@link LocalFileDBGraphManager} that is found to
	 * instantiate a {@link GraphManager}.
	 * 
	 * @param directory
	 *            the directory
	 * @param type
	 *            the type
	 * @return the local file db graph manager
	 * @throws NoClassDefFoundError
	 *             the no class def found error
	 * @throws InstantiationException
	 *             the instantiation exception
	 */
	public static LocalFileDBGraphManager createLocalFileDBGraphManager(final File directory,
	                                                                    final GraphType type) throws NoClassDefFoundError,
	                                                                                         InstantiationException {
		PRECONDITIONS: {
			if (directory == null) {
				throw new NullPointerException("Directory must not be null.");
			}
		}
		
		final Collection<Class<LocalFileDBGraphManager>> managerClasses = getManagerClasses(LocalFileDBGraphManager.class);
		
		SANITY: {
			assert managerClasses != null;
		}
		
		if (managerClasses.isEmpty()) {
			throw new NoClassDefFoundError(String.format("There is no implementation of '%s' on the classpath.",
			                                             LocalFileDBGraphManager.class.getCanonicalName()));
		} else {
			boolean success = false;
			final Iterator<Class<LocalFileDBGraphManager>> iterator = managerClasses.iterator();
			LocalFileDBGraphManager manager = null;
			while (!success && iterator.hasNext()) {
				
				final Class<LocalFileDBGraphManager> c1 = iterator.next();
				if ((type != null)
				        && !type.name()
				                .toUpperCase()
				                .equals(c1.getSimpleName().toUpperCase()
				                          .replace(GraphManager.class.getSimpleName().toUpperCase(), ""))) {
					if (Logger.logInfo()) {
						Logger.info("Skipping '%s' ", c1.getCanonicalName());
					}
					continue;
				}
				if (Logger.logInfo()) {
					Logger.info("Instantiating %s implementation: %s", LocalFileDBGraphManager.class.getSimpleName(),
					            c1.getCanonicalName());
				}
				manager = null;
				Constructor<LocalFileDBGraphManager> constructor = null;
				
				try {
					constructor = c1.getConstructor(new Class<?>[] { File.class });
				} catch (NoSuchMethodException | SecurityException ignore) {
					// ignore
				}
				
				if (constructor != null) {
					try {
						manager = constructor.newInstance(directory);
					} catch (IllegalArgumentException | InvocationTargetException | InstantiationException
					        | IllegalAccessException ignore) {
						// ignore
					}
					
					if (manager != null) {
						success = true;
						return manager;
					} else {
						if (Logger.logWarn()) {
							Logger.warn("Instantiation of '%s' failed.", c1.getCanonicalName());
						}
						continue;
					}
				} else {
					try {
						manager = c1.newInstance();
						manager.setDirectory(directory);
						success = true;
						return manager;
					} catch (InstantiationException | IllegalAccessException ignore) {
						// ignore
					}
					
					if (Logger.logWarn()) {
						Logger.warn("Instantiation of '%s' failed.", c1.getCanonicalName());
					}
					continue;
				}
			}
			if (success) {
				SANITY: {
					assert manager != null;
				}
				
				return manager;
			} else {
				throw new InstantiationException(String.format("Could not instantiate any instance of '%s'",
				                                               LocalFileDBGraphManager.class.getSimpleName()));
			}
		}
		
	}
	
	/**
	 * Gets the manager classes.
	 * 
	 * @return the manager classes
	 */
	public static final Collection<Class<? extends GraphManager>> getManagerClasses() {
		return map.values();
	}
	
	/**
	 * Gets the manager classes.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the manager classes
	 */
	public static final <T extends GraphManager> Collection<Class<T>> getManagerClasses(final Class<T> clazz) {
		PRECONDITIONS: {
			if (clazz == null) {
				throw new NullPointerException("Base class must not be null.");
			}
		}
		
		final Collection<Class<T>> collection = new LinkedList<>();
		
		try {
			
			SANITY: {
				assert map != null;
			}
			
			for (final Class<? extends GraphManager> c : map.values()) {
				if (clazz.isAssignableFrom(c)) {
					try {
						@SuppressWarnings ("unchecked")
						final Class<T> d = (Class<T>) c;
						collection.add(d);
					} catch (final ClassCastException e) {
						SANITY: {
							assert false : String.format("Could not cast '%s' to '%s'.", c.getCanonicalName(),
							                             clazz.getCanonicalName());
						}
					}
				}
			}
			
			return collection;
		} finally {
			POSTCONDITIONS: {
				assert collection != null;
			}
		}
	}
	
	/**
	 * Creates the index.
	 * 
	 * @param graphIndex
	 *            the graph index
	 */
	public abstract void createIndex(GraphIndex graphIndex);
	
	/**
	 * Creates the key index.
	 * 
	 * @param <X>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param elementClass
	 *            the element class
	 * @param indexParameters
	 *            the index parameters
	 */
	public <X extends Element> void createKeyIndex(final String key,
	                                               final Class<X> elementClass,
	                                               final Parameter<?, ?>... indexParameters) {
		getGraph().createKeyIndex(key, elementClass, indexParameters);
	}
	
	/**
	 * Creates the util.
	 * 
	 * @return the graph
	 */
	public abstract KeyIndexableGraph createUtil();
	
	/**
	 * Gets the file handle.
	 * 
	 * @return the file handle/directory of the underlying file database; null otherwise
	 */
	public abstract File getFileHandle();
	
	/**
	 * Gets the graph.
	 * 
	 * @param <T>
	 *            the generic type
	 * @return the graph
	 */
	public abstract <T extends KeyIndexableGraph> T getGraph();
	
	/**
	 * Checks if is file based.
	 * 
	 * @return true, if is file based
	 */
	public abstract boolean isFileBased();
	
	/**
	 * Checks if is local.
	 * 
	 * @return true, if is local
	 */
	public abstract boolean isLocal();
	
	/**
	 * Provides.
	 * 
	 * @return the graph type
	 */
	public abstract GraphType provides();
}
