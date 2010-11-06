/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteFilterThread<T> extends RepoSuiteThread<T, T> {
	
	/**
	 * @param threadGroup
	 * @param name
	 */
	public RepoSuiteFilterThread(final RepoSuiteThreadGroup threadGroup, final String name,
	        final RepoSuiteSettings settings) {
		super(threadGroup, name, settings);
	}
	
	@Override
	public final boolean hasInputConnector() {
		return true;
	}
	
	@Override
	public final boolean hasOutputConnector() {
		return true;
	}
}
