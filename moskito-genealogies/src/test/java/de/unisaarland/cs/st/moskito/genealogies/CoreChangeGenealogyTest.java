package de.unisaarland.cs.st.moskito.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class CoreChangeGenealogyTest extends TestEnvironment {
	
	@Test
	public void testChangeGenealogy() {
		TestEnvironment.setup();
		
		File tmpGraphDBFile = FileUtils
				.createRandomDir("reposuite", "change_genealogy_test", FileShutdownAction.DELETE);
		
		CoreChangeGenealogy changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile,
				super.getPersistenceUtil());
		assertTrue(changeGenealogy != null);
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation operation : transactionEntry.getValue()) {
				changeGenealogy.addVertex(operation);
			}
		}
		
		assertEquals(41, changeGenealogy.vertexSize());
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation op : transactionEntry.getValue()) {
				assertTrue(changeGenealogy.hasVertex(op));
				assertFalse(changeGenealogy.addVertex(op));
			}
		}
		
		//TODO test adding edges
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.DefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
				environmentOperations.get(TestEnvironmentOperation.T2F3), GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
				environmentOperations.get(TestEnvironmentOperation.T3F2M),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
				environmentOperations.get(TestEnvironmentOperation.T6F2),
				GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
				environmentOperations.get(TestEnvironmentOperation.T7F2), GenealogyEdgeType.DefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
				environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
				GenealogyEdgeType.DeletedCallOnDeletedDefinition));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
				GenealogyEdgeType.DeletedCallOnDeletedDefinition));
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T4F3A), GenealogyEdgeType.DeletedCallOnCall));
		
		assertTrue(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T5F4), GenealogyEdgeType.DeletedCallOnCall));
		
		assertEquals(16, changeGenealogy.edgeSize());
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
				environmentOperations.get(TestEnvironmentOperation.T1F2),
				GenealogyEdgeType.DeletedDefinitionOnDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
				environmentOperations.get(TestEnvironmentOperation.T1F2),
				GenealogyEdgeType.DefinitionOnDeletedDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.CallOnDefinition));
		
		assertFalse(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T1F1),
				environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.DeletedCallOnCall));
		
		//check if edges really exist
		
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
		
		//check edge types
		
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1)));
		
		assertEquals(
				GenealogyEdgeType.CallOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
						environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertEquals(GenealogyEdgeType.DefinitionOnDefinition, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T3F2M),
				environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		assertEquals(
				GenealogyEdgeType.CallOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
						environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T4F3D),
				environmentOperations.get(TestEnvironmentOperation.T2F3)));
		
		assertEquals(GenealogyEdgeType.CallOnDefinition, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T4F3A),
				environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(
				GenealogyEdgeType.CallOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
						environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(
				GenealogyEdgeType.CallOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
						environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(
				GenealogyEdgeType.DeletedDefinitionOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
						environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		
		assertEquals(
				GenealogyEdgeType.DefinitionOnDeletedDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
						environmentOperations.get(TestEnvironmentOperation.T6F2)));
		
		assertEquals(
				GenealogyEdgeType.DefinitionOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
						environmentOperations.get(TestEnvironmentOperation.T7F2)));
		
		assertEquals(
				GenealogyEdgeType.DeletedDefinitionOnDefinition,
				changeGenealogy.getEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
						environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnDeletedDefinition, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnDeletedDefinition, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		
		assertEquals(GenealogyEdgeType.DeletedCallOnCall, changeGenealogy.getEdge(
				environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T5F4)));
		
		
		Collection<JavaChangeOperation> dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1D)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T1F2));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T2F3)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T1F2));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T2F3));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3D)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T2F3));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F2)));
		assertEquals(1, dependents.size());
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F1)));
		assertEquals(1, dependents.size());
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(5, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(0, dependents.size());
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T6F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T1F2)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T3F2));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T3F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T2F3)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F3)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T4F4));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T4F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T5F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T5F4));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T6F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T7F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T6F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2M)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T7F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T8F2)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T7F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T6F2)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T8F2));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T8F2));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T7F2)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T9F1));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F3)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T10F4)));
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T9F1));
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F1A)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T10F3));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T10F3));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		dependents = changeGenealogy.getAllDependents(environmentOperations.get(TestEnvironmentOperation.T10F4));
		assertEquals(0, dependents.size());
		dependents = changeGenealogy.getAllParents(environmentOperations.get(TestEnvironmentOperation.T10F4));
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		//selective dependencies
		
		dependents = changeGenealogy.getDependents(environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.CallOnDefinition);
		assertEquals(4, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T3F2)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F3A)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T5F4)));
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T4F4)));
		
		dependents = changeGenealogy.getDependents(environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.DeletedDefinitionOnDefinition);
		assertEquals(1, dependents.size());
		assertTrue(dependents.contains(environmentOperations.get(TestEnvironmentOperation.T9F1)));
		
		dependents = changeGenealogy.getDependents(environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.DefinitionOnDefinition, GenealogyEdgeType.DefinitionOnDeletedDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition);
		assertEquals(0, dependents.size());
		
		Set<GenealogyEdgeType> existingEdgeTypes = changeGenealogy.getExistingEdgeTypes();
		assertEquals(6, existingEdgeTypes.size());
		
		
		
		int iterCounter = 0;
		int hitCounter = 0;
		Iterator<JavaChangeOperation> iterator = changeGenealogy.vertexIterator();
		while (iterator.hasNext()) {
			JavaChangeOperation tmp = iterator.next();
			assertTrue(tmp != null);
			if (environmentOperations.containsValue(tmp)) {
				++hitCounter;
			}
			++iterCounter;
		}
		assertEquals(41, iterCounter);
		assertEquals(17, hitCounter);
		
		changeGenealogy.close();
	}
}
