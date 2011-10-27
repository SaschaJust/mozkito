package de.unisaarland.cs.st.reposuite.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.reposuite.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public class ChangeGenealogyLongTest extends TestEnvironment {
	
	@BeforeClass
	public static void beforeClass() {
		TestEnvironment.setup();
	}
	
	
	@Test
	public void testChangeGenealogy() {
		
		Map<String, GenealogyVertex> transactions2Vertices = new HashMap<String, GenealogyVertex>();
		
		File tmpGraphDBFile = FileUtils
				.createRandomDir("reposuite", "change_genealogy_test", FileShutdownAction.DELETE);
		
		CoreChangeGenealogy changeGenealogy = CoreChangeGenealogy.readFromDB(tmpGraphDBFile);
		changeGenealogy.setPersistenceUtil(getPersistenceUtil());
		assertTrue(changeGenealogy != null);
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			changeGenealogy.addVertex(transactionEntry.getValue());
		}
		
		assertEquals(10, changeGenealogy.vertexSize());
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			Collection<Long> operationIds = new HashSet<Long>();
			for (JavaChangeOperation op : transactionEntry.getValue()) {
				operationIds.add(op.getId());
			}
			GenealogyVertex vertex = changeGenealogy.getVertex(transactionEntry.getKey().getId(), operationIds);
			
			transactions2Vertices.put(transactionEntry.getKey().getId(), vertex);
			
			assert (vertex != null);
			assertEquals(transactionEntry.getKey().getId(), vertex.getTransactionId());
			assertEquals(transactionEntry.getKey(), changeGenealogy.getTransactionForVertex(vertex));
			assertEquals(transactionEntry.getValue(), changeGenealogy.getJavaChangeOperationsForVertex(vertex));
		}
		
		boolean success = changeGenealogy.addEdge(
				transactions2Vertices.get("a10344533c2b442235aa3bf3dc87dd0ac37cb0af"),
				transactions2Vertices.get("a64df287a21f8a7b0690d13c1561171cbf48a0e1"),
				GenealogyEdgeType.DeletedDefinitionOnDefinition);
		assertEquals(true, success);
		
		Collection<GenealogyVertex> allDependents = transactions2Vertices.get(
				"a64df287a21f8a7b0690d13c1561171cbf48a0e1").getAllDependents();
		assertEquals(1, allDependents.size());
		Collection<GenealogyVertex> allVerticesDependingOn = transactions2Vertices.get(
				"a64df287a21f8a7b0690d13c1561171cbf48a0e1").getAllVerticesDependingOn();
		assertEquals(0, allVerticesDependingOn.size());
		
		Collection<GenealogyVertex> dependants = transactions2Vertices.get("a64df287a21f8a7b0690d13c1561171cbf48a0e1")
				.getDependents(GenealogyEdgeType.DeletedDefinitionOnDefinition);
		assertEquals(1, dependants.size());
		
		dependants = transactions2Vertices.get("a64df287a21f8a7b0690d13c1561171cbf48a0e1").getDependents(
				GenealogyEdgeType.DefinitionOnDefinition, GenealogyEdgeType.DefinitionOnDeletedDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition,
				GenealogyEdgeType.CallOnDefinition);
		assertEquals(0, dependants.size());
		
		dependants = transactions2Vertices.get("a64df287a21f8a7b0690d13c1561171cbf48a0e1").getVerticesDependingOn(
				GenealogyEdgeType.DeletedDefinitionOnDefinition);
		assertEquals(0, dependants.size());
		
		dependants = transactions2Vertices.get("a64df287a21f8a7b0690d13c1561171cbf48a0e1").getVerticesDependingOn(
				GenealogyEdgeType.DefinitionOnDefinition, GenealogyEdgeType.DefinitionOnDeletedDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition,
				GenealogyEdgeType.CallOnDefinition);
		assertEquals(0, dependants.size());
	}
}
