/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.concurrent.Semaphore;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * @author just
 * 
 */
public class PPAPersister extends AndamaSink<JavaChangeOperation> {
	
	protected static final Semaphore available = new Semaphore(1, true);
	
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
				
				try {
					available.acquire();
				} catch (InterruptedException e) {
					available.release();
				}
				if ((++i % 5000) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				persistenceUtil.save(data);
				available.release();
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
