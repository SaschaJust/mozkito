/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteSinkThread<T> extends RepoSuiteThread<T, T> {
	
	public RepoSuiteSinkThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	@Override
	public boolean hasInputConnector() {
		return true;
	}
	
	@Override
	public boolean hasOutputConnector() {
		return false;
	}
	
}
