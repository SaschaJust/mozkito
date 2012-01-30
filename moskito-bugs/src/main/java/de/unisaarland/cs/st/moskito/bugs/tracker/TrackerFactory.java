/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.exceptions.UnregisteredTrackerTypeException;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerFactory {
	
	/**
	 * container for repository connector mappings
	 */
	private static Map<TrackerType, Class<? extends Tracker>> trackerHandlers = new HashMap<TrackerType, Class<? extends Tracker>>();
	
	/**
	 * static registration of all modules extending {@link Tracker}
	 */
	static {
		// ======== Tracker handlers ========
		try {
			Collection<Class<? extends Tracker>> classesExtendingClass = ClassFinder.getClassesExtendingClass(Tracker.class.getPackage(),
			                                                                                                  Tracker.class,
			                                                                                                  Modifier.ABSTRACT
			                                                                                                          | Modifier.INTERFACE
			                                                                                                          | Modifier.PRIVATE);
			
			for (Class<? extends Tracker> klass : classesExtendingClass) {
				addTrackerHandler((TrackerType) klass.getMethod("getTrackerType", new Class<?>[0])
				                                     .invoke(klass.getConstructor(new Class<?>[0])
				                                                  .newInstance(new Object[0]), new Object[0]), klass);
			}
		} catch (InvocationTargetException e) {
			if (Logger.logError()) {
				// check if someone missed to add a corresponding enum entry in
				// TrackerType
				if (e.getCause() instanceof IllegalArgumentException) {
					Logger.error("You probably missed to add an enum constant to " + TrackerType.getHandle()
					        + ". Error was: " + e.getCause().getMessage(), e.getCause());
				}
			}
			throw new UnrecoverableError(e);
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		} catch (SecurityException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalAccessException e) {
			throw new UnrecoverableError(e);
		} catch (NoSuchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (InstantiationException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param trackerIdentifier
	 * @param trackerClass
	 */
	private static void addTrackerHandler(final TrackerType trackerIdentifier,
	                                      final Class<? extends Tracker> trackerClass) {
		assert (trackerIdentifier != null);
		assert (trackerClass != null);
		assert (trackerHandlers.get(trackerIdentifier) == null);
		
		if (Logger.logDebug()) {
			Logger.debug("Adding new TrackerType handler " + trackerIdentifier.toString() + ".");
		}
		
		trackerHandlers.put(trackerIdentifier, trackerClass);
		
		assert (trackerHandlers.get(trackerIdentifier) != null);
		assert (trackerHandlers.get(trackerIdentifier) == trackerClass);
	}
	
	/**
	 * returns a repository class object to the corresponding repositoryIdentifier and version (=default if null)
	 * 
	 * @param trackerIdentifier
	 *            not null
	 * @return the corresponding {@link Tracker} class object
	 * @throws UnregisteredTrackerTypeException
	 *             * if no matching tracker class object could be found in the registry
	 */
	public static Class<? extends Tracker> getTrackerHandler(final TrackerType trackerIdentifier) throws UnregisteredTrackerTypeException {
		assert (trackerIdentifier != null);
		
		if (Logger.logInfo()) {
			Logger.info("Requesting tracker handler for " + trackerIdentifier.toString() + ".");
		}
		
		Class<? extends Tracker> trackerClass = trackerHandlers.get(trackerIdentifier);
		
		if (trackerClass == null) {
			throw new UnregisteredTrackerTypeException("Unsupported repository type `" + trackerIdentifier.toString()
			        + "`");
		} else {
			return trackerClass;
		}
	}
	
	/**
	 * private constructor avoids instantiation
	 */
	private TrackerFactory() {
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
