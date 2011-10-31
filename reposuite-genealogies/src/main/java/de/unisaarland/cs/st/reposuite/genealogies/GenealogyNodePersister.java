package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * The Class GenealogyNodePersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyNodePersister extends AndamaTransformer<Collection<JavaChangeOperation>, JavaChangeOperation> {
	
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
		
		new ProcessHook<Collection<JavaChangeOperation>, JavaChangeOperation>(this) {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				Collection<JavaChangeOperation> changeOperations = getInputData();
				
				Iterator<JavaChangeOperation> iterator = changeOperations.iterator();
				
				JavaChangeOperationProcessQueue toWrite = new JavaChangeOperationProcessQueue();
				
				while (iterator.hasNext()) {
					
					JavaChangeOperation operation = iterator.next();
					if (Logger.logInfo()) {
						Logger.info("Adding JavaChangeOperations `" + operation.getId() + "` to ChangeGenealogy.");
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
				
				while (toWrite.hasNext()) {
					providePartialOutputData(toWrite.next());
				}
				
				setCompleted();
			}
			
		};
		
	}
	
}
