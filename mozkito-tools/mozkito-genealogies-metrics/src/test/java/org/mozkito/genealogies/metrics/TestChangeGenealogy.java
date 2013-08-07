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

package org.mozkito.genealogies.metrics;

import java.io.File;

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.graphs.TitanDBGraphManager;

import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;

/**
 * The Class TestChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TestChangeGenealogy extends ChangeGenealogy<String> {
	
	/**
	 * Read from db.
	 * 
	 * @param dbFile
	 *            the db file
	 * @return the test change genealogy
	 */
	public static TestChangeGenealogy readFromDB(final File dbFile) {
		final KeyIndexableGraph graph = new TitanDBGraphManager(dbFile).createUtil();
		final TestChangeGenealogy genealogy = new TestChangeGenealogy(graph);
		return genealogy;
	}
	
	/**
	 * Instantiates a new test change genealogy.
	 * 
	 * @param graph
	 *            the graph
	 */
	public TestChangeGenealogy(final KeyIndexableGraph graph) {
		super(graph);
	}
	
	@Override
	public boolean addVertex(final String v) {
		return super.addVertex(v, v);
	}
	
	@Override
	public CoreChangeGenealogy getCore() {
		return null;
	}
	
	@Override
	public String getNodeId(final String t) {
		return t;
	}
	
	@Override
	protected String getVertexForNode(final Vertex dependentNode) {
		return (String) dependentNode.getProperty(ChangeGenealogy.NODE_ID);
	}
	
}
