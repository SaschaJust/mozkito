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

package org.mozkito.genealogies.core;

import java.util.HashMap;
import java.util.Map;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;

// import org.neo4j.graphdb.Transaction;

/**
 * The Class TransactionChangeGenealogy.
 */
public class TransactionChangeGenealogy extends ChangeGenealogy<ChangeSet> {
	
	private final CoreChangeGenealogy    coreGenealogy;
	private final Map<String, ChangeSet> nodeCache = new HashMap<>();
	private final PersistenceUtil        persistenceUtil;
	
	/**
	 * Instantiates a new transaction change genealogy.
	 * 
	 * @param coreGenealogy
	 *            the core genealogy
	 * @param graph
	 *            the graph
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public TransactionChangeGenealogy(final CoreChangeGenealogy coreGenealogy, final KeyIndexableGraph graph,
	        final PersistenceUtil persistenceUtil) {
		super(graph);
		this.persistenceUtil = persistenceUtil;
		this.coreGenealogy = coreGenealogy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#addVertex(org.mozkito.persistence.Annotated)
	 */
	@Override
	public boolean addVertex(final ChangeSet v) {
		return super.addVertex(v.getId(), v);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#getCore()
	 */
	@Override
	public CoreChangeGenealogy getCore() {
		return this.coreGenealogy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#getNodeId(org.mozkito.persistence.Annotated)
	 */
	@Override
	public String getNodeId(final ChangeSet t) {
		return t.getId();
	}
	
	@Override
	protected ChangeSet getVertexForNode(final Vertex dependentNode) {
		final String id = (String) dependentNode.getProperty(ChangeGenealogy.NODE_ID);
		if (!this.nodeCache.containsKey(id)) {
			this.nodeCache.put(id, this.persistenceUtil.loadById(id, ChangeSet.class));
		}
		return this.nodeCache.get(id);
	}
	
}
