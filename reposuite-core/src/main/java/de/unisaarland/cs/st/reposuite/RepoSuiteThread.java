/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Collection;
import java.util.LinkedList;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * {@link RepoSuiteThread}s are the edges of a {@link RepoSuiteToolchain} graph,
 * connecting the {@link RepoSuiteDataStorage} nodes. An example for such a
 * toolchain would look like this: {@link RepositoryReader} &rarr;
 * {@link RepositoryAnalyzer} &rarr; {@link RepositoryParser} &rarr;
 * {@link RepositoryPersister}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
public abstract class RepoSuiteThread<K, V> extends Thread implements RepoSuiteGeneralThread<K, V> {
	
	private boolean                                        shutdown;
	private final Collection<RepoSuiteGeneralThread<?, ?>> knownThreads  = new LinkedList<RepoSuiteGeneralThread<?, ?>>();
	private final RepoSuiteThreadGroup                     threadGroup;
	
	private final LinkedList<RepoSuiteGeneralThread<?, K>> inputThreads  = new LinkedList<RepoSuiteGeneralThread<?, K>>();
	private final LinkedList<RepoSuiteGeneralThread<V, ?>> outputThreads = new LinkedList<RepoSuiteGeneralThread<V, ?>>();
	
	private RepoSuiteDataStorage<K>                        inputStorage;
	private RepoSuiteDataStorage<V>                        outputStorage;
	private final RepoSuiteSettings                        settings;
	
