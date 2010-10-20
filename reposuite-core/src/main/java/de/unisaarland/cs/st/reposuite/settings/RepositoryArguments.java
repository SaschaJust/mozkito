package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.RepositoryFactory;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositoryArguments extends RepoSuiteArgumentSet {
	
	private final URIArgument    repoDirArg;
	private final EnumArgument   repoTypeArg;
	private final StringArgument userArg;
	private final StringArgument passArg;
	
	/**
	 * Is an argument set that contains all arguments necessary for the
	 * repositories.
	 * 
	 * @param settings
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments(RepoSuiteSettings settings, boolean isRequired) throws DuplicateArgumentException {
		super();
		repoDirArg = new URIArgument(settings, "minerRCSDirectory", "Directory where the rcs repository is stored",
		        null, true);
		RepositoryType[] rcsTypes = RepositoryType.values();
		String[] argEnums = new String[rcsTypes.length];
		StringBuilder ss = new StringBuilder();
		ss.append("Type of the repository. Possible values: ");
		for (int i = 0; i < rcsTypes.length; ++i) {
			argEnums[i] = rcsTypes[i].toString();
			ss.append(rcsTypes[i].toString());
			ss.append(" ");
		}
		repoTypeArg = new EnumArgument(settings, "rcsType", ss.toString(), null, isRequired, argEnums);
		userArg = new StringArgument(settings, "rcsUser", "Username to access repository", null, false);
		passArg = new StringArgument(settings, "rcsPassword", "Password to access repository", null, false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Repository getValue() {
		URI repositoryURI = repoDirArg.getValue();
		String username = userArg.getValue();
		String password = passArg.getValue();
		RepositoryType rcsType = RepositoryType.valueOf(repoTypeArg.getValue());
		
		if (((username == null) && (password != null)) || ((username != null) && (password == null))) {
			Logger.warn("You provided username or password only. Ignoring set options.");
			username = null;
			password = null;
		}
		
		try {
			Class<? extends Repository> repositoryClass = RepositoryFactory.getRepositoryHandler(rcsType);
			Repository repository = repositoryClass.newInstance();
			if ((username != null) && (password != null)) {
				repository.setup(repositoryURI);
			} else {
				repository.setup(repositoryURI, username, password);
			}
			return repository;
		} catch (UnregisteredRepositoryTypeException e) {
			Logger.error(e.getMessage());
			throw new RuntimeException();
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}
}
