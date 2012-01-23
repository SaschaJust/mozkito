package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.io.File;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class GenealogyTestEnvironment {
	
	public static enum TestEnvironmentOperation {
		T1F1, T1F2, T2F3, T3F1D, T3F1A, T3F2, T4F3D, T4F3A, T4F4, T5F4, T6F2, T7F2, T8F2, T9F1, T10F3, T10F4, T3F2M;
	}
	
	private final PersistenceUtil                                    persistenceUtil;
	
	private final Map<RCSTransaction, Set<JavaChangeOperation>>      transactionMap;
	
	private final Map<Integer, RCSTransaction>                       environmentTransactions;
	
	private final Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations;
	
	private final Repository                                         repository;
	
	private final CoreChangeGenealogy                                changeGenealogy;
	
	private final File                                               tmpGraphDBFile;
	
	GenealogyTestEnvironment(PersistenceUtil persistenceUtil,
			Map<RCSTransaction, Set<JavaChangeOperation>> transactionMap,
			Map<Integer, RCSTransaction> environmentTransactions,
			Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations, Repository repository,
			CoreChangeGenealogy changeGenealogy, File tmpGraphDBFile) {
		
		this.persistenceUtil = persistenceUtil;
		this.transactionMap = transactionMap;
		this.environmentTransactions = environmentTransactions;
		this.environmentOperations = environmentOperations;
		this.repository = repository;
		this.changeGenealogy = changeGenealogy;
		this.tmpGraphDBFile = tmpGraphDBFile;
	}
	
	public void checkConsistency() {
		
		CompareCondition.equals(41, changeGenealogy.vertexSize(), "GenealogyTestEnvironment.consistenceCheck");
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(
				changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T2F3),
						environmentOperations.get(TestEnvironmentOperation.T1F2), GenealogyEdgeType.CallOnDefinition),
				"");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F1D),
				environmentOperations.get(TestEnvironmentOperation.T1F1),
				GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition
		.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2M),
				environmentOperations.get(TestEnvironmentOperation.T1F2),
				GenealogyEdgeType.DefinitionOnDefinition), "");
		
		Condition.check(
				changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T3F2),
						environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition),
				"");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3D),
				environmentOperations.get(TestEnvironmentOperation.T2F3), GenealogyEdgeType.DeletedCallOnCall), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F3A),
				environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(
				changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T4F4),
						environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition),
				"");
		
		Condition.check(
				changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T5F4),
						environmentOperations.get(TestEnvironmentOperation.T3F1A), GenealogyEdgeType.CallOnDefinition),
				"");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T6F2),
				environmentOperations.get(TestEnvironmentOperation.T3F2M),
				GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T7F2),
				environmentOperations.get(TestEnvironmentOperation.T6F2),
				GenealogyEdgeType.DefinitionOnDeletedDefinition), "");
		
		Condition
		.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T8F2),
				environmentOperations.get(TestEnvironmentOperation.T7F2),
				GenealogyEdgeType.DefinitionOnDefinition), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T9F1),
				environmentOperations.get(TestEnvironmentOperation.T3F1A),
				GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
				GenealogyEdgeType.DeletedCallOnDeletedDefinition), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
				environmentOperations.get(TestEnvironmentOperation.T9F1),
		        GenealogyEdgeType.DeletedCallOnDeletedDefinition), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F3),
		        environmentOperations.get(TestEnvironmentOperation.T4F3A), GenealogyEdgeType.DeletedCallOnCall), "");
		
		Condition.check(changeGenealogy.addEdge(environmentOperations.get(TestEnvironmentOperation.T10F4),
		        environmentOperations.get(TestEnvironmentOperation.T5F4), GenealogyEdgeType.DeletedCallOnCall), "");
	}
	
	public CoreChangeGenealogy getChangeGenealogy() {
		return changeGenealogy;
	}
	
	public Map<TestEnvironmentOperation, JavaChangeOperation> getEnvironmentOperations() {
		return environmentOperations;
	}
	
	public Map<Integer, RCSTransaction> getEnvironmentTransactions() {
		return environmentTransactions;
	}
	
	public PersistenceUtil getPersistenceUtil() {
		return persistenceUtil;
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public File getTmpGraphDBFile() {
		return tmpGraphDBFile;
	}
	
	public Map<RCSTransaction, Set<JavaChangeOperation>> getTransactionMap() {
		return transactionMap;
	}
	
}
