package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.OperationCollection;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class GenealogyNodePersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyNodePersister extends AndamaTransformer<OperationCollection, JavaChangeOperationProcessQueue> {
	
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
		
		new ProcessHook<OperationCollection, JavaChangeOperationProcessQueue>(this) {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				
				
				OperationCollection operationCollection = getInputData();
				
				Collection<JavaChangeOperation> changeOperations = operationCollection.unpack();
				
				if (Logger.logTrace()) {
					Logger.trace("GOT INPUT: "
							+ StringUtils.join(changeOperations
									.toArray(new JavaChangeOperation[changeOperations
									                                 .size()])));
				}
				
				Iterator<JavaChangeOperation> iterator = changeOperations.iterator();
				
				JavaChangeOperationProcessQueue toWrite = new JavaChangeOperationProcessQueue();
				
				while (iterator.hasNext()) {
					
					JavaChangeOperation operation = iterator.next();
					
					if (!coreGenealogy.addVertex(operation)) {
						if (Logger.logError()) {
							Logger.error("Adding JavaChangeOperations `" + operation.getId()
									+ "` to ChangeGenealogy FAILED!");
						}
					} else {
						toWrite.add(operation);
						if (Logger.logDebug()) {
							Logger.debug("Adding JavaChangeOperations `" + operation.getId() + "` to ChangeGenealogy.");
						}
						++counter;
					}
					
					
				}
				provideOutputData(toWrite);
			}
		};
		
		new PostExecutionHook<OperationCollection, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void postExecution() {
				if (Logger.logInfo()) {
					Logger.info("Added " + counter + " JavaChangeOperations to ChnageGenealogy");
				}
			}
			
		};
		
	}
}
