package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.OperationCollection;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class GenealogyNodePersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyNodePersister extends AndamaTransformer<OperationCollection, JavaChangeOperation> {
	
	private int counter = 0;
	
	/**
	 * Instantiates a new genealogy node persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param coreGenealogy
	 *            the core genealogy
	 */
	public GenealogyNodePersister(AndamaGroup threadGroup, AndamaSettings settings,
			final CoreChangeGenealogy coreGenealogy) {
		super(threadGroup, settings, false);
		
		new ProcessHook<OperationCollection, JavaChangeOperation>(this) {
			
			private JavaChangeOperationProcessQueue toWrite = new JavaChangeOperationProcessQueue();
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				
				
				if (toWrite.hasNext()) {
					providePartialOutputData(toWrite.next());
				} else {
					toWrite.clear();
					OperationCollection operationCollection = getInputData();
					
					Collection<JavaChangeOperation> changeOperations = operationCollection.unpack();
					Iterator<JavaChangeOperation> iterator = changeOperations.iterator();
					
					
					while (iterator.hasNext()) {
						
						if ((counter % 100) == 0) {
							if (Logger.logInfo()) {
								Logger.info("Added " + counter + " JavaChangeOperations to ChangeGenealogy.");
							}
						}
						++counter;
						
						JavaChangeOperation operation = iterator.next();
						if (Logger.logDebug()) {
							Logger.debug("Adding JavaChangeOperations `" + operation.getId() + "` to ChangeGenealogy.");
						}
						
						if (!coreGenealogy.addVertex(operation)) {
							if (Logger.logError()) {
								Logger.error("Adding JavaChangeOperations `" + operation.getId()
										+ "` to ChangeGenealogy FAILED!");
							}
						} else {
							toWrite.add(operation);
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Storing " + operation);
						}
					}
					if (!toWrite.hasNext()) {
						setCompleted();
					} else {
						JavaChangeOperation toOffer = toWrite.next();
						if (Logger.logDebug()) {
							Logger.debug("Offering node " + toOffer.toString() + " for dependency computation.");
						}
						providePartialOutputData(toOffer);
					}
				}
			}
		};
	}
}
