package de.unisaarland.cs.st.reposuite.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.rcs.cvs.CVSRepository;
import de.unisaarland.cs.st.reposuite.rcs.git.GitRepository;
import de.unisaarland.cs.st.reposuite.rcs.mercurial.MercurialRepository;
import de.unisaarland.cs.st.reposuite.rcs.subversion.SubversionRepository;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

public final class RepositoryFactory {
	
	private static Logger                                           logger             = LoggerFactory
	                                                                                           .getLogger(RepositoryFactory.class);
	
	/**
	 * container for repository connector mappings
	 */
	private static Map<RepositoryType, Class<? extends Repository>> repositoryHandlers = new HashMap<RepositoryType, Class<? extends Repository>>();
	
	/**
	 * static registration of known modules
	 */
	static {
		// this can be removed after adding classpath traversal search for classes implementing Repository
		// requires addRepositoryHandler to become public to register repository connectors from settings
		// class
		
		// ======== Repository handlers ========
		// Subversion
		addRepositoryHandler(RepositoryType.SUBVERSION, SubversionRepository.class);
		
		// Mercurial
		addRepositoryHandler(RepositoryType.MERCURIAL, MercurialRepository.class);
		
		// Git
		addRepositoryHandler(RepositoryType.GIT, GitRepository.class);
		
		// CVS
		addRepositoryHandler(RepositoryType.CVS, CVSRepository.class);
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
			logger.debug("[" + Utilities.getLineNumber() + "] Adding new RepositoryType handler "
			        + repositoryIdentifier.toString() + ".");
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
		
		Class<? extends Repository> repositoryClass = repositoryHandlers.get(repositoryIdentifier);
		
		if (repositoryClass == null) {
			throw new UnregisteredRepositoryTypeException();
		} else {
			return repositoryClass;
		}
	}
	
	/**
	 * private constructor avoids instantiation
	 */
	private RepositoryFactory() {
	}
}
