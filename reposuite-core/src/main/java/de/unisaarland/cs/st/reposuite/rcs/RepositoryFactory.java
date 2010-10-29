package de.unisaarland.cs.st.reposuite.rcs;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@SuppressWarnings("unchecked")
public final class RepositoryFactory {
	
	/**
	 * container for repository connector mappings
	 */
	private static Map<RepositoryType, Class<? extends Repository>> repositoryHandlers = new HashMap<RepositoryType, Class<? extends Repository>>();
	
	/**
	 * static registration of all modules extending {@link Repository}
	 */
	static {
		// ======== Repository handlers ========
		try {
			List<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(Repository.class.getPackage(),
			        Repository.class);
			
			for (Class<?> klass : classesExtendingClass) {
				addRepositoryHandler(
				        (RepositoryType) klass.getMethod("getRepositoryType", new Class<?>[0]).invoke(
				                klass.getConstructor(new Class<?>[0]).newInstance(new Object[0]), new Object[0]),
				        (Class<? extends Repository>) klass);
			}
		} catch (InvocationTargetException e) {
			if (RepoSuiteSettings.logError()) {
				// check if someone missed to add a corresponding enum entry in RepositoryType
				if (e.getCause() instanceof IllegalArgumentException) {
					Logger.error("You probably missed to add an enum constant to " + RepositoryType.getHandle()
					        + ". Error was: " + e.getCause().getMessage(), e.getCause());
				} else {
					Logger.error(e.getMessage(), e);
				}
			}
			throw new RuntimeException();
		} catch (Exception e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * registers a repository to the factory keyed by the {@link RepositoryType}
	 * and version string
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @param repositoryClass
	 *            class object implementing {@link Repository}, not null
	 */
	private static void addRepositoryHandler(RepositoryType repositoryIdentifier,
	        Class<? extends Repository> repositoryClass) {
		assert (repositoryIdentifier != null);
		assert (repositoryClass != null);
		assert (repositoryHandlers.get(repositoryIdentifier) == null);
		
		if (RepoSuiteSettings.debug) {
			if (RepoSuiteSettings.logDebug()) {
				Logger.debug("Adding new RepositoryType handler " + repositoryIdentifier.toString() + ".");
			}
		}
		
		repositoryHandlers.put(repositoryIdentifier, repositoryClass);
		
		assert (repositoryHandlers.get(repositoryIdentifier) != null);
		assert (repositoryHandlers.get(repositoryIdentifier) == repositoryClass);
	}
	
	/**
	 * returns a repository class object to the corresponding
	 * repositoryIdentifier and version (=default if null)
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @return the corresponding {@link Repository} class object
	 * @throws UnregisteredRepositoryTypeException
	 *             if no matching repository class object could be found in the
	 *             registry
	 */
	public static Class<? extends Repository> getRepositoryHandler(RepositoryType repositoryIdentifier)
	        throws UnregisteredRepositoryTypeException {
		assert (repositoryIdentifier != null);
		
		if (RepoSuiteSettings.debug) {
			if (RepoSuiteSettings.logDebug()) {
				Logger.debug("Requesting repository handler for " + repositoryIdentifier.toString() + ".");
			}
		}
		Class<? extends Repository> repositoryClass = repositoryHandlers.get(repositoryIdentifier);
		
		if (repositoryClass == null) {
			throw new UnregisteredRepositoryTypeException("Unsupported repository type `"
			        + repositoryIdentifier.toString() + "`");
		} else {
			return repositoryClass;
		}
	}
	
	/**
	 * private constructor avoids instantiation
	 */
	private RepositoryFactory() {
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
