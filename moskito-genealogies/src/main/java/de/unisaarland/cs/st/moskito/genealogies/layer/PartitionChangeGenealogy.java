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
	
	
	private PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator;
	
	public PartitionChangeGenealogy(
			CoreChangeGenealogy coreGenealogy,
			PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
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
			File graphDBDir,
			PersistenceUtil persistenceUtil,
			PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
		super(ChangeGenealogyUtils.readFromDB(graphDBDir, persistenceUtil));
		this.partitionGenerator = partitionGenerator;
	}
	
	public Collection<Collection<JavaChangeOperation>> buildPartitions(Collection<JavaChangeOperation> input) {
		return this.partitionGenerator.partition(input);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#containsEdge
	 * (java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean containsEdge(Collection<JavaChangeOperation> from, Collection<JavaChangeOperation> to) {
		for (JavaChangeOperation singleFrom : from) {
			for (JavaChangeOperation singleTo : to) {
				if (core.containsEdge(singleFrom, singleTo)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#containsVertex
	 * (java.lang.Object)
	 */
	@Override
	public boolean containsVertex(Collection<JavaChangeOperation> vertex) {
		if (vertex.isEmpty()) {
			return false;
		}
		boolean result = true;
		for (JavaChangeOperation op : vertex) {
			result &= core.hasVertex(op);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#
	 */
	@Override
	public Collection<Collection<JavaChangeOperation>> getDependants(Collection<JavaChangeOperation> t,
			GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (JavaChangeOperation op : t) {
			for (JavaChangeOperation dependent : core.getDependants(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return buildPartitions(result);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#getEdges
	 * (java.lang.Object, java.lang.Object)
	 */
	@Override
	public Collection<GenealogyEdgeType> getEdges(Collection<JavaChangeOperation> from,
			Collection<JavaChangeOperation> to) {
		Set<GenealogyEdgeType> edges = new HashSet<GenealogyEdgeType>();
		for (JavaChangeOperation singleFrom : from) {
			for (JavaChangeOperation singleTo : to) {
				GenealogyEdgeType edge = core.getEdge(singleFrom, singleTo);
				if (edge != null) {
					edges.add(edge);
				}
			}
		}
		return edges;
	}
	
	
	@Override
	public String getNodeId(Collection<JavaChangeOperation> t) {
		if(this.containsVertex(t) && (!t.isEmpty())){
			Iterator<JavaChangeOperation> iterator = t.iterator();
			StringBuilder sb = new StringBuilder();
			
			JavaChangeOperation op = iterator.next();
			
			sb.append("[");
			sb.append(core.getNodeId(op));
			while(iterator.hasNext()){
				op = iterator.next();
				sb.append(",");
				sb.append(sb.append(core.getNodeId(op)));
			}
			sb.append("]");
			return sb.toString();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#
	 * getAllDependents(java.lang.Object)
	 * 
	 */
	@Override
	public Collection<Collection<JavaChangeOperation>> getParents(Collection<JavaChangeOperation> t,
			GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (JavaChangeOperation op : t) {
			for (JavaChangeOperation dependent : core.getParents(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return buildPartitions(result);
	}
	
	@Override
	public Collection<Collection<JavaChangeOperation>> getRoots() {
		Collection<Collection<JavaChangeOperation>> roots = new LinkedList<Collection<JavaChangeOperation>>();
		Collection<JavaChangeOperation> vertices = new HashSet<JavaChangeOperation>();
		Iterator<JavaChangeOperation> vertexIterator = core.vertexIterator();
		while(vertexIterator.hasNext()){
			vertices.add(vertexIterator.next());
		}
		Collection<Collection<JavaChangeOperation>> partitions = this.buildPartitions(vertices);
		for (Collection<JavaChangeOperation> partition : partitions) {
			if (this.getAllParents(partition).isEmpty()) {
				roots.add(partition);
			}
		}
		return roots;
	}
	
	@Override
	public int inDegree(Collection<JavaChangeOperation> node) {
		return inDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(Collection<JavaChangeOperation> node, GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (Collection<JavaChangeOperation> dependant : this.getDependants(node, edgeTypes)) {
			numEdges += this.getEdges(dependant, node).size();
		}
		return numEdges;
	}
	
	@Override
	public int outDegree(Collection<JavaChangeOperation> node) {
		return outDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(Collection<JavaChangeOperation> node, GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (Collection<JavaChangeOperation> parent : this.getParents(node, edgeTypes)) {
			numEdges += this.getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#vertexSet
	 * ()
	 */
	@Override
	public Iterable<Collection<JavaChangeOperation>> vertexSet() {
		Iterator<JavaChangeOperation> vertexIterator = core.vertexIterator();
		Collection<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		while (vertexIterator.hasNext()) {
			JavaChangeOperation elem = vertexIterator.next();
			result.add(elem);
		}
		return buildPartitions(result);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#vertexSize
	 * ()
	 */
	@Override
	public int vertexSize() {
		return core.vertexSize();
	}
	
}
