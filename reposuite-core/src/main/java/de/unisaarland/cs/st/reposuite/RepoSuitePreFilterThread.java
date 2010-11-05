/**
 * 
 */
package de.unisaarland.cs.st.reposuite;



/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface RepoSuitePreFilterThread<T> extends RepoSuiteGeneralThread {
	
	public void connectInput(RepoSuitePreFilterThread<T> preFilterThread);
	
	public void connectInput(RepoSuiteSourceThread<T> sourceThread);
	
	public void connectOutput(RepoSuitePreFilterThread<T> preFilterThread);
	
	public void connectOutput(RepoSuiteTransformerThread<T, ?> transformerThread);
	
	public T getNext();
	
}
