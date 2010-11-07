package de.unisaarland.cs.st.reposuite;

public interface RepoSuiteGeneralThread<K, V> {
	
	/**
	 * @return
	 */
	public boolean checkConnections();
	
	/**
	 * @return
	 */
	public boolean checkNotShutdown();
	
	/**
	 * @param thread
	 * @return
	 */
	public boolean connectInput(RepoSuiteGeneralThread<?, K> thread);
	
	/**
	 * @param thread
	 * @return
	 */
	public boolean connectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
	/**
	 * @param thread
	 */
	public void disconnectInput(RepoSuiteGeneralThread<?, K> thread);
	
	/**
	 * @param thread
	 */
	public void disconnectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
	/**
	 * @return
	 */
	public String getHandle();
	
	/**
	 * @return
	 */
	public RepoSuiteDataStorage<K> getInputStorage();
	
	/**
	 * @return
	 */
	public RepoSuiteDataStorage<V> getOutputStorage();
	
	/**
	 * @return
	 */
	public boolean hasInputConnector();
	
	/**
	 * @return
	 */
	public boolean hasOutputConnector();
	
	/**
	 * @return
	 */
	public boolean isInputConnected();
	
	/**
	 * @param thread
	 * @return
	 */
	public boolean isInputConnected(RepoSuiteGeneralThread<?, K> thread);
	
	/**
	 * @return
	 */
	public boolean isOutputConnected();
	
	/**
	 * @param thread
	 * @return
	 */
	public boolean isOutputConnected(RepoSuiteGeneralThread<V, ?> thread);
	
	/**
	 * @return
	 */
	public boolean isShutdown();
	
	/**
	 * @param storage
	 */
	public void setInputStorage(final RepoSuiteDataStorage<K> storage);
	
	/**
	 * @param storage
	 */
	public void setOutputStorage(final RepoSuiteDataStorage<V> storage);
	
	/**
	 * 
	 */
	public void shutdown();
}
