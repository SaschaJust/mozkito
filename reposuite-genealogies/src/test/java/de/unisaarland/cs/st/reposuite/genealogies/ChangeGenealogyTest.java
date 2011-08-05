package de.unisaarland.cs.st.reposuite.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;

public class ChangeGenealogyTest {
	
	@Test
	public void testAddVertex() {
		File testDir = FileUtils.createRandomDir("generalogy", "test", FileShutdownAction.DELETE);
		System.out.println("test directory: " + testDir.getAbsolutePath());
		Graph graph = new Neo4jGraph(testDir.getAbsolutePath());
		ChangeGenealogy cg = new ChangeGenealogy(graph);
		cg.addVertex("1", new Long[] { 0l, 1l, 2l, 3l, 4l, 5l });
		cg.addVertex("2", new Long[] { 0l, 6l, 7l });
		//		cg.close();
		//
		//		graph = new Neo4jGraph(testDir.getAbsolutePath());
		
		int vertexCount = 0;
		
		for (Vertex vertex : graph.getVertices()) {
			if (vertex.getProperty("transactionId") != null) {
				String transactionId = (String) vertex.getProperty("transactionId");
				assertTrue(transactionId.equals("1") || transactionId.equals("2"));
			}
			++vertexCount;
		}
		assertEquals("The graph DB contains too less or too many vertices!", 11, vertexCount);
		
		int edgeCount = 0;
		for (Edge edge : graph.getEdges()) {
			Vertex inVertex = edge.getInVertex();
			Vertex outVertex = edge.getOutVertex();
			
			assertTrue("Found edge whose inVertex is NULL.", inVertex != null);
			assertTrue("Found edge whose outVertex is NULL.", outVertex != null);
			
			Long inId = (Long) inVertex.getProperty("javachangeoperationId");
			String transactionId = (String) outVertex.getProperty("transactionId");
			
			if (inId == 0l) {
				assertTrue("Wrong outVertex for JavaChangeOperation 0: " + transactionId + " (expected '1' oder '2'",
						transactionId.equals("1") || transactionId.equals("2"));
			} else if (inId == 1l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "1", transactionId);
			} else if (inId == 2l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "1", transactionId);
			} else if (inId == 3l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "1", transactionId);
			} else if (inId == 4l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "1", transactionId);
			} else if (inId == 5l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "1", transactionId);
			} else if (inId == 6l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "2", transactionId);
			} else if (inId == 7l) {
				assertEquals("Wrong outVertex for JavaChangeOperation " + inId + ".", "2", transactionId);
			} else {
				fail("Unknown java change operation id detected: " + inId);
			}
			++edgeCount;
		}
		
		assertEquals("The graph DB contains too less edges!", 9, edgeCount);
		
		graph.shutdown();
		try {
			FileUtils.deleteDirectory(testDir);
		} catch (IOException e) {
		}
	}
	
}
