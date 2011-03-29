package de.unisaarland.cs.st.reposuite.ppa;

import java.util.List;

import org.hibernate.Criteria;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementCache;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class ChangeOperationPersister.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationPersister extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	
	/**
	 * Instantiates a new change operation persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public ChangeOperationPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings) {
		super(threadGroup, ChangeOperationPersister.class.getSimpleName(), settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public void run() {
		
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		if (Logger.logInfo()) {
			Logger.info("Filling cache ... ");
		}
		HibernateUtil hibernateUtil;
		try {
			hibernateUtil = HibernateUtil.getInstance();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		hibernateUtil.beginTransaction();
		JavaChangeOperation currentOperation;
		String lastTransactionId = "";
		
		Criteria criteria = hibernateUtil.createCriteria(JavaClassDefinition.class);
		List<JavaClassDefinition> defs = criteria.list();
		for (JavaClassDefinition def : defs) {
			JavaElementCache.classDefs.put(def.getFullQualifiedName(), def);
		}
		criteria = hibernateUtil.createCriteria(JavaMethodDefinition.class);
		List<JavaMethodDefinition> mDefs = criteria.list();
		for (JavaMethodDefinition def : mDefs) {
			JavaElementCache.methodDefs.put(def.getFullQualifiedName(), def);
		}
		criteria = hibernateUtil.createCriteria(JavaMethodCall.class);
		List<JavaMethodCall> calls = criteria.list();
		for (JavaMethodCall call : calls) {
			JavaElementCache.methodCalls.put(call.getFullQualifiedName(), call);
		}
		
		if (Logger.logInfo()) {
			Logger.info("done. Notify all ... ");
		}
		
		synchronized (JavaElementCache.classDefs) {
			JavaElementCache.classDefs.notifyAll();
		}
		
		if (Logger.logInfo()) {
			Logger.info("done.");
		}
		
		try {
			while (!isShutdown() && ((currentOperation = read()) != null)) {
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + currentOperation);
				}
				
				String currentTransactionId = currentOperation.getRevision().getTransaction().getId();
				
				if (lastTransactionId.equals("")) {
					lastTransactionId = currentTransactionId;
				}
				if (!currentTransactionId.equals(lastTransactionId)) {
					hibernateUtil.commitTransaction();
					lastTransactionId = currentTransactionId;
					hibernateUtil.beginTransaction();
				}
				hibernateUtil.saveOrUpdate(currentOperation);
			}
			hibernateUtil.commitTransaction();
			if (Logger.logInfo()) {
				Logger.info("ChangeOperationPersister done. Terminating... ");
			}
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
