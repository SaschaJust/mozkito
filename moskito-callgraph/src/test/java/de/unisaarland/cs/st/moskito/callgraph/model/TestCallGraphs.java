/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.callgraph.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.callgraph.model.CallGraph;
import de.unisaarland.cs.st.moskito.callgraph.model.CallGraphEdge;
import de.unisaarland.cs.st.moskito.callgraph.model.ClassVertex;
import de.unisaarland.cs.st.moskito.callgraph.model.MethodVertex;
import de.unisaarland.cs.st.moskito.callgraph.model.VertexFactory;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * The Class TestCallGraphs.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TestCallGraphs {
	
	/** The A. */
	private ClassVertex  A;
	
	/** The Aa. */
	private MethodVertex Aa;
	
	/** The Ab. */
	private MethodVertex Ab;
	
	/** The Ac. */
	private MethodVertex Ac;
	
	/** The Ad. */
	private MethodVertex Ad;
	
	/** The B. */
	private ClassVertex  B;
	
	/** The Ba. */
	private MethodVertex Ba;
	
	/** The Bb. */
	private MethodVertex Bb;
	
	/** The C. */
	private ClassVertex  C;
	
	/** The Ca. */
	private MethodVertex Ca;
	
	/** The Cb. */
	private MethodVertex Cb;
	
	/** The cg. */
	private CallGraph    cg;
	
	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		this.Aa = VertexFactory.createMethodVertex("A.a(bool,bool)", "A.java");
		this.Ab = VertexFactory.createMethodVertex("A.b(String)", "A.java");
		this.Ac = VertexFactory.createMethodVertex("A.b(int)", "A.java");
		this.Ad = VertexFactory.createMethodVertex("A.d()", "A.java");
		
		this.Ba = VertexFactory.createMethodVertex("B.a()", "B.java");
		this.Bb = VertexFactory.createMethodVertex("B.b()", "B.java");
		
		this.Ca = VertexFactory.createMethodVertex("C.a(int)", "C.java");
		this.Cb = VertexFactory.createMethodVertex("C.b(double)", "C.java");
		
		this.A = this.Aa.getParent();
		this.B = this.Ba.getParent();
		this.C = this.Ca.getParent();
		
		this.cg = new CallGraph();
		this.cg.addEdge(this.Aa, this.Ba);
		this.cg.addEdge(this.Aa, this.Ba);
		this.cg.addEdge(this.Aa, this.Bb);
		this.cg.addEdge(this.Ab, this.Ac);
		this.cg.addEdge(this.Ac, this.Bb);
		this.cg.addEdge(this.Ac, this.Ab);
		this.cg.addEdge(this.Ad, this.Ad);
		this.cg.addEdge(this.Ad, this.Cb);
		
		this.cg.addEdge(this.Ba, this.Ca);
		this.cg.addEdge(this.Ba, this.Ad);
		this.cg.addEdge(this.Bb, this.Ca);
		
		this.cg.addEdge(this.Ca, this.Cb);
		
	}
	
	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		VertexFactory.clear();
	}
	
	/**
	 * Test graph generation.
	 */
	@Test
	public void testGraphGeneration() {
		
		assertEquals(8, this.cg.getVertices().size());
		assertEquals(11, this.cg.getEdges().size());
		
		assertNull(this.cg.findEdge(this.Aa, this.Aa));
		assertNull(this.cg.findEdge(this.Aa, this.Ab));
		assertNull(this.cg.findEdge(this.Aa, this.Ac));
		assertNull(this.cg.findEdge(this.Aa, this.Ad));
		assertNotNull(this.cg.findEdge(this.Aa, this.Ba));
		assertEquals(0.5, this.cg.findEdge(this.Aa, this.Ba).getWeight(), 0.0);
		
		assertNotNull(this.cg.findEdge(this.Aa, this.Bb));
		assertEquals(1.0, this.cg.findEdge(this.Aa, this.Bb).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Aa, this.Ca));
		assertNull(this.cg.findEdge(this.Aa, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ab, this.Aa));
		assertNull(this.cg.findEdge(this.Ab, this.Ab));
		assertNotNull(this.cg.findEdge(this.Ab, this.Ac));
		assertEquals(1.0, this.cg.findEdge(this.Ab, this.Ac).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ab, this.Ad));
		assertNull(this.cg.findEdge(this.Ab, this.Ba));
		assertNull(this.cg.findEdge(this.Ab, this.Bb));
		assertNull(this.cg.findEdge(this.Ab, this.Ca));
		assertNull(this.cg.findEdge(this.Ab, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ac, this.Aa));
		assertNotNull(this.cg.findEdge(this.Ac, this.Ab));
		assertEquals(1.0, this.cg.findEdge(this.Ac, this.Ab).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ac, this.Ac));
		assertNull(this.cg.findEdge(this.Ac, this.Ad));
		assertNull(this.cg.findEdge(this.Ac, this.Ba));
		assertNotNull(this.cg.findEdge(this.Ac, this.Bb));
		assertEquals(1.0, this.cg.findEdge(this.Ac, this.Bb).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ac, this.Ca));
		assertNull(this.cg.findEdge(this.Ac, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ad, this.Aa));
		assertNull(this.cg.findEdge(this.Ad, this.Ab));
		assertNull(this.cg.findEdge(this.Ad, this.Ac));
		assertNotNull(this.cg.findEdge(this.Ad, this.Ad));
		assertEquals(1.0, this.cg.findEdge(this.Ad, this.Ad).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ad, this.Ba));
		assertNull(this.cg.findEdge(this.Ad, this.Bb));
		assertNull(this.cg.findEdge(this.Ad, this.Ca));
		assertNotNull(this.cg.findEdge(this.Ad, this.Cb));
		assertEquals(1.0, this.cg.findEdge(this.Ad, this.Cb).getWeight(), 0.0);
		
		assertNull(this.cg.findEdge(this.Ba, this.Aa));
		assertNull(this.cg.findEdge(this.Ba, this.Ab));
		assertNull(this.cg.findEdge(this.Ba, this.Ac));
		assertNotNull(this.cg.findEdge(this.Ba, this.Ad));
		assertEquals(1.0, this.cg.findEdge(this.Ba, this.Ad).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ba, this.Ba));
		assertNull(this.cg.findEdge(this.Ba, this.Bb));
		assertNotNull(this.cg.findEdge(this.Ba, this.Ca));
		assertEquals(1.0, this.cg.findEdge(this.Ba, this.Ca).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Ba, this.Cb));
		
		assertNull(this.cg.findEdge(this.Bb, this.Aa));
		assertNull(this.cg.findEdge(this.Bb, this.Ab));
		assertNull(this.cg.findEdge(this.Bb, this.Ac));
		assertNull(this.cg.findEdge(this.Bb, this.Ad));
		assertNull(this.cg.findEdge(this.Bb, this.Ba));
		assertNull(this.cg.findEdge(this.Bb, this.Bb));
		assertNotNull(this.cg.findEdge(this.Bb, this.Ca));
		assertEquals(1.0, this.cg.findEdge(this.Bb, this.Ca).getWeight(), 0.0);
		assertNull(this.cg.findEdge(this.Bb, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ca, this.Aa));
		assertNull(this.cg.findEdge(this.Ca, this.Ab));
		assertNull(this.cg.findEdge(this.Ca, this.Ac));
		assertNull(this.cg.findEdge(this.Ca, this.Ad));
		assertNull(this.cg.findEdge(this.Ca, this.Ba));
		assertNull(this.cg.findEdge(this.Ca, this.Bb));
		assertNull(this.cg.findEdge(this.Ca, this.Ca));
		assertNotNull(this.cg.findEdge(this.Ca, this.Cb));
		assertEquals(1.0, this.cg.findEdge(this.Ca, this.Cb).getWeight(), 0.0);
		
		assertNull(this.cg.findEdge(this.Cb, this.Aa));
		assertNull(this.cg.findEdge(this.Cb, this.Ab));
		assertNull(this.cg.findEdge(this.Cb, this.Ac));
		assertNull(this.cg.findEdge(this.Cb, this.Ad));
		assertNull(this.cg.findEdge(this.Cb, this.Ba));
		assertNull(this.cg.findEdge(this.Cb, this.Bb));
		assertNull(this.cg.findEdge(this.Cb, this.Ca));
		assertNull(this.cg.findEdge(this.Cb, this.Cb));
		
		DirectedSparseGraph<ClassVertex, CallGraphEdge> ccg = this.cg.getClassCallGraph();
		
		assertEquals(3, ccg.getVertices().size());
		assertEquals(6, ccg.getEdges().size());
		
		assertNotNull(ccg.findEdge(this.A, this.A));
		assertEquals((1.0 / 3.0), ccg.findEdge(this.A, this.A).getWeight(), 0.0);
		assertNotNull(ccg.findEdge(this.A, this.B));
		assertEquals((1.0 / 4.0), ccg.findEdge(this.A, this.B).getWeight(), 0.0);
		assertNotNull(ccg.findEdge(this.A, this.C));
		assertEquals(1, ccg.findEdge(this.A, this.C).getWeight(), 0.0);
		assertNotNull(ccg.findEdge(this.B, this.A));
		assertEquals(1.0, ccg.findEdge(this.B, this.A).getWeight(), 0.0);
		assertNull(ccg.findEdge(this.B, this.B));
		assertNotNull(ccg.findEdge(this.B, this.C));
		assertEquals((1.0 / 2.0), ccg.findEdge(this.B, this.C).getWeight(), 0.0);
		assertNull(ccg.findEdge(this.C, this.A));
		assertNull(ccg.findEdge(this.C, this.B));
		assertNotNull(ccg.findEdge(this.C, this.C));
		assertEquals(1.0, ccg.findEdge(this.C, this.C).getWeight(), 0.0);
		
		this.cg.removeEdge(this.Aa, this.Ba);
		
		assertEquals(1d, this.cg.findEdge(this.Aa, this.Ba).getWeight(), 0.0);
		assertEquals((1d / 3d), ccg.findEdge(this.A, this.B).getWeight(), 0.0);
		
		this.cg.removeEdge(this.Aa, this.Ba);
		assertNull(this.cg.findEdge(this.Aa, this.Ba));
		assertEquals((1d / 2d), ccg.findEdge(this.A, this.B).getWeight(), 0.0);
		
		assertNotNull(ccg.findEdge(this.A, this.B));
		assertEquals((1.0 / 2.0), ccg.findEdge(this.A, this.B).getWeight(), 0.0);
		
	}
	
	/**
	 * Test serialize.
	 */
	@Test
	public void testSerialize() {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		this.cg.serialize(file);
		CallGraph scg = CallGraph.unserialize(file);
		assertEquals(this.cg, scg);
		file.delete();
	}
	
	/**
	 * Test update graph.
	 */
	@Test
	public void testUpdateGraph() {
		this.cg.removeRecursive(this.B);
		assertEquals(8, this.cg.getVertices().size());
		assertEquals(3, this.cg.getClassCallGraph().getVertices().size());
		
		assertNull(this.cg.findEdge(this.Aa, this.Aa));
		assertNull(this.cg.findEdge(this.Aa, this.Ab));
		assertNull(this.cg.findEdge(this.Aa, this.Ac));
		assertNull(this.cg.findEdge(this.Aa, this.Ad));
		assertNotNull(this.cg.findEdge(this.Aa, this.Ba));
		assertNotNull(this.cg.findEdge(this.Aa, this.Bb));
		assertNull(this.cg.findEdge(this.Aa, this.Ca));
		assertNull(this.cg.findEdge(this.Aa, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ab, this.Aa));
		assertNull(this.cg.findEdge(this.Ab, this.Ab));
		assertNotNull(this.cg.findEdge(this.Ab, this.Ac));
		assertNull(this.cg.findEdge(this.Ab, this.Ad));
		assertNull(this.cg.findEdge(this.Ab, this.Ba));
		assertNull(this.cg.findEdge(this.Ab, this.Bb));
		assertNull(this.cg.findEdge(this.Ab, this.Ca));
		assertNull(this.cg.findEdge(this.Ab, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ac, this.Aa));
		assertNotNull(this.cg.findEdge(this.Ac, this.Ab));
		assertNull(this.cg.findEdge(this.Ac, this.Ac));
		assertNull(this.cg.findEdge(this.Ac, this.Ad));
		assertNull(this.cg.findEdge(this.Ac, this.Ba));
		assertNotNull(this.cg.findEdge(this.Ac, this.Bb));
		assertNull(this.cg.findEdge(this.Ac, this.Ca));
		assertNull(this.cg.findEdge(this.Ac, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ad, this.Aa));
		assertNull(this.cg.findEdge(this.Ad, this.Ab));
		assertNull(this.cg.findEdge(this.Ad, this.Ac));
		assertNotNull(this.cg.findEdge(this.Ad, this.Ad));
		assertNull(this.cg.findEdge(this.Ad, this.Ba));
		assertNull(this.cg.findEdge(this.Ad, this.Bb));
		assertNull(this.cg.findEdge(this.Ad, this.Ca));
		assertNotNull(this.cg.findEdge(this.Ad, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ba, this.Aa));
		assertNull(this.cg.findEdge(this.Ba, this.Ab));
		assertNull(this.cg.findEdge(this.Ba, this.Ac));
		assertNull(this.cg.findEdge(this.Ba, this.Ad));
		assertNull(this.cg.findEdge(this.Ba, this.Ba));
		assertNull(this.cg.findEdge(this.Ba, this.Bb));
		assertNull(this.cg.findEdge(this.Ba, this.Ca));
		assertNull(this.cg.findEdge(this.Ba, this.Cb));
		
		assertNull(this.cg.findEdge(this.Bb, this.Aa));
		assertNull(this.cg.findEdge(this.Bb, this.Ab));
		assertNull(this.cg.findEdge(this.Bb, this.Ac));
		assertNull(this.cg.findEdge(this.Bb, this.Ad));
		assertNull(this.cg.findEdge(this.Bb, this.Ba));
		assertNull(this.cg.findEdge(this.Bb, this.Bb));
		assertNull(this.cg.findEdge(this.Bb, this.Ca));
		assertNull(this.cg.findEdge(this.Bb, this.Cb));
		
		assertNull(this.cg.findEdge(this.Ca, this.Aa));
		assertNull(this.cg.findEdge(this.Ca, this.Ab));
		assertNull(this.cg.findEdge(this.Ca, this.Ac));
		assertNull(this.cg.findEdge(this.Ca, this.Ad));
		assertNull(this.cg.findEdge(this.Ca, this.Ba));
		assertNull(this.cg.findEdge(this.Ca, this.Bb));
		assertNull(this.cg.findEdge(this.Ca, this.Ca));
		assertNotNull(this.cg.findEdge(this.Ca, this.Cb));
		
		assertNull(this.cg.findEdge(this.Cb, this.Aa));
		assertNull(this.cg.findEdge(this.Cb, this.Ab));
		assertNull(this.cg.findEdge(this.Cb, this.Ac));
		assertNull(this.cg.findEdge(this.Cb, this.Ad));
		assertNull(this.cg.findEdge(this.Cb, this.Ba));
		assertNull(this.cg.findEdge(this.Cb, this.Bb));
		assertNull(this.cg.findEdge(this.Cb, this.Ca));
		assertNull(this.cg.findEdge(this.Cb, this.Cb));
		
		assertEquals(8, this.cg.getEdges().size());
		assertEquals(4, this.cg.getClassCallGraph().getEdges().size());
	}
}
