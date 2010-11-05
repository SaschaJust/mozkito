package de.unisaarland.cs.st.reposuite;



public interface RepoSuiteGeneralThread {
	
	public boolean checkConnections();
	
	public boolean checkNotShutdown();
	
	public String getHandle();
	
	public boolean hasInputConnector();
	
	public boolean hasOutputConnector();
	
	public boolean isInputConnected();
	
	public boolean isOutputConnected();
	
	public boolean isShutdown();
	
	public void shutdown();

	public void wake();
}
