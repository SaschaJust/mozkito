package de.unisaarland.cs.st.reposuite.utils;

import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.RCSType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.cvs.CVSRepository;
import de.unisaarland.cs.st.reposuite.rcs.git.GitRepository;
import de.unisaarland.cs.st.reposuite.rcs.mercurial.MercurialRepository;
import de.unisaarland.cs.st.reposuite.rcs.subversion.SubversionRepository;

public final class RepositoryFactory {
	
	private static Map<RCSType, Map<String, Class<? extends Repository>>> repositoryHandlers = new HashMap<RCSType, Map<String, Class<? extends Repository>>>();
	
	static {
		// ======== Repository handlers ========
		// Subversion
		addRepositoryHandler(RCSType.SUBVERSION, null, SubversionRepository.class);
		addRepositoryHandler(RCSType.SUBVERSION, "1.6", SubversionRepository.class);
		
		// Mercurial
		addRepositoryHandler(RCSType.MERCURIAL, null, MercurialRepository.class);
		addRepositoryHandler(RCSType.MERCURIAL, "1.6.3", MercurialRepository.class);
		
		// Git
		addRepositoryHandler(RCSType.GIT, null, GitRepository.class);
		addRepositoryHandler(RCSType.GIT, "1.6.3.3", GitRepository.class);
		
		// CVS
		addRepositoryHandler(RCSType.CVS, null, CVSRepository.class);
		addRepositoryHandler(RCSType.CVS, "1.12.13", CVSRepository.class);
	}
	
	/**
	 * registers a repository to the factory keyed by the {@link RCSType} and
	 * version string
	 * 
	 * @param repositoryIdentifier
	 * @param version
	 * @param repositoryClass
	 */
	private static void addRepositoryHandler(RCSType repositoryIdentifier, String version,
	        Class<? extends Repository> repositoryClass) {
		assert (repositoryIdentifier != null);
		
		Map<String, Class<? extends Repository>> map = new HashMap<String, Class<? extends Repository>>();
		map.put(version == null ? "default" : version.toLowerCase(), repositoryClass);
		repositoryHandlers.put(repositoryIdentifier, map);
		
		assert (repositoryHandlers.get(repositoryIdentifier) != null);
		assert (repositoryHandlers.get(repositoryIdentifier).size() > 0);
		assert (repositoryHandlers.get(repositoryIdentifier).get("default") != null);
	}
	
	/**
	 * returns a repository class object to the corresponding
	 * repositoryIdentifier and version (=default if null)
	 * 
	 * @param repositoryIdentifier
	 * @param version
	 * @return
	 * @throws UnregisteredRepositoryTypeException
	 */
	public static Class<? extends Repository> getRepositoryHandler(RCSType repositoryIdentifier, String version)
	        throws UnregisteredRepositoryTypeException {
		assert (repositoryIdentifier != null);
		
		Class<? extends Repository> repositoryClass = repositoryHandlers.get(repositoryIdentifier).get(
		        version != null ? version.toLowerCase() : "default");
		
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
