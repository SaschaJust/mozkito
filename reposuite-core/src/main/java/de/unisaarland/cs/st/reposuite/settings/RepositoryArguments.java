package de.unisaarland.cs.st.reposuite.settings;

import java.net.MalformedURLException;
import java.net.URI;

import de.unisaarland.cs.st.reposuite.exceptions.InvalidProtocolType;
import de.unisaarland.cs.st.reposuite.exceptions.InvalidRepositoryURI;
import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolType;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryFactory;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositoryArguments extends RepoSuiteArgumentSet {
	
	private final StringArgument endRevision;
	private final StringArgument passArg;
	private final URIArgument    repoDirArg;
	private final EnumArgument   repoTypeArg;
	private final StringArgument startRevision;
	private final StringArgument userArg;
	
	/**
	 * Is an argument set that contains all arguments necessary for the
	 * repositories.
	 * 
	 * @param settings
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments(final RepoSuiteSettings settings, final boolean isRequired) {
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
		startRevision = new StringArgument(settings, "rcsStart", "Revision to start with", null, false);
		endRevision = new StringArgument(settings, "rcsStop", "Revision to stop at", "HEAD", false);
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
		String startRevision = this.startRevision.getValue();
		String endRevision = this.endRevision.getValue();
		
		if (JavaUtils.AnyNull(repositoryURI, username, password, startRevision, endRevision)) {
			return null;
		}
		
		RepositoryType rcsType = RepositoryType.valueOf(repoTypeArg.getValue());
		
		if (((username == null) && (password != null)) || ((username != null) && (password == null))) {
			if (Logger.logWarn()) {
				Logger.warn("You provided username or password only. Ignoring set options.");
			}
			username = null;
			password = null;
		}
		
		try {
			Class<? extends Repository> repositoryClass = RepositoryFactory.getRepositoryHandler(rcsType);
			Repository repository = repositoryClass.newInstance();
			if ((username != null) && (password != null)) {
				repository.setup(repositoryURI, startRevision, endRevision);
			} else {
				repository.setup(repositoryURI, startRevision, endRevision, username, password);
			}
			return repository;
		} catch (UnregisteredRepositoryTypeException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (InstantiationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (InvalidProtocolType e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (InvalidRepositoryURI e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		} catch (UnsupportedProtocolType e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage());
			}
			throw new RuntimeException();
		}
	}
}
