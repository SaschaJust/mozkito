package de.unisaarland.cs.st.moskito.genealogies.transaction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.TestEnvironment;


public class TransactionChangeGenealogyLongTest {
	
	@BeforeClass
	public static void beforeClass() {
		TestEnvironment.setup();
	}
	
	@Test
	public void test() {
		
		//		File graphDBFile = FileUtils.createRandomDir("reposuite", "genealogies-test", FileShutdownAction.DELETE);
		//		
		//		PersistenceUtil persistenceUtil = TestEnvironment.getPersistenceUtil();
		//		GenealogyAnalyzer genealogyAnalyzer = new GenealogyAnalyzer();
		//		Criteria<RCSTransaction> transactionCriteria = persistenceUtil.createCriteria(RCSTransaction.class);
		//		List<RCSTransaction> transactions = persistenceUtil.load(transactionCriteria);
		//		TransactionChangeGenealogy genealogy = new TransactionChangeGenealogy(graphDBFile, persistenceUtil,
		//				genealogyAnalyzer);
		//		
		//		assertTrue(genealogy != null);
		//		
		//		genealogy.addTransactions(transactions);
		//		
		//		assertEquals(10, genealogy.vertexSize());
		//		
		//		Map<RCSTransaction, GenealogyVertex> t2v = new HashMap<RCSTransaction, GenealogyVertex>();
		//		for (RCSTransaction t : TestEnvironment.transactionMap.keySet()) {
		//			GenealogyVertex v = genealogy.getVertex(t);
		//			assertTrue(genealogy.containsVertex(v));
		//			assert (v != null);
		//			assertEquals(t, genealogy.getTransactionForVertex(v));
		//			Collection<JavaChangeOperation> ops = genealogy.getJavaChangeOperationsForVertex(v);
		//			assertEquals(TestEnvironment.transactionMap.get(t), ops);
		//			t2v.put(t, v);
		//		}
		//		
		//		int vertexSetCounter = 0;
		//		for (GenealogyVertex v : genealogy.vertexSet()) {
		//			assertTrue(t2v.values().contains(v));
		//			++vertexSetCounter;
		//		}
		//		assertEquals(10, vertexSetCounter);
		//		
		//		GenealogyVertex v1 = t2v.get(TestEnvironment.environmentTransactions.get(1));
		//		GenealogyVertex v2 = t2v.get(TestEnvironment.environmentTransactions.get(2));
		//		GenealogyVertex v3 = t2v.get(TestEnvironment.environmentTransactions.get(3));
		//		GenealogyVertex v4 = t2v.get(TestEnvironment.environmentTransactions.get(4));
		//		GenealogyVertex v5 = t2v.get(TestEnvironment.environmentTransactions.get(5));
		//		GenealogyVertex v6 = t2v.get(TestEnvironment.environmentTransactions.get(6));
		//		GenealogyVertex v7 = t2v.get(TestEnvironment.environmentTransactions.get(7));
		//		GenealogyVertex v8 = t2v.get(TestEnvironment.environmentTransactions.get(8));
		//		GenealogyVertex v9 = t2v.get(TestEnvironment.environmentTransactions.get(9));
		//		GenealogyVertex v10 = t2v.get(TestEnvironment.environmentTransactions.get(10));
		//		
		//		assertTrue(genealogy.containsEdge(v2, v1));
		//		assertFalse(genealogy.containsEdge(v2, v2));
		//		assertFalse(genealogy.containsEdge(v2, v3));
		//		assertFalse(genealogy.containsEdge(v2, v4));
		//		assertFalse(genealogy.containsEdge(v2, v5));
		//		assertFalse(genealogy.containsEdge(v2, v6));
		//		assertFalse(genealogy.containsEdge(v2, v7));
		//		assertFalse(genealogy.containsEdge(v2, v8));
		//		assertFalse(genealogy.containsEdge(v2, v9));
		//		assertFalse(genealogy.containsEdge(v2, v10));
		//		
		//		assertTrue(genealogy.containsEdge(v3, v1));
		//		assertFalse(genealogy.containsEdge(v3, v2));
		//		assertTrue(genealogy.containsEdge(v3, v3));
		//		assertFalse(genealogy.containsEdge(v3, v4));
		//		assertFalse(genealogy.containsEdge(v3, v5));
		//		assertFalse(genealogy.containsEdge(v3, v6));
		//		assertFalse(genealogy.containsEdge(v3, v7));
		//		assertFalse(genealogy.containsEdge(v3, v8));
		//		assertFalse(genealogy.containsEdge(v3, v9));
		//		assertFalse(genealogy.containsEdge(v3, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v4, v1));
		//		assertTrue(genealogy.containsEdge(v4, v2));
		//		assertTrue(genealogy.containsEdge(v4, v3));
		//		assertFalse(genealogy.containsEdge(v4, v4));
		//		assertFalse(genealogy.containsEdge(v4, v5));
		//		assertFalse(genealogy.containsEdge(v4, v6));
		//		assertFalse(genealogy.containsEdge(v4, v7));
		//		assertFalse(genealogy.containsEdge(v4, v8));
		//		assertFalse(genealogy.containsEdge(v4, v9));
		//		assertFalse(genealogy.containsEdge(v4, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v5, v1));
		//		assertFalse(genealogy.containsEdge(v5, v2));
		//		assertTrue(genealogy.containsEdge(v5, v3));
		//		//		assertFalse(genealogy.containsEdge(v5, v4));
		//		assertFalse(genealogy.containsEdge(v5, v5));
		//		assertFalse(genealogy.containsEdge(v5, v6));
		//		assertFalse(genealogy.containsEdge(v5, v7));
		//		assertFalse(genealogy.containsEdge(v5, v8));
		//		assertFalse(genealogy.containsEdge(v5, v9));
		//		assertFalse(genealogy.containsEdge(v5, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v6, v1));
		//		assertFalse(genealogy.containsEdge(v6, v2));
		//		assertTrue(genealogy.containsEdge(v6, v3));
		//		assertFalse(genealogy.containsEdge(v6, v4));
		//		assertFalse(genealogy.containsEdge(v6, v5));
		//		assertFalse(genealogy.containsEdge(v6, v6));
		//		assertFalse(genealogy.containsEdge(v6, v7));
		//		assertFalse(genealogy.containsEdge(v6, v8));
		//		assertFalse(genealogy.containsEdge(v6, v9));
		//		assertFalse(genealogy.containsEdge(v6, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v7, v1));
		//		assertFalse(genealogy.containsEdge(v7, v2));
		//		//		assertFalse(genealogy.containsEdge(v7, v3));
		//		assertFalse(genealogy.containsEdge(v7, v4));
		//		assertFalse(genealogy.containsEdge(v7, v5));
		//		assertTrue(genealogy.containsEdge(v7, v6));
		//		assertFalse(genealogy.containsEdge(v7, v7));
		//		assertFalse(genealogy.containsEdge(v7, v8));
		//		assertFalse(genealogy.containsEdge(v7, v9));
		//		assertFalse(genealogy.containsEdge(v7, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v8, v1));
		//		assertFalse(genealogy.containsEdge(v8, v2));
		//		//		assertFalse(genealogy.containsEdge(v8, v3));
		//		assertFalse(genealogy.containsEdge(v8, v4));
		//		assertFalse(genealogy.containsEdge(v8, v5));
		//		assertFalse(genealogy.containsEdge(v8, v6));
		//		assertTrue(genealogy.containsEdge(v8, v7));
		//		assertFalse(genealogy.containsEdge(v8, v8));
		//		assertFalse(genealogy.containsEdge(v8, v9));
		//		assertFalse(genealogy.containsEdge(v8, v10));
		//		
		//		assertFalse(genealogy.containsEdge(v9, v1));
		//		assertFalse(genealogy.containsEdge(v9, v2));
		//		assertTrue(genealogy.containsEdge(v9, v3));
		//		assertFalse(genealogy.containsEdge(v9, v4));
		//		assertFalse(genealogy.containsEdge(v9, v5));
		//		assertFalse(genealogy.containsEdge(v9, v6));
		//		assertFalse(genealogy.containsEdge(v9, v7));
		//		assertFalse(genealogy.containsEdge(v9, v8));
		//		assertFalse(genealogy.containsEdge(v9, v9));
		//		assertFalse(genealogy.containsEdge(v9, v10));
		//		
		//		assertTrue(genealogy.getEdges(v2, v1).contains(GenealogyEdgeType.CallOnDefinition));
		//		assertTrue(genealogy.getEdges(v3, v1).contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		//		assertTrue(genealogy.getEdges(v3, v3).contains(GenealogyEdgeType.CallOnDefinition));
		//		assertTrue(genealogy.getEdges(v4, v2).contains(GenealogyEdgeType.DeletedCallOnCall));
		//		assertTrue(genealogy.getEdges(v4, v3).contains(GenealogyEdgeType.CallOnDefinition));
		//		assertTrue(genealogy.getEdges(v5, v3).contains(GenealogyEdgeType.CallOnDefinition));
		//		assertTrue(genealogy.getEdges(v6, v3).contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		//		assertTrue(genealogy.getEdges(v7, v6).contains(GenealogyEdgeType.DefinitionOnDeletedDefinition));
		//		assertTrue(genealogy.getEdges(v8, v7).contains(GenealogyEdgeType.DefinitionOnDefinition));
		//		assertTrue(genealogy.getEdges(v9, v3).contains(GenealogyEdgeType.DeletedDefinitionOnDefinition));
		//		
		//		genealogy.close();
	}
	
}
