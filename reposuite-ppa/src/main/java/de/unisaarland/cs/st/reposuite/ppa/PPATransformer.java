/**
 * 
 */
package de.unisaarland.cs.st.reposuite.ppa;

import java.util.HashSet;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author just
 * 
 */
public class PPATransformer extends AndamaTransformer<RCSTransaction, JavaChangeOperation> {
	
	public PPATransformer(AndamaGroup threadGroup, AndamaSettings settings, final Repository repository,
	        final Boolean usePPA) {
		super(threadGroup, settings, false);
		
		final PPATransformerVisitor visitor = new PPATransformerVisitor();
		
		new ProcessHook<RCSTransaction, JavaChangeOperation>(this) {
			
			@Override
			public void process() {
				RCSTransaction transaction = getInputData();
				
				if (Logger.logInfo()) {
					Logger.info("Computing change operations for transaction `" + transaction.getId() + "`");
				}
				
				if (usePPA) {
					PPAUtils.generateChangeOperations(repository, transaction, new HashSet<ChangeOperationVisitor>() {
						
						private static final long serialVersionUID = -6294280837922825955L;
						
						{
							add(visitor);
						}
					});
				} else {
					PPAUtils.generateChangeOperationsNOPPA(repository, transaction,
					        new HashSet<ChangeOperationVisitor>() {
						        
						        private static final long serialVersionUID = -3888102603870272730L;
						        
						        {
							        add(visitor);
						        }
					        });
				}
				
				Iterator<JavaChangeOperation> iterator = visitor.getIterator();
				
				while (iterator.hasNext()) {
					
					JavaChangeOperation operation = iterator.next();
					providePartialOutputData(operation);
				}
				
				setCompleted();
			}
			
		};
		
	}
	
}
