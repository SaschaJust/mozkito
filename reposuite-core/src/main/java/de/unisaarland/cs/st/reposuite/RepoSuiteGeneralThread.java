package de.unisaarland.cs.st.reposuite;

public interface RepoSuiteGeneralThread<K, V> {
	
	public boolean checkConnections();
	
	public boolean checkNotShutdown();
	
	public boolean connectInput(RepoSuiteGeneralThread<?, K> thread);
	
	public boolean connectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
	public void disconnectInput(RepoSuiteGeneralThread<?, K> thread);
	
	public void disconnectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
	public String getHandle();
	
	public RepoSuiteDataStorage<K> getInputStorage();
	
	public RepoSuiteDataStorage<V> getOutputStorage();
	
	public boolean hasInputConnector();
	
	public boolean hasOutputConnector();
	
	public boolean isInputConnected();
	
	public boolean isInputConnected(RepoSuiteGeneralThread<?, K> thread);
	
	public boolean isOutputConnected();
	
	public boolean isOutputConnected(RepoSuiteGeneralThread<V, ?> thread);
	
	public boolean isShutdown();
	
	public void setInputStorage(final RepoSuiteDataStorage<K> storage);
	
	public void setOutputStorage(final RepoSuiteDataStorage<V> storage);
	
	public void shutdown();
	
	// public void wake();
}
