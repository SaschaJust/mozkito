/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * The {@link RepoSuiteTransformerThread} class is a component of the
 * {@link RepoSuiteToolchain}. It takes data from a data source and modifies it.
 * The result is stored as a new instance in the output storage.
 * {@link RepoSuiteTransformerThread}s have to have an input and an output
 * connector.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteTransformerThread<K, V> extends RepoSuiteThread<K, V> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public RepoSuiteTransformerThread(final RepoSuiteThreadGroup threadGroup, final String name,
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
		return true;
	}
	
}
