package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeGenealogy extends ChangeGenealogy<RCSTransaction, RCSTransaction> {
	
	public static TransactionChangeGenealogy readFromFile(File graphDBDir, PersistenceUtil persistenceUtil){
		PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(graphDBDir, persistenceUtil);
		return new TransactionChangeGenealogy(partitionChangeGenealogy, persistenceUtil);
	}
	
	private PersistenceUtil persistenceUtil;
	private PartitionChangeGenealogy partitionChangeGenealogy;
	
	private TransactionChangeGenealogy(PartitionChangeGenealogy partitionChangeGenealogy,
			PersistenceUtil persistenceUtil) {
		super(partitionChangeGenealogy.getCore());
		this.partitionChangeGenealogy = partitionChangeGenealogy;
		this.persistenceUtil = persistenceUtil;
	}
	
	@Override
	public boolean containsEdge(RCSTransaction from, RCSTransaction to) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(from);
		Collection<JavaChangeOperation> toPartition = transactionToPartition(to);
		return partitionChangeGenealogy.containsEdge(fromPartition, toPartition);
	}
	
	@Override
	public boolean containsVertex(RCSTransaction vertex) {
		Collection<JavaChangeOperation> vertexPartition = transactionToPartition(vertex);
		return partitionChangeGenealogy.containsVertex(vertexPartition);
	}
	
	@Override
	public Collection<RCSTransaction> getDependents(RCSTransaction t, GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
		Collection<JavaChangeOperation> dependents = partitionChangeGenealogy.getDependents(fromPartition, edgeTypes);
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		
		for (JavaChangeOperation parent : dependents) {
			result.add(parent.getRevision().getTransaction());
		}
		return result;
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(RCSTransaction from, RCSTransaction to) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(from);
		Collection<JavaChangeOperation> toPartition = transactionToPartition(to);
		return partitionChangeGenealogy.getEdges(fromPartition, toPartition);
	}
	
	@Override
	public Collection<RCSTransaction> getParents(RCSTransaction t, GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
		Collection<JavaChangeOperation> parents = partitionChangeGenealogy.getParents(fromPartition, edgeTypes);
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		
		for (JavaChangeOperation parent : parents) {
			result.add(parent.getRevision().getTransaction());
		}
		return result;
	}
	
	private Collection<JavaChangeOperation> transactionToPartition(RCSTransaction transaction){
		return PPAPersistenceUtil.getChangeOperation(persistenceUtil, transaction);
	}
	
	@Override
	public Iterator<RCSTransaction> vertexSet() {
		Iterator<JavaChangeOperation> vertexIter = partitionChangeGenealogy.vertexSet();
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		while (vertexIter.hasNext()) {
			JavaChangeOperation operation = vertexIter.next();
			result.add(operation.getRevision().getTransaction());
		}
		return result.iterator();
	}
	
	@Override
	public int vertexSize() {
		Iterator<JavaChangeOperation> vertexIter = partitionChangeGenealogy.vertexSet();
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		while (vertexIter.hasNext()) {
			JavaChangeOperation operation = vertexIter.next();
			result.add(operation.getRevision().getTransaction());
		}
		return result.size();
	}
	
}
