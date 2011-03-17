/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.UnregisteredTrackerTypeException;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@SuppressWarnings ("unchecked")
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
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(
			                                                                                  Tracker.class.getPackage(), Tracker.class);
			
			for (Class<?> klass : classesExtendingClass) {
				addTrackerHandler(
				                  (TrackerType) klass.getMethod("getTrackerType", new Class<?>[0]).invoke(
				                                                                                          klass.getConstructor(new Class<?>[0]).newInstance(new Object[0]), new Object[0]),
				                                                                                          (Class<? extends Tracker>) klass);
			}
		} catch (InvocationTargetException e) {
			if (Logger.logError()) {
				// check if someone missed to add a corresponding enum entry in
				// TrackerType
				if (e.getCause() instanceof IllegalArgumentException) {
					Logger.error("You probably missed to add an enum constant to " + TrackerType.getHandle()
					             + ". Error was: " + e.getCause().getMessage(), e.getCause());
				} else {
					Logger.error(e.getMessage(), e);
				}
			}
			throw new RuntimeException();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
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
	 * returns a repository class object to the corresponding
	 * repositoryIdentifier and version (=default if null)
	 * 
	 * @param trackerIdentifier
	 *            not null
	 * @return the corresponding {@link Tracker} class object
	 * @throws UnregisteredTrackerTypeException
	 *             * if no matching tracker class object could be found in the
	 *             registry
	 */
	public static Class<? extends Tracker> getTrackerHandler(final TrackerType trackerIdentifier)
	throws UnregisteredTrackerTypeException {
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
