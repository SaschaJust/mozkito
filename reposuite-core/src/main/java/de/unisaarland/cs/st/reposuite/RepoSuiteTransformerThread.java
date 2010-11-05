/**
 * 
 */
package de.unisaarland.cs.st.reposuite;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface RepoSuiteTransformerThread<K, M> extends RepoSuiteGeneralThread {
	
	public void connectInput(RepoSuitePreFilterThread<K> preFilterThread);
	
	public void connectInput(RepoSuiteSourceThread<K> sourceThread);
	
	public void connectOutput(RepoSuitePostFilterThread<M> postFilterThread);
	
	public void connectOutput(RepoSuiteSinkThread<M> sinkThread);
	
	public M getNext();
}
