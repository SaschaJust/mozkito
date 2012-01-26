package de.unisaarland.cs.st.moskito.genealogies.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.utils.JungGenealogyGraph.Edge;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class JungGenealogyGraphTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings(unit = "ppa")
	public void testCoreLayer() {
		File tmpGraphDBFile = FileUtils.createRandomDir(this.getClass().getSimpleName(), "", FileShutdownAction.KEEP);
		
		GenealogyTestEnvironment testEnvironment = ChangeGenealogyUtils.getGenealogyTestEnvironment(tmpGraphDBFile,
		        getPersistenceUtil());
		CoreChangeGenealogy changeGenealogy = testEnvironment.getChangeGenealogy();
		
		JungGenealogyGraph<JavaChangeOperation> jungGraph = new JungGenealogyGraph<JavaChangeOperation>(changeGenealogy);
		assertEquals(changeGenealogy.vertexSize(), jungGraph.getVertexCount());
		for (JavaChangeOperation op : changeGenealogy.vertexSet()) {
			assertTrue(jungGraph.containsVertex(op));
			int inDegree = changeGenealogy.inDegree(op);
			int outDegree = changeGenealogy.outDegree(op);
			
			assertEquals(inDegree, jungGraph.inDegree(op));
			assertEquals(outDegree, jungGraph.outDegree(op));
			assertEquals(inDegree + outDegree, jungGraph.degree(op));
			
			Collection<JavaChangeOperation> allDependants = changeGenealogy.getAllDependants(op);
			Collection<JavaChangeOperation> allParents = changeGenealogy.getAllParents(op);
			
			assertTrue(jungGraph.getIncidentEdges(op).size() >= (allDependants.size() + allParents.size()));
			Collection<Edge<JavaChangeOperation>> jungInEdges = jungGraph.getInEdges(op);
			assertEquals(inDegree, jungInEdges.size());
			for (Edge<JavaChangeOperation> edge : jungInEdges) {
				assertEquals(op, edge.to);
				assertEquals(op, jungGraph.getDest(edge));
				assertTrue(allDependants.contains(edge.from));
			}
			
			assertEquals(allDependants.size() + allParents.size(), jungGraph.getNeighborCount(op));
			
			Collection<JavaChangeOperation> jungNeighbors = jungGraph.getNeighbors(op);
			assertEquals(allDependants.size() + allParents.size(), jungNeighbors.size());
			for (JavaChangeOperation neighbor : jungNeighbors) {
				assertTrue(allDependants.contains(neighbor) || allParents.contains(neighbor));
			}
			
			Collection<Edge<JavaChangeOperation>> jungOutEdges = jungGraph.getOutEdges(op);
			assertEquals(op.toString(), outDegree, jungOutEdges.size());
			for (Edge<JavaChangeOperation> edge : jungOutEdges) {
				assertEquals(op, edge.from);
				assertEquals(op, jungGraph.getSource(edge));
				assertTrue(allParents.contains(edge.to));
			}
			
			assertEquals(outDegree, jungGraph.getPredecessorCount(op));
			
			assertEquals(allParents, jungGraph.getPredecessors(op));
			
			assertEquals(inDegree, jungGraph.getSuccessorCount(op));
			assertEquals(allDependants, jungGraph.getSuccessors(op));
			
			for (JavaChangeOperation parent : allParents) {
				
				Collection<GenealogyEdgeType> edgeTypes = changeGenealogy.getEdges(op, parent);
				for (GenealogyEdgeType eType : edgeTypes) {
					assertTrue(jungGraph.containsEdge(new Edge<JavaChangeOperation>(op, parent, eType)));
				}
				assertTrue(edgeTypes.contains(jungGraph.findEdge(op, parent).type));
				Collection<Edge<JavaChangeOperation>> jungEdgeSet = jungGraph.findEdgeSet(op, parent);
				assertEquals(edgeTypes.size(), jungEdgeSet.size());
				for (Edge<JavaChangeOperation> jungEdge : jungEdgeSet) {
					assertTrue(edgeTypes.contains(jungEdge.type));
				}
			}
			
		}
		changeGenealogy.close();
		try {
			FileUtils.deleteDirectory(tmpGraphDBFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
