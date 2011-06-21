/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryFactory;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepositoryArguments extends RepoSuiteArgumentSet {
	
	private final StringArgument    endRevision;
	private final StringArgument    passArg;
	private final URIArgument       repoDirArg;
	private final EnumArgument      repoTypeArg;
	private final StringArgument    startRevision;
	private final StringArgument    userArg;
	private final RepoSuiteSettings settings;
	
	/**
	 * Is an argument set that contains all arguments necessary for the
	 * repositories.
	 * 
	 * @param settings
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments(final RepositorySettings settings, final boolean isRequired) {
		super();
		this.repoDirArg = new URIArgument(settings, "repository.uri", "URI where the rcs repository is located", null,
		        isRequired);
		this.repoTypeArg = new EnumArgument(settings, "repository.type", "Type of the repository. Possible values: "
		        + JavaUtils.enumToString(RepositoryType.SUBVERSION), null, isRequired,
		        JavaUtils.enumToArray(RepositoryType.SUBVERSION));
		this.userArg = new MaskedStringArgument(settings, "repository.user", "Username to access repository", null,
		        false);
		this.passArg = new MaskedStringArgument(settings, "repository.password", "Password to access repository", null,
		        false);
		this.startRevision = new StringArgument(settings, "repository.transaction.start", "Revision to start with",
		        null, false);
		this.endRevision = new StringArgument(settings, "repository.transaction.stop", "Revision to stop at", "HEAD",
		        false);
		this.settings = settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Repository getValue() {
		URI repositoryURI = this.getRepoDirArg().getValue();
		String username = this.getUserArg().getValue();
		String password = this.getPassArg().getValue();
		String startRevision = this.getStartRevision().getValue();
		String endRevision = this.endRevision.getValue();
		
		if (JavaUtils.AnyNull(repositoryURI, this.getRepoTypeArg().getValue())) {
			return null;
		}
		
		RepositoryType rcsType = RepositoryType.valueOf(this.getRepoTypeArg().getValue());
		
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
			
			if ((username == null) && (password == null)) {
				repository.setup(repositoryURI, startRevision, endRevision);
			} else {
				repository.setup(repositoryURI, startRevision, endRevision, username, password);
			}
			
			this.settings.addToolInformation(repository.getHandle(), repository.gatherToolInformation());
			
			return repository;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}

	public StringArgument getUserArg() {
	    return userArg;
    }

	public StringArgument getPassArg() {
	    return passArg;
    }

	public URIArgument getRepoDirArg() {
	    return repoDirArg;
    }

	public StringArgument getStartRevision() {
	    return startRevision;
    }

	public EnumArgument getRepoTypeArg() {
	    return repoTypeArg;
    }
}
