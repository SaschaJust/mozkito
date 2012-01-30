/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeGenealogy extends ChangeGenealogyLayer<RCSTransaction> {
	
	public static TransactionChangeGenealogy readFromFile(
			File graphDBDir,
			PersistenceUtil persistenceUtil,
			PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
		PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(graphDBDir, persistenceUtil,
				partitionGenerator);
		return new TransactionChangeGenealogy(partitionChangeGenealogy);
	}
	
	private PartitionChangeGenealogy partitionChangeGenealogy;
	
	public TransactionChangeGenealogy(CoreChangeGenealogy coreGenealogy) {
		super(coreGenealogy);
	}
	
	private TransactionChangeGenealogy(PartitionChangeGenealogy partitionChangeGenealogy) {
		super(partitionChangeGenealogy.getCore());
		this.partitionChangeGenealogy = partitionChangeGenealogy;
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
	public Collection<RCSTransaction> getDependants(RCSTransaction t, GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
		Collection<Collection<JavaChangeOperation>> dependents = partitionChangeGenealogy.getDependants(fromPartition,
				edgeTypes);
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		
		//FIXME this is pretty inefficient. Actually we need a mechanism that ensures that partition always belong to the same transaction
		//but this is actually inforced by the partition generator
		for (Collection<JavaChangeOperation> partition : dependents) {
			for (JavaChangeOperation parent : partition) {
				result.add(parent.getRevision().getTransaction());
			}
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
	public String getNodeId(RCSTransaction t) {
		return partitionChangeGenealogy.getNodeId(transactionToPartition(t));
	}
	
	@Override
	public Collection<RCSTransaction> getParents(RCSTransaction t, GenealogyEdgeType... edgeTypes) {
		Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
		Collection<Collection<JavaChangeOperation>> parents = partitionChangeGenealogy.getParents(fromPartition,
				edgeTypes);
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		
		for (Collection<JavaChangeOperation> partition : parents) {
			for (JavaChangeOperation parent : partition) {
				result.add(parent.getRevision().getTransaction());
			}
		}
		return result;
	}
	
	@Override
	public Collection<RCSTransaction> getRoots() {
		Collection<RCSTransaction> roots = new HashSet<RCSTransaction>();
		for(RCSTransaction t : vertexSet()){
			if (getAllParents(t).isEmpty()) {
				roots.add(t);
			}
		}
		return roots;
	}
	
	@Override
	public int inDegree(RCSTransaction node) {
		return inDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(RCSTransaction node, GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (RCSTransaction parent : this.getParents(node, edgeTypes)) {
			numEdges += this.getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	@Override
	public int outDegree(RCSTransaction node) {
		return outDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(RCSTransaction node, GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (RCSTransaction parent : this.getParents(node, edgeTypes)) {
			numEdges += this.getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	private Collection<JavaChangeOperation> transactionToPartition(RCSTransaction transaction){
		return PPAPersistenceUtil.getChangeOperation(core.getPersistenceUtil(), transaction);
	}
	
	@Override
	public Iterable<RCSTransaction> vertexSet() {
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (Collection<JavaChangeOperation> operations : partitionChangeGenealogy.vertexSet()) {
			for (JavaChangeOperation operation : operations) {
				result.add(operation.getRevision().getTransaction());
			}
		}
		return result;
	}
	
	@Override
	public int vertexSize() {
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (Collection<JavaChangeOperation> operations : partitionChangeGenealogy.vertexSet()) {
			for (JavaChangeOperation operation : operations) {
				result.add(operation.getRevision().getTransaction());
			}
		}
		return result.size();
	}
	
}
