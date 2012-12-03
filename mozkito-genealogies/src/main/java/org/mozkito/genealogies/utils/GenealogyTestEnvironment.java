/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.mozkito.genealogies.utils;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class GenealogyTestEnvironment.
 */
public class GenealogyTestEnvironment {
	
	/**
	 * The Enum TestEnvironmentOperation.
	 */
	public static enum TestEnvironmentOperation {
		
		/** The T1 f1. */
		T1F1, 
 /** The T1 f2. */
 T1F2, 
 /** The T2 f3. */
 T2F3, 
 /** The T3 f1 d. */
 T3F1D, 
 /** The T3 f1 a. */
 T3F1A, 
 /** The T3 f2. */
 T3F2, 
 /** The T4 f3 d. */
 T4F3D, 
 /** The T4 f3 a. */
 T4F3A, 
 /** The T4 f4. */
 T4F4, 
 /** The T5 f4. */
 T5F4, 
 /** The T6 f2. */
 T6F2, 
 /** The T7 f2. */
 T7F2, 
 /** The T8 f2. */
 T8F2, 
 /** The T9 f1. */
 T9F1, 
 /** The T10 f3. */
 T10F3, 
 /** The T10 f4. */
 T10F4, 
 /** The T3 f2 m. */
 T3F2M;
	}
	
	/** The persistence util. */
	private final PersistenceUtil                                    persistenceUtil;
	
	/** The transaction map. */
	private final Map<RCSTransaction, Set<JavaChangeOperation>>      transactionMap;
	
	/** The environment transactions. */
	private final Map<Integer, RCSTransaction>                       environmentTransactions;
	
	/** The environment operations. */
	private final Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations;
	
	/** The repository. */
	private final Repository                                         repository;
	
	/** The change genealogy. */
	private final CoreChangeGenealogy                                changeGenealogy;
	
	/** The tmp graph db file. */
	private final File                                               tmpGraphDBFile;
	
	/**
	 * Instantiates a new genealogy test environment.
	 *
	 * @param persistenceUtil the persistence util
	 * @param transactionMap the transaction map
	 * @param environmentTransactions the environment transactions
	 * @param environmentOperations the environment operations
	 * @param repository the repository
	 * @param changeGenealogy the change genealogy
	 * @param tmpGraphDBFile the tmp graph db file
	 */
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
	
	/**
	 * Check consistency.
	 */
	public void checkConsistency() {
		
		CompareCondition.equals(41, this.changeGenealogy.vertexSize(), "GenealogyTestEnvironment.consistenceCheck");
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                        GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T2F3),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T1F2),
		                                        GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T3F1D),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T1F1),
		                                        GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T1F2),
		                                        GenealogyEdgeType.DefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T3F2),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                        GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T4F3D),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T2F3),
		                                        GenealogyEdgeType.DeletedCallOnCall), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                        GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T4F4),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                        GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T5F4),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                        GenealogyEdgeType.CallOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T6F2),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F2M),
		                                        GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T7F2),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T6F2),
		                                        GenealogyEdgeType.DefinitionOnDeletedDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T8F2),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T7F2),
		                                        GenealogyEdgeType.DefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T9F1),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T3F1A),
		                                        GenealogyEdgeType.DeletedDefinitionOnDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T9F1),
		                                        GenealogyEdgeType.DeletedCallOnDeletedDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T9F1),
		                                        GenealogyEdgeType.DeletedCallOnDeletedDefinition), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T10F3),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T4F3A),
		                                        GenealogyEdgeType.DeletedCallOnCall), "");
		
		Condition.check(this.changeGenealogy.addEdge(this.environmentOperations.get(TestEnvironmentOperation.T10F4),
		                                        this.environmentOperations.get(TestEnvironmentOperation.T5F4),
		                                        GenealogyEdgeType.DeletedCallOnCall), "");
	}
	
	/**
	 * Gets the change genealogy.
	 *
	 * @return the change genealogy
	 */
	public CoreChangeGenealogy getChangeGenealogy() {
		return this.changeGenealogy;
	}
	
	/**
	 * Gets the environment operations.
	 *
	 * @return the environment operations
	 */
	public Map<TestEnvironmentOperation, JavaChangeOperation> getEnvironmentOperations() {
		return this.environmentOperations;
	}
	
	/**
	 * Gets the environment transactions.
	 *
	 * @return the environment transactions
	 */
	public Map<Integer, RCSTransaction> getEnvironmentTransactions() {
		return this.environmentTransactions;
	}
	
	/**
	 * Gets the persistence util.
	 *
	 * @return the persistence util
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public Repository getRepository() {
		return this.repository;
	}
	
	/**
	 * Gets the tmp graph db file.
	 *
	 * @return the tmp graph db file
	 */
	public File getTmpGraphDBFile() {
		return this.tmpGraphDBFile;
	}
	
	/**
	 * Gets the transaction map.
	 *
	 * @return the transaction map
	 */
	public Map<RCSTransaction, Set<JavaChangeOperation>> getTransactionMap() {
		return this.transactionMap;
	}
	
}
