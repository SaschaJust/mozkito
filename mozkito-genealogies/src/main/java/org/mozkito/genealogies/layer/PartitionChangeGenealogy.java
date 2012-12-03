/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/

package org.mozkito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.PartitionGenerator;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.utils.ChangeGenealogyUtils;
import org.mozkito.persistence.PersistenceUtil;


/**
 * The Class PartitionChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionChangeGenealogy extends ChangeGenealogyLayer {
	
	/** The partition generator. */
	private final PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionGenerator;
	
	/**
	 * Instantiates a new partition change genealogy.
	 *
	 * @param coreGenealogy the core genealogy
	 * @param partitionGenerator the partition generator
	 */
	public PartitionChangeGenealogy(
	        final CoreChangeGenealogy coreGenealogy,
	        final PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionGenerator) {
		super(coreGenealogy);
		this.partitionGenerator = partitionGenerator;
	}
	
	/**
	 * Instantiates a new partition change genealogy.
	 *
	 * @param graphDBDir the graph db dir
	 * @param persistenceUtil the persistence util
	 * @param partitionGenerator the partition generator
	 */
	public PartitionChangeGenealogy(
	        final File graphDBDir,
	        final PersistenceUtil persistenceUtil,
	        final PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionGenerator) {
		super(ChangeGenealogyUtils.readFromDB(graphDBDir, persistenceUtil));
		this.partitionGenerator = partitionGenerator;
	}
	
	/**
	 * Builds the partitions.
	 *
	 * @param input the input
	 * @return the collection
	 */
	public Collection<ChangeGenealogyLayerNode> buildPartitions(final Collection<JavaChangeOperation> input) {
		return this.partitionGenerator.partition(input);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#containsEdge (java.lang.Object,
	 * java.lang.Object)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#containsVertex (java.lang.Object)
	 */
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
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#
	 */
	@Override
	public Collection<ChangeGenealogyLayerNode> getDependants(final ChangeGenealogyLayerNode t,
	                                                          final GenealogyEdgeType... edgeTypes) {
		final Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (final JavaChangeOperation op : t) {
			for (final JavaChangeOperation dependent : this.core.getDependants(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return buildPartitions(result);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#getEdges (java.lang.Object, java.lang.Object)
	 */
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
	
	/**
	 * Gets the node id.
	 *
	 * @param t the t
	 * @return the node id
	 * @deprecated You can call <code>ChangeGenealogyLayerNode.getNodeId()</code> directly.
	 */
	@Override
	@Deprecated
	public String getNodeId(final ChangeGenealogyLayerNode t) {
		return t.getNodeId();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy# getAllDependents(java.lang.Object)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getRoots()
	 */
	@Override
	public Collection<ChangeGenealogyLayerNode> getRoots() {
		final Collection<ChangeGenealogyLayerNode> roots = new LinkedList<ChangeGenealogyLayerNode>();
		final Collection<JavaChangeOperation> vertices = new HashSet<JavaChangeOperation>();
		final Iterator<JavaChangeOperation> vertexIterator = this.core.vertexIterator();
		while (vertexIterator.hasNext()) {
			vertices.add(vertexIterator.next());
		}
		final Collection<ChangeGenealogyLayerNode> partitions = buildPartitions(vertices);
		for (final ChangeGenealogyLayerNode partition : partitions) {
			if (getAllParents(partition).isEmpty()) {
				roots.add(partition);
			}
		}
		return roots;
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#inDegree(java.lang.Object)
	 */
	@Override
	public int inDegree(final ChangeGenealogyLayerNode node) {
		return inDegree(node, GenealogyEdgeType.values());
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#inDegree(java.lang.Object, org.mozkito.genealogies.core.GenealogyEdgeType[])
	 */
	@Override
	public int inDegree(final ChangeGenealogyLayerNode node,
	                    final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final ChangeGenealogyLayerNode dependant : getDependants(node, edgeTypes)) {
			numEdges += getEdges(dependant, node).size();
		}
		return numEdges;
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#outDegree(java.lang.Object)
	 */
	@Override
	public int outDegree(final ChangeGenealogyLayerNode node) {
		return outDegree(node, GenealogyEdgeType.values());
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#outDegree(java.lang.Object, org.mozkito.genealogies.core.GenealogyEdgeType[])
	 */
	@Override
	public int outDegree(final ChangeGenealogyLayerNode node,
	                     final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final ChangeGenealogyLayerNode parent : getParents(node, edgeTypes)) {
			numEdges += getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#vertexSet ()
	 */
	@Override
	public Iterable<ChangeGenealogyLayerNode> vertexSet() {
		final Iterator<JavaChangeOperation> vertexIterator = this.core.vertexIterator();
		final Collection<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		while (vertexIterator.hasNext()) {
			final JavaChangeOperation elem = vertexIterator.next();
			result.add(elem);
		}
		return buildPartitions(result);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.layer.ChangeGenealogy#vertexSize ()
	 */
	@Override
	public int vertexSize() {
		return this.core.vertexSize();
	}
	
}
