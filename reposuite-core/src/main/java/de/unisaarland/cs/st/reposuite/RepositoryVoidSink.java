/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class RepositoryVoidSink extends RepoSuiteThread implements RepoSuiteSinkThread<RCSTransaction> {
	
	private RepoSuitePostFilterThread<RCSTransaction>     inPostFilter;
	private RepoSuiteTransformerThread<?, RCSTransaction> inTransformer;
	
	/**
	 * @param threadGroup
	 * @param name
	 */
	public RepositoryVoidSink(final RepoSuiteThreadGroup threadGroup) {
		super(threadGroup, RepositoryVoidSink.class.getSimpleName());
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
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector()
	 */
	@Override
	public boolean hasOutputConnector() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isInputConnected()
	 */
	@Override
	public boolean isInputConnected() {
		return (this.inPostFilter != null) || (this.inTransformer != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isOutputConnected()
	 */
	@Override
	public boolean isOutputConnected() {
		return true;
	}
	
	@Override
	public void run() {
		if (!checkConnections()) {
			return;
		}
		
		if (!checkNotShutdown()) {
			return;
		}
		
		RCSTransaction currentTransaction;
		while (!isShutdown()
				&& ((currentTransaction = (this.inPostFilter != null ? this.inPostFilter.getNext() : this.inTransformer
						.getNext())) != null)) {
			
			if (Logger.logDebug()) {
				Logger.debug("Taking " + currentTransaction + " from input connector and forgetting in.");
			}
			
		}
	}
	
}
