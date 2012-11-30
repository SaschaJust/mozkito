/**
 * 
 */
package org.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.Iterator;

import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class PPATransformer extends Transformer<RCSTransaction, JavaChangeOperation> {
	
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
						PPAPersister.available.acquire();
					} catch (final InterruptedException e) {
						PPAPersister.available.release();
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
					PPAPersister.available.release();
					
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
