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

package org.mozkito.genealogies.layer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.core.PartitionGenerator;

import com.tinkerpop.blueprints.Vertex;

/**
 * The Class PartitionChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionChangeGenealogy extends ChangeGenealogyLayer {
	
	private final CoreChangeGenealogy                                                                       core;
	private final PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionGenerator;
	
	/**
	 * Instantiates a new partition change genealogy.
	 * 
	 * @param coreGenealogy
	 *            the core genealogy
	 * @param partitionGenerator
	 *            the partition generator
	 */
	public PartitionChangeGenealogy(
	        final CoreChangeGenealogy coreGenealogy,
	        final PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionGenerator) {
		super(coreGenealogy.getGraphDB());
		this.core = coreGenealogy;
		this.partitionGenerator = partitionGenerator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#addVertex(java.lang.Object)
	 */
	@Override
	public boolean addVertex(final ChangeGenealogyLayerNode v) {
		return false;
	}
	
	/**
	 * Builds the partitions.
	 * 
	 * @param input
	 *            the input
	 * @return the collection
	 */
	public Collection<ChangeGenealogyLayerNode> buildPartitions(final Collection<JavaChangeOperation> input) {
		return this.partitionGenerator.partition(input);
	}
	
	@Override
	public boolean containsEdge(final ChangeGenealogyLayerNode from,
	                            final ChangeGenealogyLayerNode to) {
		for (final JavaChangeOperation singleFrom : from) {
			for (final JavaChangeOperation singleTo : to) {
				if (this.core.containsEdge(singleFrom, singleTo)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean containsVertex(final ChangeGenealogyLayerNode vertex) {
		if (vertex.isEmpty()) {
			return false;
		}
		boolean result = true;
		for (final JavaChangeOperation op : vertex) {
			result &= this.core.hasVertex(op);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#getCore()
	 */
	@Override
	public CoreChangeGenealogy getCore() {
		return this.core;
	}
	
	@Override
	public Collection<ChangeGenealogyLayerNode> getDependents(final ChangeGenealogyLayerNode t,
	                                                          final GenealogyEdgeType... edgeTypes) {
		final Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (final JavaChangeOperation op : t) {
			for (final JavaChangeOperation dependent : this.core.getDependents(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return buildPartitions(result);
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(final ChangeGenealogyLayerNode from,
	                                              final ChangeGenealogyLayerNode to) {
		final Set<GenealogyEdgeType> edges = new HashSet<GenealogyEdgeType>();
		for (final JavaChangeOperation singleFrom : from) {
			for (final JavaChangeOperation singleTo : to) {
				final GenealogyEdgeType edge = this.core.getEdge(singleFrom, singleTo);
				if (edge != null) {
					edges.add(edge);
				}
			}
		}
		return edges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#getNodeId(java.lang.Object)
	 */
	@Override
	public String getNodeId(final ChangeGenealogyLayerNode t) {
		return t.getNodeId();
	}
	
	@Override
	public Collection<ChangeGenealogyLayerNode> getParents(final ChangeGenealogyLayerNode t,
	                                                       final GenealogyEdgeType... edgeTypes) {
		final Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (final JavaChangeOperation op : t) {
			for (final JavaChangeOperation dependent : this.core.getParents(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return buildPartitions(result);
	}
	
	@Override
	public Collection<ChangeGenealogyLayerNode> getRoots() {
		final Collection<ChangeGenealogyLayerNode> roots = new LinkedList<ChangeGenealogyLayerNode>();
		final Collection<JavaChangeOperation> vertices = new HashSet<JavaChangeOperation>();
		for (final JavaChangeOperation op : this.core.vertexSet()) {
			vertices.add(op);
		}
		final Collection<ChangeGenealogyLayerNode> partitions = buildPartitions(vertices);
		for (final ChangeGenealogyLayerNode partition : partitions) {
			if (getAllParents(partition).isEmpty()) {
				roots.add(partition);
			}
		}
		return roots;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.core.ChangeGenealogy#getVertexForNode(com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	protected ChangeGenealogyLayerNode getVertexForNode(final Vertex dependentNode) {
		return null;
	}
	
	@Override
	public int inDegree(final ChangeGenealogyLayerNode node,
	                    final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final ChangeGenealogyLayerNode dependant : getDependents(node, edgeTypes)) {
			numEdges += getEdges(dependant, node).size();
		}
		return numEdges;
	}
	
	@Override
	public int outDegree(final ChangeGenealogyLayerNode node,
	                     final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final ChangeGenealogyLayerNode parent : getParents(node, edgeTypes)) {
			numEdges += getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	@Override
	public Iterable<ChangeGenealogyLayerNode> vertexSet() {
		final Collection<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		for (final JavaChangeOperation op : this.core.vertexSet()) {
			result.add(op);
		}
		return buildPartitions(result);
	}
	
	@Override
	public int vertexSize() {
		int result = 0;
		for (@SuppressWarnings ("unused")
		final ChangeGenealogyLayerNode n : vertexSet()) {
			++result;
		}
		return result;
	}
	
}
