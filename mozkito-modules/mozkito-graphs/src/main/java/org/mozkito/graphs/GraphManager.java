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
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Parameter;

import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.DatabaseEnvironment;
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
	 * Gets the manager classes.
	 * 
	 * @return the manager classes
	 */
	public static final Collection<Class<? extends GraphManager>> getManagerClasses() {
		return map.values();
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
	public abstract <X extends Element> void createKeyIndex(String key,
	                                                        Class<X> elementClass,
	                                                        Parameter<?, ?>... indexParameters);
	
	/**
	 * Creates the util.
	 * 
	 * @return the graph
	 */
	public abstract KeyIndexableGraph createUtil();
	
	/**
	 * Gets the database environment if any.
	 * 
	 * @return the database environment or null if the underlying database is file-based.
	 */
	public abstract DatabaseEnvironment getDatabaseEnvironment();
	
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