	/**
	 * The constructor of the {@link RepoSuiteThread}. This should be called
	 * from all extending classes.
	 * 
	 * @param threadGroup
	 *            the {@link RepoSuiteThreadGroup}. See
	 *            {@link RepoSuiteThreadGroup} for details.
	 * @param name
	 *            the name of the {@link RepoSuiteThreadGroup}. See
	 *            {@link RepoSuiteThreadGroup} for details.
	 * @param settings
	 *            An instance of RepoSuiteSettings
	 */
	public RepoSuiteThread(final RepoSuiteThreadGroup threadGroup, final String name, final RepoSuiteSettings settings) {
		super(threadGroup, name);
		assert (threadGroup != null);
		assert (name != null);
		assert (settings != null);
		
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.settings = settings;
		RepoSuiteArgument setting = settings.getSetting("cache.size");
		
		if (hasInputConnector()) {
			if (setting != null) {
				this.inputStorage = new RepoSuiteDataStorage<K>(((Long) setting.getValue()).intValue());
			} else {
				this.inputStorage = new RepoSuiteDataStorage<K>();
			}
		}
		
		if (hasOutputConnector()) {
			if (setting != null) {
				this.outputStorage = new RepoSuiteDataStorage<V>(((Long) setting.getValue()).intValue());
			} else {
				this.outputStorage = new RepoSuiteDataStorage<V>();
			}
		}
		setShutdown(false);
		
		assert (hasInputConnector() == (this.inputStorage != null));
		assert (hasOutputConnector() == (this.outputStorage != null));
		assert (!this.shutdown);
		assert (settings != null);
		assert (threadGroup != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#checkConnections()
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#checkNotShutdown()
	 */
	@Override
	public final boolean checkNotShutdown() {
		if (isShutdown()) {
			
			if (Logger.logWarn()) {
				Logger.warn("Thread already shut down. Won't run again.");
			}
		}
		return !isShutdown();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#connectInput(de
	 * .unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean connectInput(final RepoSuiteGeneralThread<?, K> thread) {
		assert (thread != null);
		
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#connectOutput(de
	 * .unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean connectOutput(final RepoSuiteGeneralThread<V, ?> thread) {
		assert (thread != null);
		
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#disconnectInput
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final void disconnectInput(final RepoSuiteGeneralThread<?, K> thread) {
		assert (thread != null);
		
		if (this.inputThreads.contains(thread)) {
			this.inputThreads.remove(thread);
			this.inputStorage.unregisterInput(thread);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#disconnectOutput
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final void disconnectOutput(final RepoSuiteGeneralThread<V, ?> thread) {
		assert (thread != null);
		
		if (this.outputThreads.contains(thread)) {
			this.outputThreads.remove(thread);
			this.outputStorage.unregisterOutput(thread);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getHandle()
	 */
	@Override
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getInputStorage()
	 */
	@Override
	public final RepoSuiteDataStorage<K> getInputStorage() {
		return this.inputStorage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getOutputStorage()
	 */
	@Override
	public final RepoSuiteDataStorage<V> getOutputStorage() {
		return this.outputStorage;
	}
	
	/**
	 * @return the settings
	 */
	protected final RepoSuiteSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * Requests the current size of the input storage. Make sure there is a
	 * valid input storage, i.e. you are using an implementation where
	 * {@link RepoSuiteThread#hasInputConnector()} is true.
	 * 
	 * @return the size of the input storage
	 */
	protected final int inputSize() {
		assert (this.inputStorage != null);
		assert (this.inputStorage != null);
		assert (hasInputConnector());
		
		return this.inputStorage.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isInputConnected()
	 */
	@Override
	public final boolean isInputConnected() {
		return !hasInputConnector() || (this.inputThreads.size() > 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isInputConnected
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean isInputConnected(final RepoSuiteGeneralThread<?, K> thread) {
		return this.inputThreads.contains(thread);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isOutputConnected()
	 */
	@Override
	public final boolean isOutputConnected() {
		return !hasOutputConnector() || (this.outputThreads.size() > 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isOutputConnected
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean isOutputConnected(final RepoSuiteGeneralThread<V, ?> thread) {
		return (this.outputThreads.contains(thread));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isShutdown()
	 */
	public final synchronized boolean isShutdown() {
		return this.shutdown;
	}
	
	/**
	 * Requests the current size of the output storage. Make sure there is a
	 * valid output storage, i.e. you are using an implementation where
	 * {@link RepoSuiteThread#hasOutputConnector()} is true.
	 * 
	 * @return the size of the output storage
	 */
	protected final int outputSize() {
		assert (this.outputStorage != null);
		assert (this.outputStorage != null);
		assert (hasOutputConnector());
		
		return this.outputStorage.size();
	}
	
	/**
	 * @return the next chunk from the inputStorage. Will be null if there isn't
	 *         any input left and no writers are attached to the storage
	 *         anymore.
	 * @throws InterruptedException
	 */
	protected final K read() throws InterruptedException {
		return this.inputStorage.read();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setInputStorage
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteDataStorage)
	 */
	@Override
	public final void setInputStorage(final RepoSuiteDataStorage<K> storage) {
		assert (storage != null);
		
		if (hasInputConnector()) {
			this.inputStorage = storage;
			storage.registerOutput(this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setOutputStorage
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteDataStorage)
	 */
	@Override
	public final void setOutputStorage(final RepoSuiteDataStorage<V> storage) {
		assert (storage != null);
		
		if (hasOutputConnector()) {
			this.outputStorage = storage;
			storage.registerInput(this);
		}
	}
	
	/**
	 * Set the current shutdown status. This method only sets the variable. Call
	 * {@link RepoSuiteThread#shutdown()} to initiate a proper shutdown.
	 * 
	 * @param shutdown
	 */
	private void setShutdown(final boolean shutdown) {
		this.shutdown = shutdown;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#shutdown()
	 */
	@Override
	public final void shutdown() {
		if (!isShutdown()) {
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			setShutdown(true);
			
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepoSuiteThread [Class=" + getHandle() + ", threadGroup=" + this.threadGroup.getName() + "]";
	}
	
	/**
	 * Writes a chunk to the output storage. Make sure to call this only if
	 * {@link RepoSuiteThread#hasOutputConnector()} is true.
	 * 
	 * @param data
	 *            a chunk of data, not null
	 * @throws InterruptedException
	 */
	protected final void write(final V data) throws InterruptedException {
		assert (data != null);
		assert (this.outputStorage != null);
		assert (hasOutputConnector());
		
		this.outputStorage.write(data);
	}
	
}
