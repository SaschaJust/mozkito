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
