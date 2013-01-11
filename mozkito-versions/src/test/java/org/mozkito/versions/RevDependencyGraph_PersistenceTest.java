/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Test;
import org.mozkito.GraphBuilder;
import org.mozkito.RepositoryParser;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class RevDependencyGraphTest.
 */
@RepositorySettings ({ @RepositorySetting (type = RepositoryType.GIT, uri = "testGit.zip", id = "testGit") })
@DatabaseSettings (unit = "versions", options = ConnectOptions.DROP_AND_CREATE_DATABASE)
public class RevDependencyGraph_PersistenceTest extends VersionsTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Test restored rev dep graph.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RepositoryOperationException
	 *             the repository operation exception
	 */
	@Test
	public void testRestoredRevDepGraph() throws IOException, RepositoryOperationException {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		
		assertTrue(getRepositories().containsKey("testGit"));
		
		final Repository repository = getRepositories().get("testGit");
		
		final RevDependencyGraph revDepGraph = repository.getRevDependencyGraph();
		final VersionArchive versionArchive = new VersionArchive(new BranchFactory(getPersistenceUtil()), revDepGraph);
		versionArchive.setRevDependencyGraph(repository.getRevDependencyGraph());
		
		persistenceUtil.beginTransaction();
		final Iterator<LogEntry> logIterator = repository.log(repository.getFirstRevisionId(),
		                                                      repository.getEndRevision()).iterator();
		while (logIterator.hasNext()) {
			final LogEntry logEntry = logIterator.next();
			
			final ChangeSet changeset = RepositoryParser.parseLogEntry(repository, versionArchive, logEntry);
			persistenceUtil.save(changeset);
		}
		persistenceUtil.commitTransaction();
		
		final GraphBuilder graphBuilder = new GraphBuilder(repository, persistenceUtil);
		graphBuilder.phaseOne();
		graphBuilder.phaseTwo();
		graphBuilder.phaseThree();
		
		repository.resetRevDependencyGraph();
		
		final RevDependencyGraph persistedRevDepGraph = repository.getRevDependencyGraph(persistenceUtil);
		assertEquals(true, revDepGraph.isEqualsTo(persistedRevDepGraph));
	}
}
