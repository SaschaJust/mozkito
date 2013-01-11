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
package org.mozkito.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.Test;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.utils.ChangeGenealogyUtils;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionChangeGenealogy_MozkitoTest.
 */
@DatabaseSettings (unit = "codeanalysis",
                   database = "moskito_genealogies_test_environment",
                   options = ConnectOptions.VALIDATE_OR_CREATE_SCHEMA,
                   hostname = "grid1.st.cs.uni-saarland.de",
                   password = "miner",
                   username = "miner",
                   type = DatabaseType.POSTGRESQL,
                   remote = true)
public class TransactionChangeGenealogy_MozkitoTest extends DatabaseTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * Test.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	@Test
	public void test() throws IOException, FilePermissionException {
		final File tmpGraphDBFile = FileUtils.createRandomDir(this.getClass().getSimpleName(), "",
		                                                      FileShutdownAction.DELETE);
		final GenealogyTestEnvironment testEnvironment = ChangeGenealogyUtils.getGenealogyTestEnvironment(tmpGraphDBFile,
		                                                                                                  getPersistenceUtil());
		final CoreChangeGenealogy changeGenealogy = testEnvironment.getChangeGenealogy();
		final Map<Integer, ChangeSet> environmentTransactions = testEnvironment.getEnvironmentTransactions();
		
		changeGenealogy.close();
		final TransactionChangeGenealogy tdg = changeGenealogy.getChangeSetLayer();
		
		assertEquals(12, tdg.edgeSize());
		
		assertTrue(tdg.containsVertex(environmentTransactions.get(1)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(2)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(3)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(4)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(5)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(6)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(7)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(8)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(9)));
		assertTrue(tdg.containsVertex(environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(3)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(1), environmentTransactions.get(10)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(1)));
		Collection<GenealogyEdgeType> edges = tdg.getEdges(environmentTransactions.get(2),
		                                                   environmentTransactions.get(1));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.CallOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(3)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(2), environmentTransactions.get(10)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(1)));
		edges = tdg.getEdges(environmentTransactions.get(3), environmentTransactions.get(1));
		assertEquals(2, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DefinitionOnDefinition));
		assertTrue(edges.contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(3)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(1)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(2)));
		edges = tdg.getEdges(environmentTransactions.get(4), environmentTransactions.get(2));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(3)));
		edges = tdg.getEdges(environmentTransactions.get(4), environmentTransactions.get(3));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.CallOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(4), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(2)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(3)));
		edges = tdg.getEdges(environmentTransactions.get(5), environmentTransactions.get(3));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.CallOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(5), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(2)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(3)));
		edges = tdg.getEdges(environmentTransactions.get(6), environmentTransactions.get(3));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(6), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(3)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(5)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(6)));
		edges = tdg.getEdges(environmentTransactions.get(7), environmentTransactions.get(6));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(7), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(3)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(6)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(7)));
		edges = tdg.getEdges(environmentTransactions.get(8), environmentTransactions.get(7));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DefinitionOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(8), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(2)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(3)));
		edges = tdg.getEdges(environmentTransactions.get(9), environmentTransactions.get(3));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(4)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(5)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(8)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(9)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(9), environmentTransactions.get(10)));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(1)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(2)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(3)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(4)));
		edges = tdg.getEdges(environmentTransactions.get(10), environmentTransactions.get(4));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(5)));
		edges = tdg.getEdges(environmentTransactions.get(10), environmentTransactions.get(5));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedCallOnCall));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(6)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(7)));
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(8)));
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(9)));
		edges = tdg.getEdges(environmentTransactions.get(10), environmentTransactions.get(9));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.DeletedCallOnDeletedDefinition));
		
		assertFalse(tdg.containsEdge(environmentTransactions.get(10), environmentTransactions.get(10)));
		
		Collection<ChangeSet> dependents = tdg.getAllDependants(environmentTransactions.get(3));
		assertEquals(4, dependents.size());
		assertTrue(dependents.contains(environmentTransactions.get(4)));
		assertTrue(dependents.contains(environmentTransactions.get(5)));
		assertTrue(dependents.contains(environmentTransactions.get(6)));
		assertTrue(dependents.contains(environmentTransactions.get(9)));
		
		dependents = tdg.getDependants(environmentTransactions.get(3), GenealogyEdgeType.CallOnDefinition);
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentTransactions.get(4)));
		assertTrue(dependents.contains(environmentTransactions.get(5)));
		
		Collection<ChangeSet> parents = tdg.getAllParents(environmentTransactions.get(10));
		assertEquals(3, parents.size());
		assertTrue(parents.contains(environmentTransactions.get(4)));
		assertTrue(parents.contains(environmentTransactions.get(5)));
		assertTrue(parents.contains(environmentTransactions.get(9)));
		
		parents = tdg.getParents(environmentTransactions.get(10), GenealogyEdgeType.DeletedCallOnCall);
		assertEquals(2, parents.size());
		assertTrue(parents.contains(environmentTransactions.get(4)));
		assertTrue(parents.contains(environmentTransactions.get(5)));
		
		assertEquals(10, tdg.vertexSize());
		final Set<ChangeSet> vertices = new HashSet<ChangeSet>();
		for (final ChangeSet v : tdg.vertexSet()) {
			vertices.add(v);
		}
		
		assertTrue(environmentTransactions.values().containsAll(vertices));
		assertTrue(vertices.containsAll(environmentTransactions.values()));
		
		tdg.close();
	}
}
