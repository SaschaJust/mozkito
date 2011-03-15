package de.unisaarland.cs.st.reposuite.rcs;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
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
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(
			                                                                                  Repository.class.getPackage(), Repository.class);
			
			for (Class<?> klass : classesExtendingClass) {
				addRepositoryHandler(
				                     (RepositoryType) klass.getMethod("getRepositoryType", new Class<?>[0]).invoke(
				                                                                                                   klass.getConstructor(new Class<?>[0]).newInstance(new Object[0]), new Object[0]),
				                                                                                                   (Class<? extends Repository>) klass);
			}
		} catch (InvocationTargetException e) {
			if (Logger.logError()) {
				// check if someone missed to add a corresponding enum entry in
				// RepositoryType
				if (e.getCause() instanceof IllegalArgumentException) {
					Logger.error("You probably missed to add an enum constant to " + RepositoryType.getHandle()
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
	 * registers a repository to the factory keyed by the {@link RepositoryType}
	 * and version string
	 * 
	 * @param repositoryIdentifier
	 *            not null
	 * @param repositoryClass
	 *            class object implementing {@link Repository}, not null
	 */
	private static void addRepositoryHandler(@NotNull final RepositoryType repositoryIdentifier,
	                                         @NotNull final Class<? extends Repository> repositoryClass) {
		Condition.isNull(repositoryHandlers.get(repositoryIdentifier),
		"The should not be a reposiotry with the same identifier already");
		
		if (RepositorySettings.debug) {
			if (Logger.logDebug()) {
				Logger.debug("Adding new RepositoryType handler " + repositoryIdentifier.toString() + ".");
			}
		}
		
		repositoryHandlers.put(repositoryIdentifier, repositoryClass);
		
		Condition.notNull(repositoryHandlers.get(repositoryIdentifier),
		"The must be a repository with the identifier just been created and assigned.");
		CompareCondition.equals(repositoryHandlers.get(repositoryIdentifier), repositoryClass,
		"The must be a repository with the identifier just been created and assigned.");
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
	@NoneNull
	public static Class<? extends Repository> getRepositoryHandler(final RepositoryType repositoryIdentifier)
	throws UnregisteredRepositoryTypeException {
		if (RepositorySettings.debug) {
			if (Logger.logDebug()) {
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
