package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class PartitionChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionChangeGenealogy extends ChangeGenealogy<Collection<JavaChangeOperation>, JavaChangeOperation> {
	
	
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
	public PartitionChangeGenealogy(File graphDBDir, PersistenceUtil persistenceUtil) {
		super(ChangeGenealogyUtils.readFromDB(graphDBDir, persistenceUtil));
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
	public Collection<JavaChangeOperation> getDependents(Collection<JavaChangeOperation> t,
			GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (JavaChangeOperation op : t) {
			for (JavaChangeOperation dependent : core.getDependents(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return result;
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#
	 * getAllDependents(java.lang.Object)
	 * 
	 */
	@Override
	public Collection<JavaChangeOperation> getParents(Collection<JavaChangeOperation> t,
			GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (JavaChangeOperation op : t) {
			for (JavaChangeOperation dependent : core.getParents(op, edgeTypes)) {
				if (!t.contains(dependent)) {
					result.add(dependent);
				}
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogy#vertexSet
	 * ()
	 */
	@Override
	public Iterator<JavaChangeOperation> vertexSet() {
		return core.vertexIterator();
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
