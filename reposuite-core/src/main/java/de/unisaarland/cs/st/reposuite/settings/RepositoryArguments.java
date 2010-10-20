package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;

import org.apache.log4j.Logger;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.utils.RepositoryFactory;

public class RepositoryArguments extends RepoSuiteArgumentSet {
	
	private final URIArgument    repoDirArg;
	private final EnumArgument   repoTypeArg;
	private final StringArgument userArg;
	private final StringArgument passArg;
	
	public RepositoryArguments(RepoSuiteSettings settings, boolean isRequired) throws DuplicateArgumentException {
		super();
		this.repoDirArg = new URIArgument(settings, "minerRCSDirectory",
		        "Directory where the rcs repository is stored", null, true);
		RepositoryType[] rcsTypes = RepositoryType.values();
		String[] argEnums = new String[rcsTypes.length];
		StringBuilder ss = new StringBuilder();
		ss.append("Type of the repository. Possible values: ");
		for (int i = 0; i < rcsTypes.length; ++i) {
			argEnums[i] = rcsTypes[i].toString();
			ss.append(rcsTypes[i].toString());
			ss.append(" ");
		}
		this.repoTypeArg = new EnumArgument(settings, "rcsType", ss.toString(), null, isRequired, argEnums);
		this.userArg = new StringArgument(settings, "rcsUser", "Username to access repository", null, false);
		this.passArg = new StringArgument(settings, "rcsPassword", "Password to access repository", null, false);
	}
	
	@Override
	public Repository getValue() {
		URI repositoryURI = this.repoDirArg.getValue();
		String username = this.userArg.getValue();
		String password = this.passArg.getValue();
		RepositoryType rcsType = RepositoryType.valueOf(this.repoTypeArg.getValue());
		
		if (((username == null) && (password != null)) || ((username != null) && (password == null))) {
			Logger.getLogger(RepositoryArguments.class).warn(
			        "You provided username or password only. Ignoring set options.");
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
			Logger.getLogger(RepositoryArguments.class).error(e.getMessage(), e);
			throw new RuntimeException();
		} catch (InstantiationException e) {
			Logger.getLogger(RepositoryArguments.class).error(e.getMessage(), e);
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			Logger.getLogger(RepositoryArguments.class).error(e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
