package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;

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
		this.repoDirArg = new URIArgument(settings, "minerRCSDirectory", "Directory where the rcs repository is stored",
				null, isRequired);
		this.repoTypeArg = new EnumArgument(settings, "rcsType", "Type of the repository. Possible values: "
		        + JavaUtils.enumToString(RepositoryType.SUBVERSION), null, isRequired,
		        JavaUtils.enumToArray(RepositoryType.SUBVERSION));
		this.userArg = new StringArgument(settings, "rcsUser", "Username to access repository", null, false);
		this.passArg = new StringArgument(settings, "rcsPassword", "Password to access repository", null, false);
		this.startRevision = new StringArgument(settings, "rcsStart", "Revision to start with", null, false);
		this.endRevision = new StringArgument(settings, "rcsStop", "Revision to stop at", "HEAD", false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Repository getValue() {
		URI repositoryURI = this.repoDirArg.getValue();
		String username = this.userArg.getValue();
		String password = this.passArg.getValue();
		String startRevision = this.startRevision.getValue();
		String endRevision = this.endRevision.getValue();
		
		if (JavaUtils.AnyNull(repositoryURI, this.repoTypeArg.getValue())) {
			return null;
		}
		
		RepositoryType rcsType = RepositoryType.valueOf(this.repoTypeArg.getValue());
		
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
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
}
