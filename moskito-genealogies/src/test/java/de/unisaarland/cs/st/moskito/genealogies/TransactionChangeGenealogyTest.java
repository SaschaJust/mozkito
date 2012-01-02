package de.unisaarland.cs.st.moskito.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionPartitioner;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeGenealogyTest extends TestEnvironment {
	
	@Test
	public void test() {
		TestEnvironment.setup();
		
		changeGenealogy.close();
		TransactionChangeGenealogy tdg = TransactionChangeGenealogy.readFromFile(tmpGraphDBFile, getPersistenceUtil(),
		        new TransactionPartitioner());
		
		assertEquals(16, tdg.edgeSize());
		
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
		
		assertTrue(tdg.containsEdge(environmentTransactions.get(3), environmentTransactions.get(3)));
		edges = tdg.getEdges(environmentTransactions.get(3), environmentTransactions.get(3));
		assertEquals(1, edges.size());
		assertTrue(edges.contains(GenealogyEdgeType.CallOnDefinition));
		
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
		
		Collection<RCSTransaction> dependents = tdg.getAllDependants(environmentTransactions.get(3));
		assertEquals(4, dependents.size());
		assertTrue(dependents.contains(environmentTransactions.get(4)));
		assertTrue(dependents.contains(environmentTransactions.get(5)));
		assertTrue(dependents.contains(environmentTransactions.get(6)));
		assertTrue(dependents.contains(environmentTransactions.get(9)));
		
		dependents = tdg.getDependants(environmentTransactions.get(3), GenealogyEdgeType.CallOnDefinition);
		assertEquals(2, dependents.size());
		assertTrue(dependents.contains(environmentTransactions.get(4)));
		assertTrue(dependents.contains(environmentTransactions.get(5)));
		
		Collection<RCSTransaction> parents = tdg.getAllParents(environmentTransactions.get(10));
		assertEquals(3, parents.size());
		assertTrue(parents.contains(environmentTransactions.get(4)));
		assertTrue(parents.contains(environmentTransactions.get(5)));
		assertTrue(parents.contains(environmentTransactions.get(9)));
		
		parents = tdg.getParents(environmentTransactions.get(10), GenealogyEdgeType.DeletedCallOnCall);
		assertEquals(2, parents.size());
		assertTrue(parents.contains(environmentTransactions.get(4)));
		assertTrue(parents.contains(environmentTransactions.get(5)));
		
		assertEquals(10, tdg.vertexSize());
		Iterator<RCSTransaction> vertexIter = tdg.vertexSet();
		
		Set<RCSTransaction> vertices = new HashSet<RCSTransaction>();
		while (vertexIter.hasNext()) {
			vertices.add(vertexIter.next());
		}
		
		assertTrue(environmentTransactions.values().containsAll(vertices));
		assertTrue(vertices.containsAll(environmentTransactions.values()));
		
		tdg.close();
	}
	
}