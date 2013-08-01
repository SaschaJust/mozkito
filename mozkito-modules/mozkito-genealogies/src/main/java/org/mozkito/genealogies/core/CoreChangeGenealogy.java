/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.genealogies.core;

import java.io.File;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.genealogies.persistence.JavaChangeOperationCache;
import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.TitanDBGraphManager;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.FileUtils.FileShutdownAction;
import org.mozkito.utilities.io.exceptions.FilePermissionException;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;

import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;

/**
 * The Class ChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CoreChangeGenealogy extends ChangeGenealogy<JavaChangeOperation> {
	
	/** The transaction genealogy. */
	private final ChangeGenealogy<ChangeSet> transactionGenealogy;
	private final JavaChangeOperationCache   nodeCache;
	private final PersistenceUtil            persistenceUtil;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param graphManager
	 *            the graph manager
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public CoreChangeGenealogy(final GraphManager graphManager, final PersistenceUtil persistenceUtil) {
		
		super(graphManager.createUtil());
		this.persistenceUtil = persistenceUtil;
		this.nodeCache = new JavaChangeOperationCache(persistenceUtil);
		final File transactionDbFile = new File(graphManager.getFileHandle().getAbsolutePath()
		        + FileUtils.fileSeparator + "transactionLayer");
		try {
			FileUtils.createDir(transactionDbFile, FileShutdownAction.KEEP);
		} catch (final FilePermissionException e) {
			throw new UnrecoverableError("Cannot create transaction genealogy graph directory.", e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				CoreChangeGenealogy.this.graph.shutdown();
			}
		});
		
		final KeyIndexableGraph transactionGraph = new TitanDBGraphManager(transactionDbFile).createUtil();
		this.transactionGenealogy = new TransactionChangeGenealogy(this, transactionGraph, persistenceUtil);
	}
	
	/**
	 * Adds a directed edge between target <--type-- dependent of type edgeType. Adds missing vertices before adding
	 * edge, if necessary.
	 * 
	 * @param dependent
	 *            the dependent
	 * @param target
	 *            The collection of JavaChangeOperations that represent the edge target vertex.
	 * @param edgeType
	 *            the GenealogyEdgeType of the edge to be added
	 * @return true, if successful
	 */
	@Override
	@NoneNull
	public boolean addEdge(final JavaChangeOperation dependent,
	                       final JavaChangeOperation target,
	                       final GenealogyEdgeType edgeType) {
		
		final ChangeType depChangeType = dependent.getChangeType();
		final JavaElement depElement = dependent.getChangedElementLocation().getElement();
		final JavaElement targetElement = target.getChangedElementLocation().getElement();
		final ChangeType targetChangeType = target.getChangeType();
		
		switch (edgeType) {
			case DefinitionOnDefinition:
			case DefinitionOnDeletedDefinition:
			case ModifiedDefinitionOnDefinition:
				if (depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DefinitionOn(Deleted)Definition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				
				switch (edgeType) {
					case ModifiedDefinitionOnDefinition:
					case DefinitionOnDefinition:
						if (targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DefinitionOnDefinition` edge pointing to delete operation. Edge not added.");
							}
							return false;
						}
						break;
					case DefinitionOnDeletedDefinition:
						if (!targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DefinitionOnDefinition` edge pointing to non-delete operation. Edge not added.");
							}
							return false;
						}
						break;
					default:
						if (Logger.logError()) {
							Logger.error("Unhandled situation found: edgeType=" + edgeType.toString() + " dependent="
							        + dependent.toString() + " target=" + target.toString());
						}
						return false;
				}
				break;
			
			case DeletedDefinitionOnDefinition:
				if (!depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge starting from non-delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				if (targetChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge pointing to delete operation. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedDefinitionOnDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				break;
			case CallOnDefinition:
				if (depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (targetChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodCall)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge starting from non JavaMethodCall. Edge not added.");
					}
					return false;
				}
				if (!(targetElement instanceof JavaMethodDefinition)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `CallOnDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				break;
			case DeletedCallOnCall:
			case DeletedCallOnDeletedDefinition:
				
				if (!depChangeType.equals(ChangeType.Deleted)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedCallOn[DeletedDefinition|Call]` edge starting from non-delete operation. Edge not added.");
					}
					return false;
				}
				if (!(depElement instanceof JavaMethodCall)) {
					if (Logger.logError()) {
						Logger.error("Cannot add `DeletedCallOn[DeletedDefinition|Call]` edge starting from non JavaMethodDefinition. Edge not added.");
					}
					return false;
				}
				switch (edgeType) {
					case DeletedCallOnCall:
						if (targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnCall` edge pointing to delete operation. Edge not added.");
							}
							return false;
						}
						if (!(targetElement instanceof JavaMethodCall)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnCall` edge pointing to non JavaMethodCall. Edge not added.");
							}
							return false;
						}
						break;
					case DeletedCallOnDeletedDefinition:
						if (!targetChangeType.equals(ChangeType.Deleted)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnDeletedDefinition` edge pointing to non-delete operation. Edge not added.");
							}
							return false;
						}
						if (!(targetElement instanceof JavaMethodDefinition)) {
							if (Logger.logError()) {
								Logger.error("Cannot add `DeletedCallOnDeletedDefinition` edge pointing to non JavaMethodDefinition. Edge not added.");
							}
							return false;
						}
						break;
					default:
						return false;
				}
				break;
			default:
				if (Logger.logError()) {
					Logger.error("Unhandled situation found: edgeType=" + edgeType.toString() + " dependent="
					        + dependent.toString() + " target=" + target.toString());
				}
				return false;
		}
		
		if (target.getRevision().getChangeSet().getTimestamp()
		          .isAfter(dependent.getRevision().getChangeSet().getTimestamp())) {
			return false;
		}
		
		super.addEdge(dependent, target, edgeType);
		
		if (!dependent.getRevision().getChangeSet().getId().equals(target.getRevision().getChangeSet().getId())) {
			this.transactionGenealogy.addEdge(dependent.getRevision().getChangeSet(), target.getRevision()
			                                                                                .getChangeSet(), edgeType);
		}
		
		return true;
	}
	
	/**
	 * Adds a vertex to the genealogy that is associated with the specified JavaChangeOperation. This method also checks
	 * if such a vertex exists already.
	 * 
	 * @param v
	 *            the JavaChangeOperation to add
	 * @return true if the new vertex was successfully added. False otherwise (this may include that the vertex existed
	 *         already).
	 */
	@Override
	@NoneNull
	public boolean addVertex(@NotNull final JavaChangeOperation v) {
		if (!super.addVertex(v.getId(), v)) {
			return false;
		}
		return this.transactionGenealogy.addVertex(v.getRevision().getChangeSet());
	}
	
	/**
	 * Gets the transaction layer.
	 * 
	 * @return the transaction layer
	 */
	public ChangeGenealogy<ChangeSet> getChangeSetLayer() {
		return this.transactionGenealogy;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getCore()
	 */
	@Override
	public CoreChangeGenealogy getCore() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getNodeId(java.lang.Object)
	 */
	@Override
	public String getNodeId(final JavaChangeOperation t) {
		if (containsVertex(t)) {
			return String.valueOf(t.getId());
		}
		return null;
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	@Override
	protected JavaChangeOperation getVertexForNode(@NotNull final Vertex dependentNode) {
		final Long operationId = (Long) dependentNode.getProperty(ChangeGenealogy.NODE_ID);
		return this.nodeCache.loadById(operationId);
	}
}
