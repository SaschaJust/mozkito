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

import java.util.Iterator;

import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Test;
import org.mozkito.RepositoryParser;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.VersionsTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.testing.annotation.RepositorySetting;
import org.mozkito.testing.annotation.RepositorySettings;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class RevDependencyGraphTest.
 */
@RepositorySettings ({ @RepositorySetting (type = RepositoryType.GIT, uri = "testGit.zip", id = "GIT") })
@DatabaseSettings (unit = "versions")
public class RevDependencyGraph_PersistenceTest extends VersionsTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Test restored rev dep graph.
	 */
	@Test
	public void testRestoredRevDepGraph() {
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		
		assertTrue(getRepositories().containsKey("testGit"));
		
		final Repository repository = getRepositories().get("testGit");
		
		persistenceUtil.beginTransaction();
		
		final RevDependencyGraph revDepGraph = repository.getRevDependencyGraph();
		final Iterator<LogEntry> logIterator = repository.log(repository.getFirstRevisionId(),
		                                                      repository.getEndRevision()).iterator();
		while (logIterator.hasNext()) {
			final LogEntry logEntry = logIterator.next();
			final RCSTransaction rcsTransaction = RepositoryParser.parseLogEntry(repository, logEntry);
			persistenceUtil.save(rcsTransaction);
		}
		
		persistenceUtil.commitTransaction();
		
		repository.resetRevDependencyGraph();
		
		final RevDependencyGraph persistedRevDepGraph = repository.getRevDependencyGraph(persistenceUtil);
		assertEquals(true, revDepGraph.isEqualsTo(persistedRevDepGraph));
	}
}
