/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
