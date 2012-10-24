/**
 * 
 */
package org.mozkito.codeanalysis;

import java.util.concurrent.Semaphore;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPAPersister extends Sink<JavaChangeOperation> {
	
	protected static final Semaphore available = new Semaphore(1, true);
	
	private Integer                  i         = 0;
	
	public PPAPersister(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
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
				final JavaChangeOperation data = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + data);
				}
				
				try {
					available.acquire();
				} catch (final InterruptedException e) {
					available.release();
				}
				if ((++PPAPersister.this.i % 5000) == 0) {
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
