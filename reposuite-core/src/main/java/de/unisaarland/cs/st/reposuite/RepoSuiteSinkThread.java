/**
 * 
 */
package de.unisaarland.cs.st.reposuite;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface RepoSuiteSinkThread<T> extends RepoSuiteGeneralThread {
	
	public void connectInput(RepoSuitePostFilterThread<T> thread);
	
	public void connectInput(RepoSuiteTransformerThread<?, T> thread);
}
