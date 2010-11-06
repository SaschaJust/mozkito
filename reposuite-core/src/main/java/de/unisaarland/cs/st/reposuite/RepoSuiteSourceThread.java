/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteSourceThread<T> extends RepoSuiteThread<T, T> {
	
	public RepoSuiteSourceThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	@Override
	public boolean hasInputConnector() {
		return false;
	}
	
	@Override
	public boolean hasOutputConnector() {
		return true;
	}
	
}
