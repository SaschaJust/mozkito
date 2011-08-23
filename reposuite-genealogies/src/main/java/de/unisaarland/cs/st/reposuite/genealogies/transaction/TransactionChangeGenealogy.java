package de.unisaarland.cs.st.reposuite.genealogies.transaction;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.reposuite.genealogies.CoreChangeGenealogy;
import de.unisaarland.cs.st.reposuite.genealogies.GenealogyAnalyzer;
import de.unisaarland.cs.st.reposuite.genealogies.GenealogyEdgeType;
import de.unisaarland.cs.st.reposuite.genealogies.GenealogyVertex;
import de.unisaarland.cs.st.reposuite.genealogies.GenealogyVertexIterator;
import de.unisaarland.cs.st.reposuite.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class TransactionChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TransactionChangeGenealogy implements ChangeGenealogy {
	
	/** The genealogy. */
	private final CoreChangeGenealogy   genealogy;
	
	/** The persistence util. */
	private final PersistenceUtil   persistenceUtil;
	
	/** The genealogy analyzer. */
	private final GenealogyAnalyzer genealogyAnalyzer;
	
	/**
	 * Instantiates a new transaction change genealogy.
	 * 
	 * @param graphDBFile
	 *            the graph db file
	 * @param persistenceUtil
	 *            the persistence util
	 * @param genealogyAnalyzer
	 *            the genealogy analyzer
	 */
	public TransactionChangeGenealogy(final File graphDBFile, final PersistenceUtil persistenceUtil,
			final GenealogyAnalyzer genealogyAnalyzer) {
		this.genealogy = CoreChangeGenealogy.readFromDB(graphDBFile, persistenceUtil);
		this.persistenceUtil = persistenceUtil;
		this.genealogyAnalyzer = genealogyAnalyzer;
	}
	
	@Override
	public boolean addEdge(GenealogyVertex dependantVertex, GenealogyVertex targetVertex, GenealogyEdgeType edgeType) {
		return this.genealogy.addEdge(dependantVertex, targetVertex, edgeType);
	}
	
	/**
	 * Adds the transactions.
	 * 
	 * @param transactions
	 *            the transactions
	 */
	public void addTransactions(final Collection<RCSTransaction> transactions) {
		//add all vertices
		int counter = 0;
		for (RCSTransaction transaction : transactions) {
			++counter;
			List<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
					transaction);
			if (Logger.logInfo()) {
				Logger.info("Adding transaction " + transaction.getId() + " ... (" + counter + "/"
						+ transactions.size() + ")");
			}
			if (changeOperations.isEmpty()) {
				continue;
			}
			this.genealogy.addVertex(changeOperations);
		}
		if (Logger.logInfo()) {
			Logger.info("Added " + this.genealogy.vertexSize() + " vertices to the TransactionChangeGenealogy.");
		}
		
		Map<String, List<JavaChangeOperation>> cache = new HashMap<String, List<JavaChangeOperation>>();
		
		//add edges
		for (RCSTransaction transaction : transactions) {
			List<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
					transaction);
			for (JavaChangeOperation operation : changeOperations) {
				Collection<JavaChangeOperation> dependencies = this.genealogyAnalyzer.getDependencies(operation,
						persistenceUtil);
				for (JavaChangeOperation parent : dependencies) {
					GenealogyEdgeType edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(operation, parent);
					if (edgeType == null) {
						continue;
					}
					if (!cache.containsKey(parent.getRevision().getTransaction().getId())) {
						cache.put(parent.getRevision().getTransaction().getId(), PPAPersistenceUtil.getChangeOperation(
								persistenceUtil, parent.getRevision().getTransaction()));
					}
					List<JavaChangeOperation> parentList = cache.get(parent.getRevision().getTransaction().getId());
					this.genealogy.addEdge(changeOperations, parentList, edgeType);
				}
			}
		}
	}
	
	@Override
	public void close() {
		this.genealogy.close();
	}
	
	@Override
	public boolean containsEdge(GenealogyVertex from, GenealogyVertex to) {
		return this.genealogy.containsEdge(from, to);
	}
	
	@Override
	public boolean containsVertex(GenealogyVertex vertex) {
		return this.genealogy.containsVertex(vertex);
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(GenealogyVertex from, GenealogyVertex to) {
		return this.genealogy.getEdges(from, to);
	}
	
	@Override
	public Collection<JavaChangeOperation> getJavaChangeOperationsForVertex(GenealogyVertex v) {
		return this.genealogy.getJavaChangeOperationsForVertex(v);
	}
	
	@Override
	public RCSTransaction getTransactionForVertex(GenealogyVertex v) {
		return this.genealogy.getTransactionForVertex(v);
	}
	
	/**
	 * Gets the vertex.
	 * 
	 * @param transaction
	 *            the transaction
	 * @return the vertex
	 */
	@NoneNull
	public GenealogyVertex getVertex(final RCSTransaction transaction) {
		//		PPAPersistenceUtil.getChangeOperation(persistenceUtil, transaction);
		return this.genealogy.getVertex(transaction.getId(), new HashSet<Long>());
	}
	
	@Override
	public GenealogyVertexIterator vertexSet() {
		return this.genealogy.vertexSet();
	}
	
	@Override
	public int vertexSize(){
		return this.genealogy.vertexSize();
	}
}
