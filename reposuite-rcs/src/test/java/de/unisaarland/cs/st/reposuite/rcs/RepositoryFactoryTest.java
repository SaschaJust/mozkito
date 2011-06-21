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
package de.unisaarland.cs.st.reposuite.rcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryFactory;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;
import de.unisaarland.cs.st.reposuite.rcs.cvs.CVSRepository;
import de.unisaarland.cs.st.reposuite.rcs.git.GitRepository;
import de.unisaarland.cs.st.reposuite.rcs.mercurial.MercurialRepository;
import de.unisaarland.cs.st.reposuite.rcs.subversion.SubversionRepository;

public class RepositoryFactoryTest {
	
	@Test
	public void testRegistration() {
		try {
			Class<? extends Repository> repositoryHandler = RepositoryFactory
			        .getRepositoryHandler(RepositoryType.SUBVERSION);
			assertEquals(SubversionRepository.class, repositoryHandler);
			repositoryHandler = RepositoryFactory.getRepositoryHandler(RepositoryType.GIT);
			assertEquals(GitRepository.class, repositoryHandler);
			repositoryHandler = RepositoryFactory.getRepositoryHandler(RepositoryType.MERCURIAL);
			assertEquals(MercurialRepository.class, repositoryHandler);
			repositoryHandler = RepositoryFactory.getRepositoryHandler(RepositoryType.CVS);
			assertEquals(CVSRepository.class, repositoryHandler);
			
		} catch (UnregisteredRepositoryTypeException e) {
			fail(e.getMessage());
		}
	}
}
