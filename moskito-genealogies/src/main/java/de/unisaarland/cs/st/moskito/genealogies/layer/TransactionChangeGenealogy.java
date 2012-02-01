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
 ******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeGenealogy extends ChangeGenealogyLayer<RCSTransaction> {
	
	public static TransactionChangeGenealogy readFromFile(final File graphDBDir,
	                                                      final PersistenceUtil persistenceUtil,
	                                                      final PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> partitionGenerator) {
		final PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(graphDBDir,
		                                                                                       persistenceUtil,
		                                                                                       partitionGenerator);
		return new TransactionChangeGenealogy(partitionChangeGenealogy);
	}
	
	private final Map<RCSTransaction, Collection<JavaChangeOperation>>                      partitionCache  = new HashMap<RCSTransaction, Collection<JavaChangeOperation>>();
	
	private final Map<RCSTransaction, Map<GenealogyEdgeType[], Collection<RCSTransaction>>> dependentsCache = new HashMap<RCSTransaction, Map<GenealogyEdgeType[], Collection<RCSTransaction>>>();
	private final Map<RCSTransaction, Map<GenealogyEdgeType[], Collection<RCSTransaction>>> parentsCache    = new HashMap<RCSTransaction, Map<GenealogyEdgeType[], Collection<RCSTransaction>>>();
	
	private final PartitionChangeGenealogy                                                  partitionChangeGenealogy;
	
	public TransactionChangeGenealogy(final CoreChangeGenealogy coreGenealogy) {
		super(coreGenealogy);
		this.partitionChangeGenealogy = new PartitionChangeGenealogy(coreGenealogy, new TransactionPartitioner());
	}
	
	private TransactionChangeGenealogy(final PartitionChangeGenealogy partitionChangeGenealogy) {
		super(partitionChangeGenealogy.getCore());
		this.partitionChangeGenealogy = partitionChangeGenealogy;
	}
	
	@Override
	public boolean containsEdge(final RCSTransaction from,
	                            final RCSTransaction to) {
		final Collection<JavaChangeOperation> fromPartition = transactionToPartition(from);
		final Collection<JavaChangeOperation> toPartition = transactionToPartition(to);
		return this.partitionChangeGenealogy.containsEdge(fromPartition, toPartition);
	}
	
	@Override
	public boolean containsVertex(final RCSTransaction vertex) {
		final Collection<JavaChangeOperation> vertexPartition = transactionToPartition(vertex);
		return this.partitionChangeGenealogy.containsVertex(vertexPartition);
	}
	
	@Override
	public Collection<RCSTransaction> getDependants(final RCSTransaction t,
	                                                final GenealogyEdgeType... edgeTypes) {
		
		if ((!this.dependentsCache.containsKey(t)) || (!this.dependentsCache.get(t).containsKey(edgeTypes))) {
			final Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
			final Collection<Collection<JavaChangeOperation>> dependents = this.partitionChangeGenealogy.getDependants(fromPartition,
			                                                                                                           edgeTypes);
			final Set<RCSTransaction> result = new HashSet<RCSTransaction>();
			
			// FIXME this is pretty inefficient. Actually we need a mechanism that ensures that partition always belong
			// to
			// the same transaction
			// but this is actually inforced by the partition generator
			for (final Collection<JavaChangeOperation> partition : dependents) {
				for (final JavaChangeOperation parent : partition) {
					result.add(parent.getRevision().getTransaction());
				}
			}
			if (!this.dependentsCache.containsKey(t)) {
				this.dependentsCache.put(t, new HashMap<GenealogyEdgeType[], Collection<RCSTransaction>>());
			}
			this.dependentsCache.get(t).put(edgeTypes, result);
		}
		
		return this.dependentsCache.get(t).get(edgeTypes);
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(final RCSTransaction from,
	                                              final RCSTransaction to) {
		final Collection<JavaChangeOperation> fromPartition = transactionToPartition(from);
		final Collection<JavaChangeOperation> toPartition = transactionToPartition(to);
		return this.partitionChangeGenealogy.getEdges(fromPartition, toPartition);
	}
	
	@Override
	public String getNodeId(final RCSTransaction t) {
		return t.getId();
	}
	
	@Override
	public Collection<RCSTransaction> getParents(final RCSTransaction t,
	                                             final GenealogyEdgeType... edgeTypes) {
		
		if ((!this.parentsCache.containsKey(t)) || (!this.parentsCache.get(t).containsKey(edgeTypes))) {
			final Collection<JavaChangeOperation> fromPartition = transactionToPartition(t);
			final Collection<Collection<JavaChangeOperation>> parents = this.partitionChangeGenealogy.getParents(fromPartition,
			                                                                                                     edgeTypes);
			final Set<RCSTransaction> result = new HashSet<RCSTransaction>();
			
			for (final Collection<JavaChangeOperation> partition : parents) {
				for (final JavaChangeOperation parent : partition) {
					result.add(parent.getRevision().getTransaction());
				}
			}
			if (!this.parentsCache.containsKey(t)) {
				this.parentsCache.put(t, new HashMap<GenealogyEdgeType[], Collection<RCSTransaction>>());
			}
			this.parentsCache.get(t).put(edgeTypes, result);
		}
		return this.parentsCache.get(t).get(edgeTypes);
	}
	
	@Override
	public Collection<RCSTransaction> getRoots() {
		final Collection<RCSTransaction> roots = new HashSet<RCSTransaction>();
		for (final RCSTransaction t : vertexSet()) {
			if (getAllParents(t).isEmpty()) {
				roots.add(t);
			}
		}
		return roots;
	}
	
	@Override
	public int inDegree(final RCSTransaction node) {
		return inDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(final RCSTransaction node,
	                    final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final RCSTransaction parent : getParents(node, edgeTypes)) {
			numEdges += getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	@Override
	public int outDegree(final RCSTransaction node) {
		return outDegree(node, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(final RCSTransaction node,
	                     final GenealogyEdgeType... edgeTypes) {
		int numEdges = 0;
		for (final RCSTransaction parent : getParents(node, edgeTypes)) {
			numEdges += getEdges(node, parent).size();
		}
		return numEdges;
	}
	
	private Collection<JavaChangeOperation> transactionToPartition(final RCSTransaction transaction) {
		if (!this.partitionCache.containsKey(transaction)) {
			
			if (Logger.logDebug()) {
				Logger.debug("Loading partition from database: " + transaction.toString());
			}
			this.partitionCache.put(transaction,
			                        PPAPersistenceUtil.getChangeOperation(this.core.getPersistenceUtil(), transaction));
		}
		return this.partitionCache.get(transaction);
	}
	
	@Override
	public Iterable<RCSTransaction> vertexSet() {
		final Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (final Collection<JavaChangeOperation> operations : this.partitionChangeGenealogy.vertexSet()) {
			for (final JavaChangeOperation operation : operations) {
				result.add(operation.getRevision().getTransaction());
			}
		}
		return result;
	}
	
	@Override
	public int vertexSize() {
		final Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (final Collection<JavaChangeOperation> operations : this.partitionChangeGenealogy.vertexSet()) {
			for (final JavaChangeOperation operation : operations) {
				result.add(operation.getRevision().getTransaction());
			}
		}
		return result.size();
	}
	
}
