/**
 * 
 */
package de.unisaarland.cs.st.reposuite.ppa;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * @author just
 * 
 */
public class PPAPersister extends AndamaSink<JavaChangeOperation> {
	
	private Integer i = 0;
	
	public PPAPersister(AndamaGroup threadGroup, AndamaSettings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void process() {
				JavaChangeOperation data = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + data);
				}
				
				if ((++i % 50) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				persistenceUtil.save(data);
			}
		};
		
		new PostExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				persistenceUtil.shutdown();
			}
		};
	}
	
}
