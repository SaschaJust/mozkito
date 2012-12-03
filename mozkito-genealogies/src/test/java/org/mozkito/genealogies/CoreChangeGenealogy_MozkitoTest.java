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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.utils.ChangeGenealogyUtils;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment.TestEnvironmentOperation;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.BranchFactory;

/**
 * The Class CoreChangeGenealogy_MozkitoTest.
 */
@DatabaseSettings (unit = "codeanalysis",
                   database = "moskito_genealogies_test_environment",
                   options = ConnectOptions.CREATE,
                   hostname = "grid1.st.cs.uni-saarland.de",
                   password = "miner",
                   username = "miner",
                   type = "POSTGRESQL",
                   driver = "org.postgresql.Driver",
                   remote = true)
public class CoreChangeGenealogy_MozkitoTest extends DatabaseTest {
	
	/**
	 * Test change genealogy.
	 */
	@Test
	public void testChangeGenealogy() {
		final File tmpGraphDBFile = FileUtils.createRandomDir(this.getClass().getSimpleName(), "",
		                                                      FileShutdownAction.DELETE);
		
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final GenealogyTestEnvironment testEnvironment = ChangeGenealogyUtils.getGenealogyTestEnvironment(tmpGraphDBFile,
		                                                                                                  branchFactory);
		CoreChangeGenealogy changeGenealogy = testEnvironment.getChangeGenealogy();
		final PersistenceUtil persistenceUtil = testEnvironment.getPersistenceUtil();
		final Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations = testEnvironment.getEnvironmentOperations();
		changeGenealogy.getTransactionLayer().close();
		changeGenealogy.close();
		changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile, persistenceUtil);
		assertEquals(41, changeGenealogy.vertexSize());
		assertEquals(16, changeGenealogy.edgeSize());
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                    environmentOperations.get(TestEnvironmentOperation.T1F2),
		                                    GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                    environmentOperations.get(TestEnvironmentOperation.T1F2),
		                                    GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                    environmentOperations.get(TestEnvironmentOperation.T1F2),
		                                    GenealogyEdgeType.CallOnDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                    environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                                    GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		// check if edges really exist
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
		                                        environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                                        environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                                        environmentOperations.get(TestEnvironmentOperation.T1F1)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
		                                        environmentOperations.get(TestEnvironmentOperation.T2F3)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
		                                        environmentOperations.get(TestEnvironmentOperation.T6F2)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
		                                        environmentOperations.get(TestEnvironmentOperation.T7F2)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
		                                        environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                        environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                        environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                        environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		
		assertTrue(changeGenealogy.containsEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                        environmentOperations.get(TestEnvironmentOperation.T5F4)));
		
		// check edge types
		
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                                     environmentOperations.get(TestEnvironmentOperation.T1F1)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
		                                     environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertEquals(GenealogyEdgeType.DefinitionOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                                     environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
		                                     environmentOperations.get(TestEnvironmentOperation.T2F3)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		
		assertEquals(GenealogyEdgeType.DefinitionOnDeletedDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
		                                     environmentOperations.get(TestEnvironmentOperation.T6F2)));
		
		assertEquals(GenealogyEdgeType.DefinitionOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
		                                     environmentOperations.get(TestEnvironmentOperation.T7F2)));
		
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
		                                     environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                     environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                     environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                     environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall,
		             changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                     environmentOperations.get(TestEnvironmentOperation.T5F4)));
		
		Collection<JavaChangeOperation> dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1D)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T1F2));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T2F3)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T1F2));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T2F3));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3D)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T2F3));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F2)));
		assertEquals(1, dependents.size());
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F1)));
		assertEquals(1, dependents.size());
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(5, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T6F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T3F2));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T2F3)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F3)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T4F4));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T5F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T5F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T6F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T7F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T6F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T7F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T8F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T7F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T6F2)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T8F2));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T8F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T7F2)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T9F1));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F3)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T9F1));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T10F3));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T10F3));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		dependents = changeGenealogy.getAllDependants(environmentOperations.get(TestEnvironmentOperation.T10F4));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T10F4));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		// selective dependencies
		
		dependents = changeGenealogy.getDependants(environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                           GenealogyEdgeType.CallOnDefinition);
		assertEquals(4, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F4)));
		
		dependents = changeGenealogy.getDependants(environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                           GenealogyEdgeType.DeletedDefinitionOnDefinition);
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		dependents = changeGenealogy.getDependants(environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                           GenealogyEdgeType.DefinitionOnDefinition,
		                                           GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                           GenealogyEdgeType.DeletedCallOnCall,
		                                           GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		assertEquals(0, dependents.size());
		
		final Set<GenealogyEdgeType> existingEdgeTypes = changeGenealogy.getExistingEdgeTypes();
		assertEquals(6, existingEdgeTypes.size());
		
		int iterCounter = 0;
		int hitCounter = 0;
		final Iterator<JavaChangeOperation> iterator = changeGenealogy.vertexIterator();
		while (iterator.hasNext()) {
			final JavaChangeOperation tmp = iterator.next();
			assertNotNull(tmp);
			if (environmentOperations.containsValue(tmp)) {
				++hitCounter;
			}
			++iterCounter;
		}
		assertEquals(41, iterCounter);
		assertEquals(17, hitCounter);
		
		// test roots
		final Collection<JavaChangeOperation> roots = changeGenealogy.getRoots();
		assertTrue(roots.size() >= 7);
		
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T10F3)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T10F4)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T8F2)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T4F3D)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T4F4)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T3F1D)));
		assertTrue(roots.contains(environmentOperations.get(TestEnvironmentOperation.T3F2)));
		
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T1F1)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T1F2)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T2F3)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T6F2)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T7F2)));
		assertFalse(roots.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		changeGenealogy.close();
		try {
			FileUtils.deleteDirectory(tmpGraphDBFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
