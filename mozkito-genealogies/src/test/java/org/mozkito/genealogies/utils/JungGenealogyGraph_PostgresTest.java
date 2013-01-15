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
package org.mozkito.genealogies.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;

import org.junit.Test;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.utils.JungGenealogyGraph.Edge;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class JungGenealogyGraph_PostgresTest.
 */
@DatabaseSettings (unit = "codeanalysis",
                   database = "mozkito_genealogies_test_environment",
                   options = ConnectOptions.VALIDATE_OR_CREATE_SCHEMA,
                   hostname = "grid1.st.cs.uni-saarland.de",
                   password = "miner",
                   username = "miner",
                   type = DatabaseType.POSTGRESQL,
                   remote = true)
public class JungGenealogyGraph_PostgresTest extends DatabaseTest {
	
	/**
	 * Test core layer.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	@Test
	public void testCoreLayer() throws IOException, FilePermissionException {
		final File tmpGraphDBFile = FileUtils.createRandomDir(this.getClass().getSimpleName(), "",
		                                                      FileShutdownAction.KEEP);
		
		final GenealogyTestEnvironment testEnvironment = ChangeGenealogyUtils.getGenealogyTestEnvironment(tmpGraphDBFile,
		                                                                                                  getPersistenceUtil());
		final CoreChangeGenealogy changeGenealogy = testEnvironment.getChangeGenealogy();
		
		final JungGenealogyGraph<JavaChangeOperation> jungGraph = new JungGenealogyGraph<JavaChangeOperation>(
		                                                                                                      changeGenealogy);
		assertEquals(changeGenealogy.vertexSize(), jungGraph.getVertexCount());
		for (final JavaChangeOperation op : changeGenealogy.vertexSet()) {
			assertTrue(jungGraph.containsVertex(op));
			final int inDegree = changeGenealogy.inDegree(op);
			final int outDegree = changeGenealogy.outDegree(op);
			
			assertEquals(inDegree, jungGraph.inDegree(op));
			assertEquals(outDegree, jungGraph.outDegree(op));
			assertEquals(inDegree + outDegree, jungGraph.degree(op));
			
			final Collection<JavaChangeOperation> allDependants = changeGenealogy.getAllDependants(op);
			final Collection<JavaChangeOperation> allParents = changeGenealogy.getAllParents(op);
			
			assertTrue(jungGraph.getIncidentEdges(op).size() >= (allDependants.size() + allParents.size()));
			final Collection<Edge<JavaChangeOperation>> jungInEdges = jungGraph.getInEdges(op);
			assertEquals(inDegree, jungInEdges.size());
			for (final Edge<JavaChangeOperation> edge : jungInEdges) {
				assertEquals(op, edge.to);
				assertEquals(op, jungGraph.getDest(edge));
				assertTrue(allDependants.contains(edge.from));
			}
			
			assertEquals(allDependants.size() + allParents.size(), jungGraph.getNeighborCount(op));
			
			final Collection<JavaChangeOperation> jungNeighbors = jungGraph.getNeighbors(op);
			assertEquals(allDependants.size() + allParents.size(), jungNeighbors.size());
			for (final JavaChangeOperation neighbor : jungNeighbors) {
				assertTrue(allDependants.contains(neighbor) || allParents.contains(neighbor));
			}
			
			final Collection<Edge<JavaChangeOperation>> jungOutEdges = jungGraph.getOutEdges(op);
			assertEquals(op.toString(), outDegree, jungOutEdges.size());
			for (final Edge<JavaChangeOperation> edge : jungOutEdges) {
				assertEquals(op, edge.from);
				assertEquals(op, jungGraph.getSource(edge));
				assertTrue(allParents.contains(edge.to));
			}
			
			assertEquals(outDegree, jungGraph.getPredecessorCount(op));
			
			assertEquals(allParents, jungGraph.getPredecessors(op));
			
			assertEquals(inDegree, jungGraph.getSuccessorCount(op));
			assertEquals(allDependants, jungGraph.getSuccessors(op));
			
			for (final JavaChangeOperation parent : allParents) {
				
				final Collection<GenealogyEdgeType> edgeTypes = changeGenealogy.getEdges(op, parent);
				for (final GenealogyEdgeType eType : edgeTypes) {
					final Edge<JavaChangeOperation> edge = new Edge<JavaChangeOperation>(op, parent, eType);
					assertTrue(jungGraph.containsEdge(edge));
				}
				assertTrue(edgeTypes.contains(jungGraph.findEdge(op, parent).type));
				final Collection<Edge<JavaChangeOperation>> jungEdgeSet = jungGraph.findEdgeSet(op, parent);
				assertEquals(edgeTypes.size(), jungEdgeSet.size());
				for (final Edge<JavaChangeOperation> jungEdge : jungEdgeSet) {
					assertTrue(edgeTypes.contains(jungEdge.type));
				}
			}
			
		}
		changeGenealogy.close();
		try {
			FileUtils.deleteDirectory(tmpGraphDBFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
}
