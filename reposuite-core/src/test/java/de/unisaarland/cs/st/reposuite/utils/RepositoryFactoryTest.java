package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UnregisteredRepositoryTypeException;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.RepositoryType;

public class RepositoryFactoryTest {
	
	@Test
	public void testRegistration() {
		try {
			Class<? extends Repository> repositoryHandler = RepositoryFactory
			        .getRepositoryHandler(RepositoryType.SUBVERSION);
			
		} catch (UnregisteredRepositoryTypeException e) {
			fail(e.getMessage());
		}
	}
}
