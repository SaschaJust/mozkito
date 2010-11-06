/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Collection;
import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteThread<K, V> extends Thread implements RepoSuiteGeneralThread<K, V> {
	
	protected boolean                                  shutdown;
	protected Collection<RepoSuiteGeneralThread<?, ?>> knownThreads  = new LinkedList<RepoSuiteGeneralThread<?, ?>>();
	protected RepoSuiteThreadGroup                     threadGroup;
	
	protected LinkedList<RepoSuiteGeneralThread<?, K>> inputThreads  = new LinkedList<RepoSuiteGeneralThread<?, K>>();
	protected LinkedList<RepoSuiteGeneralThread<V, ?>> outputThreads = new LinkedList<RepoSuiteGeneralThread<V, ?>>();
	
	protected RepoSuiteDataStorage<K>                  inputStorage;
	protected RepoSuiteDataStorage<V>                  outputStorage;
	protected final RepoSuiteSettings                  settings;
	
	public RepoSuiteThread(final RepoSuiteThreadGroup threadGroup, final String name, final RepoSuiteSettings settings) {
		super(threadGroup, name);
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.shutdown = false;
		this.settings = settings;
		this.inputStorage = new RepoSuiteDataStorage<K>(
		        ((Long) settings.getSetting("cache.size").getValue()).intValue());
		this.outputStorage = new RepoSuiteDataStorage<V>(
		        ((Long) settings.getSetting("cache.size").getValue()).intValue());
	}
	
	@Override
	public final boolean checkConnections() {
		boolean retval = true;
		if (!isInputConnected()) {
			if (Logger.logError()) {
				Logger.error(getHandle() + " is not input connected (required to run this task).");
			}
			retval = false;
		}
		
		if (!isOutputConnected()) {
			if (Logger.logError()) {
				Logger.error(getHandle() + " is not out connected (required to run this task).");
			}
			retval = false;
		}
		
		if (retval && this.knownThreads.isEmpty()) {
			if (Logger.logError()) {
				Logger.error(getHandle()
				        + " has known connections, but knownThreads is empty. This should never happen.");
			}
			retval = false;
		}
		
		if (!retval) {
			
			if (Logger.logError()) {
				Logger.error("Shutting all threads down.");
				this.threadGroup.shutdown();
			}
		}
		return retval;
	}
	
	@Override
	public final boolean checkNotShutdown() {
		if (isShutdown()) {
			
			if (Logger.logError()) {
				Logger.error("Thread already shut down. Won't run again.");
			}
		}
		return !isShutdown();
	}
	
	@Override
	public final boolean connectInput(final RepoSuiteGeneralThread<?, K> thread) {
		if (hasInputConnector()) {
			this.inputThreads.add(thread);
			this.knownThreads.add(thread);
			this.setInputStorage(thread.getOutputStorage());
			
			if (Logger.logInfo()) {
				Logger.info("[" + getHandle() + "] Linking input connector to [" + thread.getHandle() + "]");
			}
			
			if (thread.hasOutputConnector() && !thread.isOutputConnected(this)) {
				thread.connectOutput(this);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public final boolean connectOutput(final RepoSuiteGeneralThread<V, ?> thread) {
		if (hasOutputConnector()) {
			this.outputThreads.add(thread);
			this.knownThreads.add(thread);
			this.setOutputStorage(thread.getInputStorage());
			
			if (Logger.logInfo()) {
				Logger.info("[" + getHandle() + "] Linking output connector to [" + thread.getHandle() + "]");
			}
			
			if (thread.hasInputConnector() && !thread.isInputConnected(this)) {
				thread.connectInput(this);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void disconnectInput(final RepoSuiteGeneralThread<?, K> thread) {
		if (this.inputThreads.contains(thread)) {
			this.inputThreads.remove(thread);
			this.inputStorage.unregisterInput(thread);
		}
	}
	
	@Override
	public void disconnectOutput(final RepoSuiteGeneralThread<V, ?> thread) {
		if (this.outputThreads.contains(thread)) {
			this.outputThreads.remove(thread);
			this.outputStorage.unregisterOutput(thread);
		}
	}
	
	@Override
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public final RepoSuiteDataStorage<K> getInputStorage() {
		return this.inputStorage;
	}
	
	@Override
	public final RepoSuiteDataStorage<V> getOutputStorage() {
		return this.outputStorage;
	}
	
	@Override
	public final boolean isInputConnected() {
		return !hasInputConnector() || (this.inputThreads.size() > 0);
	}
	
	@Override
	public final boolean isInputConnected(final RepoSuiteGeneralThread<?, K> thread) {
		return this.inputThreads.contains(thread);
	}
	
	@Override
	public final boolean isOutputConnected() {
		return !hasOutputConnector() || (this.outputThreads.size() > 0);
	}
	
	@Override
	public final boolean isOutputConnected(final RepoSuiteGeneralThread<V, ?> thread) {
		return (this.outputThreads.contains(thread));
	}
	
	public final synchronized boolean isShutdown() {
		return this.shutdown;
	}
	
	protected final K read() throws InterruptedException {
		return this.inputStorage.read();
	}
	
	@Override
	public final void setInputStorage(final RepoSuiteDataStorage<K> storage) {
		this.inputStorage = storage;
		storage.registerOutput(this);
	}
	
	@Override
	public final void setOutputStorage(final RepoSuiteDataStorage<V> storage) {
		this.outputStorage = storage;
		storage.registerInput(this);
	}
	
	@Override
	public final void shutdown() {
		if (!this.shutdown) {
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			this.shutdown = true;
			
			for (RepoSuiteGeneralThread<V, ?> thread : this.outputThreads) {
				thread.disconnectInput(this);
			}
			
			for (RepoSuiteGeneralThread<?, K> thread : this.inputThreads) {
				thread.disconnectOutput(this);
			}
			
			for (RepoSuiteGeneralThread<?, ?> thread : this.knownThreads) {
				thread.shutdown();
			}
		}
	}
	
	// @Override
	// public synchronized void wake() {
	// notifyAll();
	// }
	
	protected final void write(final V data) throws InterruptedException {
		this.outputStorage.write(data);
	}
	
}
