/**
 * 
 */
package net.ownhero.dev.andama.threads;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.storages.AndamaDataStorage;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.checks.Check;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AndamaThread}s are the edges of a {@link AndamaChain} graph,
 * connecting the {@link AndamaDataStorage} nodes. 
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
// TODO make package protected
public abstract class AndamaThread<K, V> extends Thread implements AndamaThreadable<K, V> {
	
	private final Logger                                      logger         = LoggerFactory.getLogger(this.getClass());
	
	private boolean                                           shutdown;
	private final LinkedBlockingDeque<AndamaThreadable<?, ?>> knownThreads   = new LinkedBlockingDeque<AndamaThreadable<?, ?>>();
	private final AndamaGroup                                 threadGroup;
	
	private final LinkedBlockingDeque<AndamaThreadable<?, K>> inputThreads   = new LinkedBlockingDeque<AndamaThreadable<?, K>>();
	private final LinkedBlockingDeque<AndamaThreadable<V, ?>> outputThreads  = new LinkedBlockingDeque<AndamaThreadable<V, ?>>();
	
	private AndamaDataStorage<K>                              inputStorage;
	private AndamaDataStorage<V>                              outputStorage;
	private final AndamaSettings                              settings;
	private boolean                                           parallelizable = false;
	private K                                                 inputData;
	
	private V                                                 outputData;
	
	/**
	 * The constructor of the {@link AndamaThread}. This should be called
	 * from all extending classes.
	 * 
	 * @param threadGroup
	 *            the {@link AndamaGroup}. See
	 *            {@link AndamaGroup} for details.
	 * @param name
	 *            the name of the {@link AndamaGroup}. See
	 *            {@link AndamaGroup} for details.
	 * @param settings
	 *            An instance of RepoSuiteSettings
	 */
	@NoneNull
	public AndamaThread(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, "default");
		setName(this.getClass().getSimpleName());
		this.parallelizable = parallelizable;
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.settings = settings;
		AndamaArgument<?> setting = settings.getSetting("cache.size");
		
		if (hasInputConnector()) {
			if (setting != null) {
				this.inputStorage = new AndamaDataStorage<K>(((Long) setting.getValue()).intValue());
			} else {
				this.inputStorage = new AndamaDataStorage<K>();
			}
		}
		
