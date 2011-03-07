package de.unisaarland.cs.st.reposuite.ppa;

import org.hibernate.NonUniqueObjectException;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.persistence.PPAHibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeOperationPersister extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	public ChangeOperationPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings) {
		super(threadGroup, ChangeOperationPersister.class.getSimpleName(), settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void run() {
		
		try {
			HibernateUtil hibernateUtil = HibernateUtil.getInstance(false);
			
			if (!checkConnections()) {
				return;
			}
			
			if (!checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			hibernateUtil.beginTransaction();
			JavaChangeOperation currentOperation;
			String lastTransactionId = "";
			
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
					try {
						hibernateUtil.saveOrUpdate(currentOperation);
					} catch (NonUniqueObjectException e) {
						JavaElement element = PPAHibernateUtil.getSessionJavaElement(hibernateUtil, currentOperation
								.getChangedElementLocation().getElement());
						if (element != null) {
							currentOperation.getChangedElementLocation().setElement(element);
							try {
								hibernateUtil.saveOrUpdate(currentOperation);
							} catch (NonUniqueObjectException e1) {
								throw new UnrecoverableError(e1.getMessage(), e1);
							}
						} else {
							element = PPAHibernateUtil.getJavaElement(hibernateUtil, currentOperation
									.getChangedElementLocation().getElement());
							if (element != null) {
								currentOperation.getChangedElementLocation().setElement(element);
								try {
									hibernateUtil.saveOrUpdate(currentOperation);
								} catch (NonUniqueObjectException e1) {
									throw new UnrecoverableError(e1.getMessage(), e1);
								}
							} else {
								this.getThreadGroup().interrupt();
								throw new UnrecoverableError(e.getMessage(), e);
							}
						}
					}
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
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
}
