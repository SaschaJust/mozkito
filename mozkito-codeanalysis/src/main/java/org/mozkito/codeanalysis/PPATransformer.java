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
package org.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.Iterator;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class PPATransformer.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PPATransformer extends Transformer<RCSTransaction, JavaChangeOperation> {
	
	/**
	 * Instantiates a new pPA transformer.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 * @param usePPA
	 *            the use ppa
	 * @param factory
	 *            the factory
	 * @param packageFilter
	 *            the package filter
	 */
	public PPATransformer(final Group threadGroup, final Settings settings, final Repository repository,
	        final Boolean usePPA, final JavaElementFactory factory, final String[] packageFilter) {
		super(threadGroup, settings, false);
		
		final PPATransformerVisitor visitor = new PPATransformerVisitor();
		new ProcessHook<RCSTransaction, JavaChangeOperation>(this) {
			
			private Iterator<JavaChangeOperation> iterator;
			
			@Override
			public void process() {
				
				if ((this.iterator == null) || (!this.iterator.hasNext())) {
					
					final RCSTransaction rCSTransaction = getInputData();
					
					if (Logger.logInfo()) {
						Logger.info("Computing change operations for transaction `" + rCSTransaction.getId() + "`");
					}
					
					try {
						PPAPersister.AVAILABLE.acquire();
					} catch (final InterruptedException e) {
						PPAPersister.AVAILABLE.release();
					}
					if (usePPA) {
						
						PPAUtils.generateChangeOperations(repository, rCSTransaction,
						                                  new HashSet<ChangeOperationVisitor>() {
							                                  
							                                  private static final long serialVersionUID = -6294280837922825955L;
							                                  
							                                  {
								                                  add(visitor);
							                                  }
						                                  }, factory, packageFilter);
					} else {
						PPAUtils.generateChangeOperationsNOPPA(repository, rCSTransaction,
						                                       new HashSet<ChangeOperationVisitor>() {
							                                       
							                                       private static final long serialVersionUID = -3888102603870272730L;
							                                       
							                                       {
								                                       add(visitor);
							                                       }
						                                       }, factory, packageFilter);
					}
					PPAPersister.AVAILABLE.release();
					
					this.iterator = visitor.getIterator();
				}
				
				if (this.iterator.hasNext()) {
					
					final JavaChangeOperation operation = this.iterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("providing JavaChangeOperation: " + operation.toString());
					}
					
					if (this.iterator.hasNext()) {
						providePartialOutputData(operation);
					} else {
						provideOutputData(operation);
					}
				} else {
					skipData();
				}
			}
			
		};
		
	}
	
}
