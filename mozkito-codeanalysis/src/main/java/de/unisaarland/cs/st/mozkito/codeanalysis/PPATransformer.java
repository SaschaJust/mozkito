/**
 * 
 */
package de.unisaarland.cs.st.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.Iterator;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaElementFactory;
import de.unisaarland.cs.st.mozkito.codeanalysis.utils.PPAUtils;
import de.unisaarland.cs.st.mozkito.versions.Repository;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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
					
					final RCSTransaction transaction = getInputData();
					
					if (Logger.logInfo()) {
						Logger.info("Computing change operations for transaction `" + transaction.getId() + "`");
					}
					
					try {
						PPAPersister.available.acquire();
					} catch (final InterruptedException e) {
						PPAPersister.available.release();
					}
					if (usePPA) {
						
						PPAUtils.generateChangeOperations(repository, transaction,
						                                  new HashSet<ChangeOperationVisitor>() {
							                                  
							                                  private static final long serialVersionUID = -6294280837922825955L;
							                                  
							                                  {
								                                  add(visitor);
							                                  }
						                                  }, factory, packageFilter);
					} else {
						PPAUtils.generateChangeOperationsNOPPA(repository, transaction,
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
