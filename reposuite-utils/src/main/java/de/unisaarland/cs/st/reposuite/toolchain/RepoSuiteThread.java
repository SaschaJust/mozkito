/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.checks.Check;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import net.ownhero.dev.ioda.Tuple;

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
	
	private boolean                                                 shutdown;
	private final LinkedBlockingDeque<RepoSuiteGeneralThread<?, ?>> knownThreads  = new LinkedBlockingDeque<RepoSuiteGeneralThread<?, ?>>();
	private final RepoSuiteThreadGroup                              threadGroup;
	
	private final LinkedBlockingDeque<RepoSuiteGeneralThread<?, K>> inputThreads  = new LinkedBlockingDeque<RepoSuiteGeneralThread<?, K>>();
	private final LinkedBlockingDeque<RepoSuiteGeneralThread<V, ?>> outputThreads = new LinkedBlockingDeque<RepoSuiteGeneralThread<V, ?>>();
	
	private RepoSuiteDataStorage<K>                                 inputStorage;
	private RepoSuiteDataStorage<V>                                 outputStorage;
	private final RepoSuiteSettings                                 settings;
	
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
	@NoneNull
	public RepoSuiteThread(final RepoSuiteThreadGroup threadGroup, final String name, final RepoSuiteSettings settings) {
		super(threadGroup, name);
		
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
		
		CompareCondition.equals(hasInputConnector(),
		                        this.inputStorage != null,
		                        "Either this class has no input connector, then inputStorage must be null, or it has one and inputStorage must not be null. [hasInputConnector(): %s] [inputStorage!=null: %s]",
		                        hasInputConnector(), this.inputStorage != null);
		CompareCondition.equals(hasInputConnector(),
		                        this.inputStorage != null,
		                        "Either this class has no output connector, then outputStorage must be null, or it has one and outputStorage must not be null. [hasOutputConnector(): %s] [outputStorage!=null: %s]",
		                        hasOutputConnector(), this.outputStorage != null);
		Condition.check(!this.shutdown, "`shutdown` must not be set after constructor.");
		Condition.notNull(settings, "`settings` must not be null.");
		Condition.notNull(threadGroup, "`threadGroup` must not be null.");
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
	public final boolean connectInput(@NotNull final RepoSuiteGeneralThread<?, K> thread) {
		
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
	public final boolean connectOutput(@NotNull final RepoSuiteGeneralThread<V, ?> thread) {
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
	public final void disconnectInput(@NotNull final RepoSuiteGeneralThread<?, K> thread) {
		if (hasInputConnector()) {
			if (this.inputThreads.contains(thread)) {
				this.inputThreads.remove(thread);
				this.inputStorage.unregisterInput(thread);
				this.knownThreads.remove(thread);
				
				if (Logger.logInfo()) {
					Logger.info("[" + getHandle() + "] Unlinking input connector from [" + thread.getHandle() + "]");
				}
				
				if (thread.hasOutputConnector() && thread.isOutputConnected()) {
					thread.disconnectOutput(this);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#disconnectOutput
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final void disconnectOutput(@NotNull final RepoSuiteGeneralThread<V, ?> thread) {
		if (hasOutputConnector()) {
			if (this.outputThreads.contains(thread)) {
				this.outputThreads.remove(thread);
				this.outputStorage.unregisterOutput(thread);
				this.knownThreads.remove(thread);
				
				if (Logger.logInfo()) {
					Logger.info("[" + getHandle() + "] Unlinking output connector from [" + thread.getHandle() + "]");
				}
				
				if (thread.hasInputConnector() && thread.isInputConnected()) {
					thread.disconnectInput(this);
				}
			}
		}
	}
	
	@Override
	public synchronized void finish() {
		
		if (Logger.logInfo()) {
			Logger.info("All done. Disconnecting from data storages.");
		}
		
		RepoSuiteGeneralThread<V, ?> outputThread = null;
		
		while ((outputThread = this.outputThreads.poll()) != null) {
			if (Logger.logDebug()) {
				Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
			}
			outputThread.disconnectInput(this);
		}
		
		RepoSuiteGeneralThread<?, K> inputThread = null;
		
		while ((inputThread = this.inputThreads.poll()) != null) {
			if (Logger.logDebug()) {
				Logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
			}
			inputThread.disconnectOutput(this);
		}
		
		setShutdown(true);
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
		Check.notNull(this.inputStorage,
		              "When requesting the inputSize, there has to be already an inputStorage attached");
		Check.check(hasInputConnector(), "When requesting the inputSize, there has to exist an inputConnector");
		
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
	@Override
	public final boolean isShutdown() {
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
		Check.notNull(this.outputStorage,
		              "When requesting the inputSize, there has to be already an outputStorage attached");
		Check.check(hasOutputConnector(), "When requesting the outputSize, there has to exist an outputConnector");
		
		return this.outputStorage.size();
	}
	
	/**
	 * @return the next chunk from the inputStorage. Will be null if there isn't
	 *         any input left and no writers are attached to the storage
	 *         anymore.
	 * @throws InterruptedException
	 */
	protected final K read() throws InterruptedException {
		Tuple<K, CountDownLatch> data = this.inputStorage.read();
		if (data == null) {
			return null;
		} else {
			return data.getFirst();
		}
	}
	
	/**
	 * @return the next chunk from the inputStorage. Will be null if there isn't
	 *         any input left and no writers are attached to the storage
	 *         anymore.
	 * @throws InterruptedException
	 */
	protected final Tuple<K, CountDownLatch> readLatch() throws InterruptedException {
		return this.inputStorage.read();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setInputStorage
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteDataStorage)
	 */
	@Override
	public final void setInputStorage(@NotNull final RepoSuiteDataStorage<K> storage) {
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
	public final void setOutputStorage(@NotNull final RepoSuiteDataStorage<V> storage) {
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
	public final synchronized void shutdown() {
		if (!isShutdown()) {
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			setShutdown(true);
			
			RepoSuiteGeneralThread<V, ?> outputThread = null;
			
			while ((outputThread = this.outputThreads.poll()) != null) {
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
				}
				outputThread.disconnectInput(this);
			}
			
			RepoSuiteGeneralThread<?, K> inputThread = null;
			
			while ((inputThread = this.inputThreads.poll()) != null) {
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
				}
				inputThread.disconnectOutput(this);
			}
			
			RepoSuiteGeneralThread<?, ?> thread = null;
			
			while ((thread = this.knownThreads.poll()) != null) {
				if (!thread.isShutdown()) {
					thread.shutdown();
				}
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
	 * @return
	 * @throws InterruptedException
	 */
	protected final CountDownLatch write(final V data) throws InterruptedException {
		Condition.notNull(data, "[write] `data` should not be null.");
		Condition.notNull(this.outputStorage, "[write] `outputStorage` should not be null.");
		Condition.check(hasOutputConnector(), "[write] `hasOutputConnector()` should be true, but is: %s",
		                hasOutputConnector());
		if (Logger.logTrace()) {
			Logger.trace("writing data: " + data);
		}
		return this.outputStorage.write(data);
	}
	
}
