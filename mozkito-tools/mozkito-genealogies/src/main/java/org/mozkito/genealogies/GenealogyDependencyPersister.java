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

package org.mozkito.genealogies;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.genealogies.core.JavaChangeOperationProcessQueue;
import org.mozkito.genealogies.utils.JavaMethodRegistry;
import org.mozkito.versions.elements.ChangeType;

/**
 * The Class GenealogyDependencyPersister.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GenealogyDependencyPersister extends Sink<JavaChangeOperationProcessQueue> {
	
	/** The registry. */
	private final JavaMethodRegistry  registry;
	
	/** The genealogy. */
	private final CoreChangeGenealogy genealogy;
	
	/** The counter. */
	private int                       counter        = 0;
	
	/** The dep counter. */
	private int                       depCounter     = 0;
	
	/** The package counter. */
	private int                       packageCounter = 0;
	
	/**
	 * Instantiates a new genealogy dependency persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param coreGenealogy
	 *            the core genealogy
	 */
	public GenealogyDependencyPersister(final Group threadGroup, final Settings settings,
	        final CoreChangeGenealogy coreGenealogy) {
		super(threadGroup, settings, false);
		
		this.genealogy = coreGenealogy;
		this.registry = new JavaMethodRegistry(this.genealogy);
		
		new ProcessHook<JavaChangeOperationProcessQueue, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void process() {
				final JavaChangeOperationProcessQueue operationQueue = getInputData();
				++GenealogyDependencyPersister.this.packageCounter;
				int localCounter = 0;
				
				while (operationQueue.hasNext()) {
					
					final JavaChangeOperation operation = operationQueue.next();
					++localCounter;
					
					final JavaElementLocation location = operation.getChangedElementLocation();
					final JavaElement element = location.getElement();
					
					if (element.getPackageName().contains("UNKNOWN")) {
						continue;
					}
					
					switch (operation.getChangeType()) {
						case Deleted:
							if (element instanceof JavaMethodDefinition) {
								// find the previous operation that added the same method definition
								final JavaChangeOperation previousDefinition = GenealogyDependencyPersister.this.registry.removeDefiniton(operation);
								
								if (previousDefinition == null) {
									if (Logger.logWarn()) {
										Logger.warn("WARNING! Cannot find the JavaChangeOperation that added `"
										        + element.getFullQualifiedName()
										        + "` when adding JavaMethodDefinitionDeletion.");
									}
								} else {
									GenealogyDependencyPersister.this.genealogy.addEdge(operation,
									                                                    previousDefinition,
									                                                    GenealogyEdgeType.DeletedDefinitionOnDefinition);
									++GenealogyDependencyPersister.this.depCounter;
								}
							} else if (element instanceof JavaMethodCall) {
								
								// find the previous call that was added by this operation
								final JavaChangeOperation deletedCall = GenealogyDependencyPersister.this.registry.removeMethodCall(operation);
								if (deletedCall == null) {
									if (Logger.logWarn()) {
										Logger.warn("WARNING! Could not find add operation that added method call `"
										        + element.getFullQualifiedName() + "` in " + location.getFilePath());
									}
								} else {
									GenealogyDependencyPersister.this.genealogy.addEdge(operation,
									                                                    deletedCall,
									                                                    GenealogyEdgeType.DeletedCallOnCall);
									++GenealogyDependencyPersister.this.depCounter;
								}
								
								// check if the corresponding method definition was deleted too.
								// if (!GenealogyDependencyPersister.this.registry.existsDefinition(element, false)) {
								// final JavaChangeOperation previousDefinitionDeletion =
								// GenealogyDependencyPersister.this.registry.findPreviousDefinitionDeletion(element);
								// if (previousDefinitionDeletion != null) {
								// if (operation.isBefore(previousDefinitionDeletion)) {
								// if (Logger.logError()) {
								// Logger.error("Fatal error occured. Found previous method definition deletion that was deleted after the current operation: current operation="
								// + operation
								// + ", previous definition="
								// + previousDefinitionDeletion);
								// }
								// break;
								// }
								// GenealogyDependencyPersister.this.genealogy.addEdge(operation,
								// previousDefinitionDeletion,
								// GenealogyEdgeType.DeletedCallOnDeletedDefinition);
								// ++GenealogyDependencyPersister.this.depCounter;
								// }
								// }
							}
							break;
						case Modified:
						case Added:
							if (element instanceof JavaMethodDefinition) {
								GenealogyDependencyPersister.this.registry.addMethodDefiniton(operation);
								
								final JavaChangeOperation previousDefinition = GenealogyDependencyPersister.this.registry.findPreviousDefinition(element,
								                                                                                                                 true);
								
								if (previousDefinition != null) {
									if (previousDefinition.getChangeType().equals(ChangeType.Deleted)) {
										GenealogyDependencyPersister.this.genealogy.addEdge(operation,
										                                                    previousDefinition,
										                                                    GenealogyEdgeType.DefinitionOnDeletedDefinition);
										++GenealogyDependencyPersister.this.depCounter;
									} else {
										GenealogyDependencyPersister.this.genealogy.addEdge(operation,
										                                                    previousDefinition,
										                                                    GenealogyEdgeType.ModifiedDefinitionOnDefinition);
										++GenealogyDependencyPersister.this.depCounter;
									}
								}
							} else if (element instanceof JavaMethodCall) {
								GenealogyDependencyPersister.this.registry.addCall(operation);
								
								final JavaChangeOperation previousDefinition = GenealogyDependencyPersister.this.registry.findPreviousDefinition(element,
								                                                                                                                 false);
								if (previousDefinition != null) {
									GenealogyDependencyPersister.this.genealogy.addEdge(operation,
									                                                    previousDefinition,
									                                                    GenealogyEdgeType.CallOnDefinition);
									++GenealogyDependencyPersister.this.depCounter;
								}
								
							}
							break;
						default:
							break;
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Added dependencies for " + operation);
					}
				}
				
				if (Logger.logDebug()) {
					Logger.debug("Received package " + operationQueue.toString() + " with " + localCounter
					        + " elements.");
				}
				GenealogyDependencyPersister.this.counter += localCounter;
			}
		};
		
		new PostExecutionHook<JavaChangeOperationProcessQueue, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void postExecution() {
				if (Logger.logInfo()) {
					Logger.info("Received " + GenealogyDependencyPersister.this.packageCounter + " input data objects.");
					Logger.info("Added dependencies for " + GenealogyDependencyPersister.this.counter
					        + " JavaChangeOperations.");
					Logger.info("Added a total of " + GenealogyDependencyPersister.this.depCounter
					        + " dependencies to ChangeGenealogies.");
				}
			}
			
		};
		
	}
	
}
