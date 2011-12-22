package de.unisaarland.cs.st.moskito.genealogies;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.utils.JavaMethodRegistry;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;

/**
 * The Class GenealogyDependencyPersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyDependencyPersister extends AndamaSink<JavaChangeOperationProcessQueue> {
	
	private JavaMethodRegistry  registry;
	private CoreChangeGenealogy genealogy;
	private int                 counter        = 0;
	private int                 depCounter = 0;
	private int                 packageCounter = 0;
	
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
	public GenealogyDependencyPersister(AndamaGroup threadGroup, AndamaSettings settings,
			CoreChangeGenealogy coreGenealogy) {
		super(threadGroup, settings, false);
		
		genealogy = coreGenealogy;
		registry = new JavaMethodRegistry(genealogy);
		
		new ProcessHook<JavaChangeOperationProcessQueue, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void process() {
				JavaChangeOperationProcessQueue operationQueue = getInputData();
				++packageCounter;
				
				while (operationQueue.hasNext()) {
					
					JavaChangeOperation operation = operationQueue.next();
					++counter;
					
					JavaElementLocation location = operation.getChangedElementLocation();
					JavaElement element = location.getElement();
					
					switch (operation.getChangeType()) {
						case Deleted:
							if (element instanceof JavaMethodDefinition) {
								//find the previous operation that added the same method definition
								JavaChangeOperation previousDefinition = registry.removeDefiniton(operation);
								if (previousDefinition == null) {
									if (Logger.logWarn()) {
										Logger.warn("WARNING! Cannot find the JavaChangeOperation that added `"
												+ element.getFullQualifiedName()
												+ "` when adding JavaMethodDefinitionDeletion.");
									}
								} else {
									genealogy.addEdge(operation, previousDefinition,
											GenealogyEdgeType.DeletedDefinitionOnDefinition);
									++depCounter;
								}
							} else if (element instanceof JavaMethodCall) {
								
								//find the previous call that was added by this operation
								JavaChangeOperation deletedCall = registry.removeMethodCall(operation);
								if (deletedCall == null) {
									if (Logger.logWarn()) {
										Logger.warn("WARNING! Could not find add operation that added method call `"
												+ element.getFullQualifiedName() + "` in " + location.getFilePath());
									}
								} else {
									genealogy.addEdge(operation, deletedCall, GenealogyEdgeType.DeletedCallOnCall);
									++depCounter;
								}
								
								//check if the corresponding method definition was added too.
								if (!registry.existsDefinition(element, false)) {
									JavaChangeOperation previousDefinitionDeletion = registry
											.findPreviousDefinitionDeletion(element);
									if (previousDefinitionDeletion != null) {
										genealogy.addEdge(operation, previousDefinitionDeletion,
												GenealogyEdgeType.DeletedCallOnDeletedDefinition);
										++depCounter;
									}
								}
							}
							break;
						case Modified:
						case Added:
							if (element instanceof JavaMethodDefinition) {
								registry.addMethodDefiniton(operation);
								
								JavaChangeOperation previousDefinition = registry.findPreviousDefinition(element, true);
								if (previousDefinition != null) {
									if (previousDefinition.getChangeType().equals(ChangeType.Deleted)) {
										genealogy.addEdge(operation, previousDefinition,
												GenealogyEdgeType.DefinitionOnDeletedDefinition);
										++depCounter;
									} else {
										genealogy.addEdge(operation, previousDefinition,
												GenealogyEdgeType.DefinitionOnDefinition);
										++depCounter;
									}
								}
							} else if (element instanceof JavaMethodCall) {
								registry.addCall(operation);
								
								JavaChangeOperation previousDefinition = registry
										.findPreviousDefinition(element, false);
								if (previousDefinition != null) {
									genealogy
									.addEdge(operation, previousDefinition, GenealogyEdgeType.CallOnDefinition);
									++depCounter;
								}
								
							}
							break;
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Added dependencies for " + operation);
					}
				}
			}
		};
		
		new PostExecutionHook<JavaChangeOperationProcessQueue, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void postExecution() {
				if (Logger.logInfo()) {
					Logger.info("Received " + packageCounter + " input data objects.");
					Logger.info("Added dependencies for " + counter + " JavaChangeOperations.");
					Logger.info("Added a total of " + depCounter + " dependencies to ChangeGenealogies.");
				}
			}
			
		};
		
	}
	
}
