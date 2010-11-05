/**
 * 
 */
package de.unisaarland.cs.st.reposuite;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public interface RepoSuiteSourceThread<T> extends RepoSuiteGeneralThread {
	
	public void connectOutput(RepoSuitePreFilterThread<T> preFilterThread);
	
	public void connectOutput(RepoSuiteTransformerThread<T, ?> transformerThread);
	
	public T getNext();
}
