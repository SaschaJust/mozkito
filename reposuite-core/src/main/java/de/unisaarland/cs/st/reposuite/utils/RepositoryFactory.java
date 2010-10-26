package de.unisaarland.cs.st.reposuite.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

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
				        (RepositoryType) klass.getMethod("getRepositoryType", null).invoke(
				                klass.getConstructor(null).newInstance(null), null),
				        (Class<? extends Repository>) klass);
			}
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
