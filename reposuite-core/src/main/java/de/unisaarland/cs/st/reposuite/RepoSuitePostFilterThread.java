package de.unisaarland.cs.st.reposuite;


public interface RepoSuitePostFilterThread<T> extends RepoSuiteGeneralThread {
	
	public void connectInput(RepoSuitePostFilterThread<T> postFilterThread);
	
	public void connectInput(RepoSuiteTransformerThread<?, T> transformerThread);
	
	public void connectOutput(RepoSuitePostFilterThread<T> postFilterThread);
	
	public void connectOutput(RepoSuiteSinkThread<T> sinkThread);
	
	public T getNext();
}
