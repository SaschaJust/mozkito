/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryPersister extends RepoSuiteThread implements RepoSuiteSinkThread<RCSTransaction> {
	
	
	private final HibernateUtil    hibernateUtil;
	
	private RepoSuitePostFilterThread<RCSTransaction>     inPostFilter;
	
	private RepoSuiteTransformerThread<?, RCSTransaction> inTransformer;
	
	@SuppressWarnings ("unused")
	private final RepoSuiteSettings                       settings;
	
	public RepositoryPersister(final RepoSuiteThreadGroup threadGroup, final HibernateUtil hibernateUtil,
			final RepoSuiteSettings settings) {
		super(threadGroup, RepositoryPersister.class.getSimpleName());
		this.hibernateUtil = hibernateUtil;
		this.settings = settings;
	}
	
	@Override
	public void connectInput(final RepoSuitePostFilterThread<RCSTransaction> postFilterThread) {
		this.inPostFilter = postFilterThread;
		this.knownThreads.add(postFilterThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking input connector to: " + postFilterThread.getHandle());
		}
		
		if (postFilterThread.hasOutputConnector() && !postFilterThread.isOutputConnected()) {
			postFilterThread.connectOutput(this);
		}
	}
	
	@Override
	public void connectInput(final RepoSuiteTransformerThread<?, RCSTransaction> transformerThread) {
		this.inTransformer = transformerThread;
		this.knownThreads.add(transformerThread);
		
		if (Logger.logInfo()) {
			Logger.info("[" + getHandle() + "] Linking input connector to: " + transformerThread.getHandle());
		}
		
		if (transformerThread.hasOutputConnector() && !transformerThread.isOutputConnected()) {
			transformerThread.connectOutput(this);
		}
	}
	
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	@Override
	public boolean hasOutputConnector() {
		return false;
	}
	
	@Override
	public boolean isInputConnected() {
		return (this.inPostFilter != null) || (this.inTransformer != null);
	}
	
	@Override
	public boolean isOutputConnected() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
		this.hibernateUtil.beginTransaction();
		RCSTransaction currentTransaction;
		int i = 0;
		while (!isShutdown()
				&& ((currentTransaction = (this.inPostFilter != null ? this.inPostFilter.getNext()
						: this.inTransformer.getNext())) != null)) {
			
			if (Logger.logTrace()) {
				Logger.trace("Saving " + currentTransaction);
			}
			
			if (++i % 1000 == 0) {
				this.hibernateUtil.commitTransaction();
				this.hibernateUtil.beginTransaction();
			}
			this.hibernateUtil.saveOrUpdate(currentTransaction);
		}
		this.hibernateUtil.commitTransaction();
	}
}
