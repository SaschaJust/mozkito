/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * {@link RepoSuiteSinkThread}s are the end points of a tool chain. In general,
 * they provide a connection to a database back-end (e.g.
 * {@link RepositoryPersister}). There can also be void sinks in case you don't
 * want to store anything, e.g. if you just want to do some analysis.
 * {@link RepositoryVoidSink} is an example for this. All instances of
 * {@link RepoSuiteSinkThread} must have an input connector, but must not have
 * an output connector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteSinkThread<T> extends RepoSuiteThread<T, T> {
	
	/**
	 * @see RepoSuiteThread
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public RepoSuiteSinkThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasInputConnector()
	 */
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#hasOutputConnector
	 * ()
	 */
	@Override
	public boolean hasOutputConnector() {
		return false;
	}
	
}