		if (hasOutputConnector()) {
			if (setting != null) {
				this.outputStorage = new AndamaDataStorage<V>(((Long) setting.getValue()).intValue());
			} else {
				this.outputStorage = new AndamaDataStorage<V>();
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
			if (this.logger.isErrorEnabled()) {
				this.logger.error(getHandle() + " is not input connected (required to run this task).");
			}
			retval = false;
		}
		
		if (!isOutputConnected()) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error(getHandle() + " is not out connected (required to run this task).");
			}
			retval = false;
		}
		
		if (retval && this.knownThreads.isEmpty()) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error(getHandle()
				        + " has known connections, but knownThreads is empty. This should never happen.");
			}
			retval = false;
		}
		
		if (!retval) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Shutting all threads down.");
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
			
			if (this.logger.isWarnEnabled()) {
				this.logger.warn("Thread already shut down. Won't run again.");
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
	public final boolean connectInput(@NotNull final AndamaThreadable<?, K> thread) {
		
		if (hasInputConnector()) {
			this.inputThreads.add(thread);
			this.knownThreads.add(thread);
			this.setInputStorage(thread.getOutputStorage());
			
			if (this.logger.isInfoEnabled()) {
				this.logger.info("[" + getHandle() + "] Linking input connector to [" + thread.getHandle() + "]");
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
	public final boolean connectOutput(@NotNull final AndamaThreadable<V, ?> thread) {
		if (hasOutputConnector()) {
			this.outputThreads.add(thread);
			this.knownThreads.add(thread);
			this.setOutputStorage(thread.getInputStorage());
			
			if (this.logger.isInfoEnabled()) {
				this.logger.info("[" + getHandle() + "] Linking output connector to [" + thread.getHandle() + "]");
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
	public final void disconnectInput(@NotNull final AndamaThreadable<?, K> thread) {
		if (hasInputConnector()) {
			if (this.inputThreads.contains(thread)) {
				this.inputThreads.remove(thread);
				this.inputStorage.unregisterInput(thread);
				this.knownThreads.remove(thread);
				
				if (this.logger.isInfoEnabled()) {
					this.logger.info("[" + getHandle() + "] Unlinking input connector from [" + thread.getHandle()
					        + "]");
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
	public final void disconnectOutput(@NotNull final AndamaThreadable<V, ?> thread) {
		if (hasOutputConnector()) {
			if (this.outputThreads.contains(thread)) {
				this.outputThreads.remove(thread);
				this.outputStorage.unregisterOutput(thread);
				this.knownThreads.remove(thread);
				
				if (this.logger.isInfoEnabled()) {
					this.logger.info("[" + getHandle() + "] Unlinking output connector from [" + thread.getHandle()
					        + "]");
				}
				
				if (thread.hasInputConnector() && thread.isInputConnected()) {
					thread.disconnectInput(this);
				}
			}
		}
	}
	
	@Override
	public synchronized void finish() {
		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("All done. Disconnecting from data storages.");
		}
		
		AndamaThreadable<V, ?> outputThread = null;
		
		while ((outputThread = this.outputThreads.poll()) != null) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
			}
			outputThread.disconnectInput(this);
		}
		
		AndamaThreadable<?, K> inputThread = null;
		
		while ((inputThread = this.inputThreads.poll()) != null) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
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
	
	/**
	 * @return the inputData
	 */
	public K getInputData() {
		return this.inputData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getInputStorage()
	 */
	@Override
	public final AndamaDataStorage<K> getInputStorage() {
		return this.inputStorage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.AndamaThreadable#getInputThreads()
	 */
	@Override
	public Collection<AndamaThreadable<?, K>> getInputThreads() {
		LinkedList<AndamaThreadable<?, K>> list = new LinkedList<AndamaThreadable<?, K>>();
		
		if (isInputConnected()) {
			list.addAll(this.inputThreads);
		}
		
		return list;
	}
	
	/**
	 * @return the outputData
	 */
	public V getOutputData() {
		return this.outputData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getOutputStorage()
	 */
	@Override
	public final AndamaDataStorage<V> getOutputStorage() {
		return this.outputStorage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.AndamaThreadable#getOutputThreads()
	 */
	@Override
	public Collection<AndamaThreadable<V, ?>> getOutputThreads() {
		LinkedList<AndamaThreadable<V, ?>> list = new LinkedList<AndamaThreadable<V, ?>>();
		
		if (isOutputConnected()) {
			list.addAll(this.outputThreads);
		}
		
		return list;
	}
	
	/**
	 * @return the settings
	 */
	protected final AndamaSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * Requests the current size of the input storage. Make sure there is a
	 * valid input storage, i.e. you are using an implementation where
	 * {@link AndamaThread#hasInputConnector()} is true.
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
	public final boolean isInputConnected(final AndamaThreadable<?, K> thread) {
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
	public final boolean isOutputConnected(final AndamaThreadable<V, ?> thread) {
		return (this.outputThreads.contains(thread));
	}
	
	/**
	 * @return the parallelizable
	 */
	public boolean isParallelizable() {
		return this.parallelizable;
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
	 * {@link AndamaThread#hasOutputConnector()} is true.
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
	
	private K readInputData() {
		// TODO Auto-generated method stub
		return null;
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
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		try {
			// TODO log
			beforeExecution();
			// TODO log
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			while (!isShutdown() && ((this.inputData = readInputData()) != null)) {
				// TODO log
				beforeProcess();
				// TODO log
				this.outputData = process(getInputData());
				// TODO log
				writeOutputData(getOutputData());
				// TODO log
				afterProcess();
				// TODO log
			}
			
			// TODO log
			afterExecution();
			// TODO log
			finish();
		} catch (Exception e) {
			// TODO log
			shutdown();
		}
	}
	
	/**
	 * @param inputData the inputData to set
	 */
	public void setInputData(final K inputData) {
		this.inputData = inputData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setInputStorage
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteDataStorage)
	 */
	@Override
	public final void setInputStorage(@NotNull final AndamaDataStorage<K> storage) {
		if (hasInputConnector()) {
			this.inputStorage = storage;
			storage.registerOutput(this);
		}
	}
	
	/**
	 * @param outputData the outputData to set
	 */
	public void setOutputData(final V outputData) {
		this.outputData = outputData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setOutputStorage
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteDataStorage)
	 */
	@Override
	public final void setOutputStorage(@NotNull final AndamaDataStorage<V> storage) {
		if (hasOutputConnector()) {
			this.outputStorage = storage;
			storage.registerInput(this);
		}
	}
	
	/**
	 * Set the current shutdown status. This method only sets the variable. Call
	 * {@link AndamaThread#shutdown()} to initiate a proper shutdown.
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
			if (this.logger.isInfoEnabled()) {
				this.logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			setShutdown(true);
			
			AndamaThreadable<V, ?> outputThread = null;
			
			while ((outputThread = this.outputThreads.poll()) != null) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
				}
				outputThread.disconnectInput(this);
			}
			
			AndamaThreadable<?, K> inputThread = null;
			
			while ((inputThread = this.inputThreads.poll()) != null) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
				}
				inputThread.disconnectOutput(this);
			}
			
			AndamaThreadable<?, ?> thread = null;
			
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
	 * {@link AndamaThread#hasOutputConnector()} is true.
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
		
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("writing data: " + data);
		}
		
		return this.outputStorage.write(data);
	}
	
	private void writeOutputData(final V outputData2) {
		// TODO Auto-generated method stub
		
	}
	
}
