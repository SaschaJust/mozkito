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

package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
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
public class GenealogyNodePersister extends Transformer<OperationCollection, JavaChangeOperationProcessQueue> {
	
	private int counter        = 0;
	private int packageCounter = 0;
	
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
	public GenealogyNodePersister(final Group threadGroup, final Settings settings,
	        final CoreChangeGenealogy coreGenealogy) {
		super(threadGroup, settings, false);
		
		new ProcessHook<OperationCollection, JavaChangeOperationProcessQueue>(this) {
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				
				final OperationCollection operationCollection = getInputData();
				int localCounter = 0;
				
				final Collection<JavaChangeOperation> changeOperations = operationCollection.unpack();
				
				if (Logger.logTrace()) {
					Logger.trace("GOT INPUT: "
					        + StringUtils.join(changeOperations.toArray(new JavaChangeOperation[changeOperations.size()])));
				}
				
				final Iterator<JavaChangeOperation> iterator = changeOperations.iterator();
				
				final JavaChangeOperationProcessQueue toWrite = new JavaChangeOperationProcessQueue();
				
				while (iterator.hasNext()) {
					
					final JavaChangeOperation operation = iterator.next();
					
					if (!coreGenealogy.addVertex(operation)) {
						if (Logger.logError()) {
							Logger.error("Adding JavaChangeOperations `" + operation.getId()
							        + "` to ChangeGenealogy FAILED!");
						}
					} else {
						if (toWrite.add(operation)) {
							if (Logger.logDebug()) {
								Logger.debug("Added JavaChangeOperation `" + operation.getId()
								        + "` to ChangeGenealogy.");
							}
							++localCounter;
						}
					}
					
				}
				GenealogyNodePersister.this.counter += localCounter;
				if (Logger.logDebug()) {
					Logger.debug("Send package " + toWrite.toString() + " with " + localCounter + " elements.");
				}
				provideOutputData(toWrite);
				++GenealogyNodePersister.this.packageCounter;
			}
		};
		
		new PostExecutionHook<OperationCollection, JavaChangeOperationProcessQueue>(this) {
			
			@Override
			public void postExecution() {
				if (Logger.logInfo()) {
					Logger.info("Sent " + GenealogyNodePersister.this.packageCounter + " output data objects.");
					Logger.info("Added " + GenealogyNodePersister.this.counter
					        + " JavaChangeOperations to ChangeGenealogy");
				}
			}
			
		};
		
	}
}
