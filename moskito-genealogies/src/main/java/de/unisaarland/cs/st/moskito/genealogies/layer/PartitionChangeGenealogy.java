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

package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.utils.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class PartitionChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionChangeGenealogy extends ChangeGenealogyLayer<Collection<JavaChangeOperation>> {
	
	private final PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator;
	
	public PartitionChangeGenealogy(
	        final CoreChangeGenealogy coreGenealogy,
	        final PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
		super(coreGenealogy);
		this.partitionGenerator = partitionGenerator;
	}
	
	/**
	 * Instantiates a new partition change genealogy.
	 * 
	 * @param graphDBDir
	 *            the graph db dir
	 * @param persistenceUtil
	 *            the persistence util
	 * @param existingPartitions
	 *            the existing partitions
	 */
	public PartitionChangeGenealogy(
	        final File graphDBDir,
	        final PersistenceUtil persistenceUtil,
	        final PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
		super(ChangeGenealogyUtils.readFromDB(graphDBDir, persistenceUtil));
		this.partitionGenerator = partitionGenerator;
	}
	
	public Collection<Collection<JavaChangeOperation>> buildPartitions(final Collection<JavaChangeOperation> input) {
		return this.partitionGenerator.partition(input);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#containsEdge (java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean containsEdge(final Collection<JavaChangeOperation> from,
	                            final Collection<JavaChangeOperation> to) {
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
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#containsVertex (java.lang.Object)
	 */
	@Override
	public boolean containsVertex(final Collection<JavaChangeOperation> vertex) {
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
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#
	 */
	@Override
	public Collection<Collection<JavaChangeOperation>> getDependants(final Collection<JavaChangeOperation> t,
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
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#getEdges (java.lang.Object, java.lang.Object)
	 */
	@Override
	public Collection<GenealogyEdgeType> getEdges(final Collection<JavaChangeOperation> from,
	                                              final Collection<JavaChangeOperation> to) {
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
	
	@Override
	public String getNodeId(final Collection<JavaChangeOperation> t) {
		return this.partitionGenerator.getNodeId(t);
		// if (this.containsVertex(t) && (!t.isEmpty())) {
		// Iterator<JavaChangeOperation> iterator = t.iterator();
		// StringBuilder sb = new StringBuilder();
		//
		// JavaChangeOperation op = iterator.next();
		//
		// sb.append("[");
		// sb.append(this.core.getNodeId(op));
		// while (iterator.hasNext()) {
		// op = iterator.next();
		// sb.append(",");
		// sb.append(sb.append(this.core.getNodeId(op)));
		// }
		// sb.append("]");
		// return sb.toString();
		// }
		// return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy# getAllDependents(java.lang.Object)
	 */
	@Override
	public Collection<Collection<JavaChangeOperation>> getParents(final Collection<JavaChangeOperation> t,
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
	public Collection<Collection<JavaChangeOperation>> getRoots() {
		final Collection<Collection<JavaChangeOperation>> roots = new LinkedList<Collection<JavaChangeOperation>>();
		final Collection<JavaChangeOperation> vertices = new HashSet<JavaChangeOperation>();
		final Iterator<JavaChangeOperation> vertexIterator = this.core.vertexIterator();
		while (vertexIterator.hasNext()) {
			vertices.add(vertexIterator.next());
		}
		final Collection<Collection<JavaChangeOperation>> partitions = buildPartitions(vertices);
		for (final Collection<JavaChangeOperation> partition : partitions) {
			if (getAllParents(partition).isEmpty()) {
				roots.add(partition);
			}
		}
		return roots;
	}
	
	@Override
	public int inDegree(final Collection<JavaChangeOperation> node) {
		return inDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(final Collection<JavaChangeOperation> node,
	                    final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final Collection<JavaChangeOperation> dependant : getDependants(node, edgeTypes)) {
			numEdges += getEdges(dependant, node).size();
		}
		return numEdges;
	}
	
	@Override
	public int outDegree(final Collection<JavaChangeOperation> node) {
		return outDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(final Collection<JavaChangeOperation> node,
	                     final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final Collection<JavaChangeOperation> parent : getParents(node, edgeTypes)) {
			numEdges += getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#vertexSet ()
	 */
	@Override
	public Iterable<Collection<JavaChangeOperation>> vertexSet() {
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
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#vertexSize ()
	 */
	@Override
	public int vertexSize() {
		return this.core.vertexSize();
	}
	
}
