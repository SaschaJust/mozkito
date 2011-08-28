/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.threads;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.storages.AndamaDataStorage;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.checks.Check;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

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
public abstract class AndamaThread<K, V> extends Thread implements AndamaThreadable<K, V>,
        Comparable<AndamaThread<?, ?>> {
	
	/**
	 * @param type
	 * @return
	 */
	public static String getTypeName(final Type type) {
		if (type == null) {
			return "(none)";
		} else {
			String typeName = type.toString();
			String[] parts = typeName.split(" ");
			if (parts.length > 1) {
				typeName = parts[parts.length - 1];
			}
			parts = typeName.split("\\.");
			if (parts.length > 1) {
				typeName = parts[parts.length - 1];
			}
			return typeName;
		}
	}
	
	private AndamaDataStorage<K>                              inputStorage;
	private final LinkedBlockingDeque<AndamaThreadable<?, K>> inputThreads   = new LinkedBlockingDeque<AndamaThreadable<?, K>>();
	private final LinkedBlockingDeque<AndamaThreadable<?, ?>> knownThreads   = new LinkedBlockingDeque<AndamaThreadable<?, ?>>();
	private final Logger                                      logger         = LoggerFactory.getLogger(this.getClass());
	private V                                                 outputData;
	private AndamaDataStorage<V>                              outputStorage;
	private final LinkedBlockingDeque<AndamaThreadable<V, ?>> outputThreads  = new LinkedBlockingDeque<AndamaThreadable<V, ?>>();
	private boolean                                           parallelizable = false;
	private final AndamaSettings                              settings;
	private boolean                                           shutdown;
	private final AndamaGroup                                 threadGroup;
	private Tuple<K, CountDownLatch>                          inputDataTuple;
	private CountDownLatch                                    outputLatch    = null;
	
	private final boolean                                     waitForLatch   = false;
	private boolean                                           skip;
	
	/**
	 * The constructor of the {@link AndamaThread}. This should be called from
	 * all extending classes.
	 * 
	 * @param threadGroup
	 *            the {@link AndamaGroup}. See {@link AndamaGroup} for details.
	 * @param name
	 *            the name of the {@link AndamaGroup}. See {@link AndamaGroup}
	 *            for details.
	 * @param settings
	 *            An instance of RepoSuiteSettings
	 */
	// @NoneNull
	public AndamaThread(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, "default");
		setName(this.getClass().getSimpleName());
		this.parallelizable = parallelizable;
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.settings = settings;
		AndamaArgument setting = null; // settings.getSetting("cache.size");
		
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
		// Condition.notNull(settings, "`settings` must not be null.");
		Condition.notNull(threadGroup, "`threadGroup` must not be null.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterExecution()
	 */
	@Override
	public void afterExecution() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterProcess()
	 */
	@Override
	public void afterProcess() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeProcess()
	 */
	@Override
	public void beforeProcess() {
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
	 * @see java.lang.Thread#clone()
	 */
	@Override
	final protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(final AndamaThread<?, ?> o) {
		AndamaThreadComparator comparator = new AndamaThreadComparator();
		return comparator.compare(this, o);
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
	 * @see java.lang.Thread#countStackFrames()
	 */
	@SuppressWarnings ("deprecation")
	@Override
	final public int countStackFrames() {
		return super.countStackFrames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#destroy()
	 */
	@SuppressWarnings ("deprecation")
	@Override
	final public void destroy() {
		super.destroy();
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	final protected void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public final synchronized void finish() {
		
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
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	@Override
	final public ClassLoader getContextClassLoader() {
		return super.getContextClassLoader();
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
	 * @see java.lang.Thread#getId()
	 */
	@Override
	public long getId() {
		return super.getId();
	}
	
	/**
	 * @param thread
	 * @return the type of the input chunks of the given thread
	 */
	public final Type getInputClassType() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[0];
	}
	
	/**
	 * @return the inputData
	 */
	public final K getInputData() {
		return this.inputDataTuple.getFirst();
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
	public final Collection<AndamaThreadable<?, K>> getInputThreads() {
		LinkedList<AndamaThreadable<?, K>> list = new LinkedList<AndamaThreadable<?, K>>();
		
		if (isInputConnected()) {
			list.addAll(this.inputThreads);
		}
		
		return list;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public final Class<K> getInputType() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		if (hasInputConnector()) {
			return (Class<K>) type.getActualTypeArguments()[0];
		} else {
			return null;
		}
	}
	
	/**
	 * @param thread
	 * @return the type of the output chunks of the given thread
	 */
	public final Type getOutputClassType() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		return (type.getActualTypeArguments().length > 1
		                                                ? type.getActualTypeArguments()[1]
		                                                : type.getActualTypeArguments()[0]);
	}
	
	/**
	 * @return the outputData
	 */
	public final V getOutputData() {
		return this.outputData;
	}
	
	/**
	 * @return the outputLatch
	 */
	private CountDownLatch getOutputLatch() {
		return this.outputLatch;
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
	public final Collection<AndamaThreadable<V, ?>> getOutputThreads() {
		LinkedList<AndamaThreadable<V, ?>> list = new LinkedList<AndamaThreadable<V, ?>>();
		
		if (isOutputConnected()) {
			list.addAll(this.outputThreads);
		}
		
		return list;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public final Class<V> getOutputType() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		if (hasOutputConnector()) {
			return (Class<V>) type.getActualTypeArguments()[type.getActualTypeArguments().length - 1];
		} else {
			return null;
		}
		
	}
	
	/**
	 * @return the settings
	 */
	protected final AndamaSettings getSettings() {
		return this.settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#getStackTrace()
	 */
	@Override
	final public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#getState()
	 */
	@Override
	final public State getState() {
		return super.getState();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#getUncaughtExceptionHandler()
	 */
	@Override
	final public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return super.getUncaughtExceptionHandler();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	final public int hashCode() {
		return super.hashCode();
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
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	final public void interrupt() {
		super.interrupt();
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
	 * @see java.lang.Thread#isInterrupted()
	 */
	@Override
	final public boolean isInterrupted() {
		return super.isInterrupted();
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
	public final boolean isParallelizable() {
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
	 * @return the waitForLatch
	 */
	public boolean isWaitForLatch() {
		return this.waitForLatch;
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
	private final K read() throws InterruptedException {
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
	private final Tuple<K, CountDownLatch> readLatch() throws InterruptedException {
		return this.inputStorage.read();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public final void run() {
		try {
			
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Starting [beforeExecution] hook.");
			}
			
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Launching " + getHandle() + ".");
			}
			
			beforeExecution();
			
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Finished [beforeExecution] hook.");
			}
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (hasInputConnector()) {
				while (!isShutdown() && ((this.inputDataTuple = readLatch()) != null)) {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Starting [beforeProcess] hook.");
					}
					
					beforeProcess();
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Finished [beforeProcess] hook.");
					}
					
					K data = getInputData();
					
					if (this.logger.isInfoEnabled()) {
						this.logger.info("Processing: " + data);
					}
					
					if (hasOutputConnector()) {
						this.outputData = ((InputOutputConnectable<K, V>) this).process(data);
						
						if (!this.skip) {
							if (this.logger.isDebugEnabled()) {
								this.logger.debug("Handing over: " + this.outputData);
							}
							
							writeOutputData(getOutputData());
						} else {
							this.skip = false;
						}
					} else {
						((OnlyInputConnectable<K>) this).process(data);
					}
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Starting [afterProcess] hook.");
					}
					
					afterProcess();
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Finished [afterProcess] hook.");
					}
					
					// decrease latch for waiting threads
					this.inputDataTuple.getSecond().countDown();
				}
			} else {
				do {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Starting [beforeProcess] hook.");
					}
					
					beforeProcess();
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Finished [beforeProcess] hook.");
					}
					
					if (this.logger.isInfoEnabled()) {
						this.logger.info("Preparing data.");
					}
					
					this.outputData = ((OnlyOutputConnectable<V>) this).process();
					
					if (!this.skip) {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("Handing over: " + this.outputData);
						} else {
							this.skip = false;
						}
						
						writeOutputData(getOutputData());
					}
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Starting [afterProcess] hook.");
					}
					
					afterProcess();
					
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Finished [afterProcess] hook.");
					}
				} while (this.outputData != null);
			}
			
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Starting [afterExecution] hook.");
			}
			
			afterExecution();
			
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Finished [afterExecution] hook.");
				this.logger.debug("Cleaning up and terminating: " + getHandle());
			}
			
			finish();
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Caught exception: " + e.getMessage());
				this.logger.error("Shutting down.");
			}
			shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#setContextClassLoader(java.lang.ClassLoader)
	 */
	@Override
	final public void setContextClassLoader(final ClassLoader cl) {
		super.setContextClassLoader(cl);
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
	 * @param outputData
	 *            the outputData to set
	 */
	public void setOutputData(final V outputData) {
		this.outputData = outputData;
	}
	
	/**
	 * @param outputLatch
	 *            the outputLatch to set
	 */
	private void setOutputLatch(final CountDownLatch outputLatch) {
		this.outputLatch = outputLatch;
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
	 * @see java.lang.Thread#setUncaughtExceptionHandler(java.lang.Thread.
	 * UncaughtExceptionHandler)
	 */
	@Override
	final public void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
		super.setUncaughtExceptionHandler(eh);
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
	
	/**
	 * @param data
	 * @return
	 */
	public final V skip(final Object data) {
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Skipping: " + data);
		}
		
		this.skip = true;
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#start()
	 */
	@Override
	final public synchronized void start() {
		super.start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append('[').append(this.getThreadGroup().getName()).append("] ");
		
		builder.append(getHandle());
		
		if ((this.getClass().getSuperclass() != null)
		        && AndamaThread.class.isAssignableFrom(this.getClass().getSuperclass())) {
			builder.append(' ').append(this.getClass().getSuperclass().getSimpleName());
		}
		
		builder.append(' ');
		
		StringBuilder typeBuilder = new StringBuilder();
		if (hasInputConnector()) {
			typeBuilder.append(getTypeName(getInputClassType()));
		}
		
		if (hasOutputConnector()) {
			if (typeBuilder.length() > 0) {
				typeBuilder.append(":");
			}
			
			typeBuilder.append(getTypeName(getOutputClassType()));
		}
		
		builder.append(typeBuilder);
		return builder.toString();
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
	private final CountDownLatch write(final V data) throws InterruptedException {
		Condition.notNull(data, "[write] `data` should not be null.");
		Condition.notNull(this.outputStorage, "[write] `outputStorage` should not be null.");
		Condition.check(hasOutputConnector(), "[write] `hasOutputConnector()` should be true, but is: %s",
		                hasOutputConnector());
		
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("writing data: " + data);
		}
		
		return this.outputStorage.write(data);
	}
	
	/**
	 * @param data
	 * @throws InterruptedException
	 */
	private void writeOutputData(final V data) throws InterruptedException {
		CountDownLatch latch = write(data);
		
		if (this.isWaitForLatch()) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Waiting for latch to be resolved.");
			}
			
			latch.await();
		}
	}
	
}
