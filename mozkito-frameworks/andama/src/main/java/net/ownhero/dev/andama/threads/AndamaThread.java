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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.storages.AndamaDataStorage;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.checks.Check;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * {@link AndamaThread}s are the edges of a {@link AndamaChain} graph,
 * connecting the {@link AndamaDataStorage} nodes.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
abstract class AndamaThread<K, V> extends Thread implements AndamaThreadable<K, V>, Comparable<AndamaThread<?, ?>> {
	
	/**
	 * @param type
	 * @return
	 */
	static String getTypeName(final Type type) {
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
	private final LinkedBlockingDeque<AndamaThreadable<?, K>> inputThreads       = new LinkedBlockingDeque<AndamaThreadable<?, K>>();
	private final LinkedBlockingDeque<AndamaThreadable<?, ?>> knownThreads       = new LinkedBlockingDeque<AndamaThreadable<?, ?>>();
	private V                                                 outputData;
	private AndamaDataStorage<V>                              outputStorage;
	private final LinkedBlockingDeque<AndamaThreadable<V, ?>> outputThreads      = new LinkedBlockingDeque<AndamaThreadable<V, ?>>();
	private boolean                                           parallelizable     = false;
	private final AndamaSettings                              settings;
	private boolean                                           shutdown;
	private final AndamaGroup                                 threadGroup;
	private Tuple<K, CountDownLatch>                          inputDataTuple;
	private CountDownLatch                                    outputLatch        = null;
	
	private final boolean                                     waitForLatch       = false;
	private final Set<CountDownLatch>                         processLatches     = new HashSet<CountDownLatch>();
	
	// hooks
	private final Set<PreExecutionHook<K, V>>                 preExecutionHooks  = new HashSet<PreExecutionHook<K, V>>();
	
	private final Set<PreInputHook<K, V>>                     preInputHooks      = new HashSet<PreInputHook<K, V>>();
	private final Set<InputHook<K, V>>                        inputHooks         = new HashSet<InputHook<K, V>>();
	private final Set<PostInputHook<K, V>>                    postInputHooks     = new HashSet<PostInputHook<K, V>>();
	
	private final Set<PreProcessHook<K, V>>                   preProcessHooks    = new HashSet<PreProcessHook<K, V>>();
	private final Set<ProcessHook<K, V>>                      processHooks       = new HashSet<ProcessHook<K, V>>();
	private final Set<PostProcessHook<K, V>>                  postProcessHooks   = new HashSet<PostProcessHook<K, V>>();
	
	private final Set<PreOutputHook<K, V>>                    preOutputHooks     = new HashSet<PreOutputHook<K, V>>();
	private final Set<OutputHook<K, V>>                       outputHooks        = new HashSet<OutputHook<K, V>>();
	private final Set<PostOutputHook<K, V>>                   postOutputHooks    = new HashSet<PostOutputHook<K, V>>();
	
	private final Set<PostExecutionHook<K, V>>                postExecutionHooks = new HashSet<PostExecutionHook<K, V>>();
	private boolean                                           skipData           = false;
	
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
	public AndamaThread(@NotNull final AndamaGroup threadGroup, @NotNull final AndamaSettings settings,
	        final boolean parallelizable) {
		super(threadGroup, "default");
		setName(this.getClass().getSimpleName());
		this.parallelizable = parallelizable;
		threadGroup.addThread(this);
		this.threadGroup = threadGroup;
		this.settings = settings;
		@SuppressWarnings ("unchecked")
		AndamaArgument<Long> setting = (AndamaArgument<Long>) settings.getSetting("cache.size");
		
		if (hasInputConnector()) {
			if (setting != null) {
				this.inputStorage = new AndamaDataStorage<K>(setting.getValue().intValue());
			} else {
				this.inputStorage = new AndamaDataStorage<K>();
			}
		}
		
		if (hasOutputConnector()) {
			if (setting != null) {
				this.outputStorage = new AndamaDataStorage<V>(setting.getValue().intValue());
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
		Condition.notNull(this.settings, "`settings` must not be null.");
		Condition.notNull(this.threadGroup, "`threadGroup` must not be null.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addInputHook(net.ownhero
	 * .dev.andama.threads.InputHook)
	 */
	@Override
	public final void addInputHook(final InputHook<K, V> hook) {
		getInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addOutputHook(net.ownhero
	 * .dev.andama.threads.OutputHook)
	 */
	@Override
	public final void addOutputHook(final OutputHook<K, V> hook) {
		getOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPostExecutionHook(
	 * net.ownhero.dev.andama.threads.PostExecutionHook)
	 */
	@Override
	public final void addPostExecutionHook(final PostExecutionHook<K, V> hook) {
		getPostExecutionHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPostInputHook(net.
	 * ownhero.dev.andama.threads.PostInputHook)
	 */
	@Override
	public final void addPostInputHook(final PostInputHook<K, V> hook) {
		getPostInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPostOutputHook(net
	 * .ownhero.dev.andama.threads.PostOutputHook)
	 */
	@Override
	public final void addPostOutputHook(final PostOutputHook<K, V> hook) {
		getPostOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPostProcessHook(net
	 * .ownhero.dev.andama.threads.PostProcessHook)
	 */
	@Override
	public final void addPostProcessHook(final PostProcessHook<K, V> hook) {
		getPostProcessHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPreExecutionHook(net
	 * .ownhero.dev.andama.threads.PreExecutionHook)
	 */
	@Override
	public final void addPreExecutionHook(final PreExecutionHook<K, V> hook) {
		getPreExecutionHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPreInputHook(net.ownhero
	 * .dev.andama.threads.PreInputHook)
	 */
	@Override
	public final void addPreInputHook(final PreInputHook<K, V> hook) {
		getPreInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPreOutputHook(net.
	 * ownhero.dev.andama.threads.PreOutputHook)
	 */
	@Override
	public final void addPreOutputHook(final PreOutputHook<K, V> hook) {
		getPreOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addPreProcessHook(net
	 * .ownhero.dev.andama.threads.PreProcessHook)
	 */
	@Override
	public final void addPreProcessHook(final PreProcessHook<K, V> hook) {
		getPreProcessHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.AndamaThreadable#addProcessHook(net.ownhero
	 * .dev.andama.threads.ProcessHook)
	 */
	@Override
	public final void addProcessHook(final ProcessHook<K, V> hook) {
		getProcessHooks().add(hook);
	}
	
	/**
	 * @param latch
	 */
	final void addProcessLatch(final CountDownLatch latch) {
		this.processLatches.add(latch);
	}
	
	/**
	 * @return true if there are no glitches found in the connector setup.
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
	
	/**
	 * @return true if the thread hasn't been shutdown.
	 */
	private final boolean checkNotShutdown() {
		if (isShutdown()) {
			
			if (Logger.logWarn()) {
				Logger.warn("Thread already shut down. Won't run again.");
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
	public final boolean connectOutput(@NotNull final AndamaThreadable<V, ?> thread) {
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
	public final void disconnectOutput(@NotNull final AndamaThreadable<V, ?> thread) {
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	final protected void finalize() throws Throwable {
		super.finalize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#finish()
	 */
	@Override
	public final synchronized void finish() {
		
		if (Logger.logInfo()) {
			Logger.info("All done. Disconnecting from data storages.");
		}
		
		AndamaThreadable<V, ?> outputThread = null;
		
		while ((outputThread = this.outputThreads.poll()) != null) {
			
			if (Logger.logDebug()) {
				Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
			}
			outputThread.disconnectInput(this);
		}
		
		AndamaThreadable<?, K> inputThread = null;
		
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
		return this.inputDataTuple != null
		                                  ? this.inputDataTuple.getFirst()
		                                  : null;
	}
	
	/**
	 * @return the inputHooks
	 */
	protected final Set<InputHook<K, V>> getInputHooks() {
		return this.inputHooks;
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
	@Override
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
	 * @return the outputHooks
	 */
	protected final Set<OutputHook<K, V>> getOutputHooks() {
		return this.outputHooks;
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
	@Override
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
	 * @return the postExecutionHooks
	 */
	protected final Set<PostExecutionHook<K, V>> getPostExecutionHooks() {
		return this.postExecutionHooks;
	}
	
	/**
	 * @return the postInputHooks
	 */
	protected final Set<PostInputHook<K, V>> getPostInputHooks() {
		return this.postInputHooks;
	}
	
	/**
	 * @return the postOutputHooks
	 */
	protected final Set<PostOutputHook<K, V>> getPostOutputHooks() {
		return this.postOutputHooks;
	}
	
	/**
	 * @return the postProcessHooks
	 */
	protected final Set<PostProcessHook<K, V>> getPostProcessHooks() {
		return this.postProcessHooks;
	}
	
	/**
	 * @return the preExecutionHooks
	 */
	protected final Set<PreExecutionHook<K, V>> getPreExecutionHooks() {
		return this.preExecutionHooks;
	}
	
	/**
	 * @return the preInputHooks
	 */
	protected final Set<PreInputHook<K, V>> getPreInputHooks() {
		return this.preInputHooks;
	}
	
	/**
	 * @return the preOutputHooks
	 */
	protected final Set<PreOutputHook<K, V>> getPreOutputHooks() {
		return this.preOutputHooks;
	}
	
	/**
	 * @return the preProcessHooks
	 */
	protected final Set<PreProcessHook<K, V>> getPreProcessHooks() {
		return this.preProcessHooks;
	}
	
	/**
	 * @return the processHooks
	 */
	protected final Set<ProcessHook<K, V>> getProcessHooks() {
		return this.processHooks;
	}
	
	/**
	 * @return the processLatches
	 */
	protected final Set<CountDownLatch> getProcessLatches() {
		return this.processLatches;
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
	
	/**
	 * @return true if {@link AndamaThreadable#shutdown()} has already been
	 *         called on this object; false otherwise. The shutdown method can
	 *         also be called internally, after an error occurred.
	 */
	boolean isShutdown() {
		return this.shutdown;
	}
	
	/**
	 * @return the skipData
	 */
	protected final boolean isSkipData() {
		return this.skipData;
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
	 * @return
	 */
	private final boolean processingCompleted() {
		return AndamaHook.allCompleted(getProcessHooks());
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
	
	/**
	 * @throws InterruptedException
	 * 
	 */
	final void readNext() throws InterruptedException {
		this.inputDataTuple = readLatch();
	}
	
	@Override
	public final void run() {
		CollectionCondition.maxSize(this.inputHooks, 1, "There must not be more than 1 input hooks, but got: %s",
		                            this.inputHooks.size());
		// @formatter:off
		
		/*
		 *
         *                   +----------------------------------------------------------------------------------------------------------------------------------+
         *                   |                                                                                                                                  |
         *                   |                                no                                                                                                |
         *                   |                   +------------------------------------------------------------------------------------------------+             +--------------------------------------------------------------------------------------------------------------------+
         *                   v                   |                                                                                                v                                                                                                                                  |
         * +---------+     +----------+  yes   +------------+  yes   +----------+     +-------+     +-----------+     +-----------------+  no   +------------+     +---------+     +-------------+     +-------+  no    +-----------------+  no   +-----------+     +--------+     +------------+
         * | PREEXEC | --> |          | -----> | completed? | -----> | PREINPUT | --> | INPUT | --> | POSTINPUT | --> | i_data == null? | ----> | PREPROCESS | --> | PROCESS | --> | POSTPROCESS | --> | skip? | -----> | p_data == null? | ----> | PREOUTPUT | --> | OUTPUT | --> | POSTOUTPUT |
         * +---------+     |          |        +------------+        +----------+     +-------+     +-----------+     +-----------------+       +------------+     +---------+     +-------------+     +-------+        +-----------------+       +-----------+     +--------+     +------------+
         *                 |          |                                                                                 |                 no      ^                                                      |                |
         *                 | reading? | --------------------------------------------------------------------------------+-------------------------+                                                      |                | yes
         *                 |          |                                                                                 |                                                                                |                v
         *                 |          |                                                                                 |                                                                                |       yes    +-----------------+
         *                 |          |                                                                                 +--------------------------------------------------------------------------------+------------> |  POSTEXECUTION  |
         *                 +----------+                                                                                                                                                                  |              +-----------------+
         *                   ^          yes                                                                                                                                                              |
         *                   +---------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
         *
		 */
		
		// @formatter:on
		
		try {
			if (Logger.logInfo()) {
				Logger.info("Booting " + getHandle() + ".");
			}
			
			if (!checkNotShutdown()) {
				if (Logger.logError()) {
					Logger.error("Node status is 'shutdown'. Aborting...");
				}
				
				return;
			}
			
			// add default input hooks if none have been specified
			if (hasInputConnector() && getInputHooks().isEmpty()) {
				
				if (Logger.logInfo()) {
					Logger.info("Adding default input hook to " + getHandle() + ".");
				}
				getInputHooks().add(new DefaultInputHook<K, V>(this));
			}
			
			// add default output hooks if none have been specified
			if (hasOutputConnector() && getOutputHooks().isEmpty()) {
				if (Logger.logInfo()) {
					Logger.info("Adding default output hook to " + getHandle() + ".");
				}
				getOutputHooks().add(new DefaultOutputHook<K, V>(this));
			}
			
			// PREEXECUTION HOOKS
			if (!getPreExecutionHooks().isEmpty()) {
				if (Logger.logDebug()) {
					Logger.debug("Starting [preExecution] hook(s): "
					        + JavaUtils.collectionToString(getPreExecutionHooks()));
				}
				
				for (PreExecutionHook<K, V> hook : getPreExecutionHooks()) {
					do {
						if (Logger.logDebug()) {
							Logger.debug("Executing hook: " + hook.getHandle());
						}
						
						hook.execute();
						
						if (Logger.logDebug()) {
							Logger.debug("Done with hook: " + hook.getHandle());
						}
					} while (!hook.completed());
				}
				
				if (Logger.logDebug()) {
					Logger.debug("Finished [preExecution] hook processing.");
				}
			}
			
			do {
				// we require to have the processing completed before we fetch
				// new data
				if (hasInputConnector() && isInputConnected() && processingCompleted()) {
					if (!getPreInputHooks().isEmpty()) {
						if (Logger.logDebug()) {
							Logger.debug("Starting [preInput] hook(s): "
							        + JavaUtils.collectionToString(getPreInputHooks()));
						}
						for (PreInputHook<K, V> hook : getPreInputHooks()) {
							int i = 0;
							do {
								++i;
								
								if (Logger.logDebug()) {
									Logger.debug("Executing hook (" + i + "x round): " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook (" + i + "x round): " + hook.getHandle());
								}
							} while (!hook.completed());
						}
						if (Logger.logDebug()) {
							Logger.debug("Finished [preInput] hook processing.");
						}
					}
					
					if (!getInputHooks().isEmpty()) {
						if (Logger.logDebug()) {
							Logger.debug("Starting [input] hook(s): " + JavaUtils.collectionToString(getInputHooks()));
						}
						
						for (InputHook<K, V> hook : getInputHooks()) {
							int i = 0;
							do {
								++i;
								
								if (Logger.logDebug()) {
									Logger.debug("Executing hook (" + i + "x round): " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook (" + i + "x round): " + hook.getHandle());
								}
							} while (!hook.completed());
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Finished [input] hook processing.");
						}
					}
					
					if (!getPostInputHooks().isEmpty()) {
						if (Logger.logDebug()) {
							Logger.debug("Starting [postInput] hook(s): "
							        + JavaUtils.collectionToString(getPostInputHooks()));
						}
						
						for (PostInputHook<K, V> hook : getPostInputHooks()) {
							int i = 0;
							do {
								++i;
								
								if (Logger.logDebug()) {
									Logger.debug("Executing hook (" + i + "x round): " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook (" + i + "x round): " + hook.getHandle());
								}
							} while (!hook.completed());
						}
						
						if (Logger.logDebug()) {
							Logger.debug("Finished [postInput] hook processing.");
						}
					}
					
					// return if we did not fetch any new data from input
					if (getInputData() == null) {
						return;
					}
				}
				
				// PREPROCESS HOOKS
				if (!getPreProcessHooks().isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Starting [preProcess] hook(s): "
						        + JavaUtils.collectionToString(getPreProcessHooks()));
					}
					
					for (PreProcessHook<K, V> hook : getPreProcessHooks()) {
						if (Logger.logDebug()) {
							Logger.debug("Executing hook: " + hook.getHandle());
						}
						
						hook.execute();
						
						if (Logger.logDebug()) {
							Logger.debug("Done with hook: " + hook.getHandle());
						}
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Finished [preProcess] hook processing.");
					}
				}
				
				// PROCESS HOOKS
				if (!getProcessHooks().isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Starting [process] hook(s): " + JavaUtils.collectionToString(getProcessHooks()));
					}
					
					for (ProcessHook<K, V> hook : getProcessHooks()) {
						if (Logger.logDebug()) {
							Logger.debug("Executing hook: " + hook.getHandle());
						}
						
						hook.execute();
						
						if (Logger.logDebug()) {
							Logger.debug("Done with hook: " + hook.getHandle());
						}
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Finished [process] hook processing.");
					}
				}
				
				// POSTPROCESS HOOKS
				if (!getPostProcessHooks().isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Starting [postProcess] hook(s): "
						        + JavaUtils.collectionToString(getPostProcessHooks()));
					}
					
					for (PostProcessHook<K, V> hook : getPostProcessHooks()) {
						if (Logger.logDebug()) {
							Logger.debug("Executing hook: " + hook.getHandle());
						}
						
						hook.execute();
						
						if (Logger.logDebug()) {
							Logger.debug("Done with hook: " + hook.getHandle());
						}
					}
					
					if (Logger.logDebug()) {
						Logger.debug("Finished [postProcess] hook processing.");
					}
				}
				
				if (!skipData()) {
					if (getOutputData() != null) {
						// PREOUTPUT HOOKS
						if (!getPreOutputHooks().isEmpty()) {
							if (Logger.logDebug()) {
								Logger.debug("Starting [preOutput] hook(s): "
								        + JavaUtils.collectionToString(getPreOutputHooks()));
							}
							
							for (PreOutputHook<K, V> hook : getPreOutputHooks()) {
								if (Logger.logDebug()) {
									Logger.debug("Executing hook: " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook: " + hook.getHandle());
								}
							}
							
							if (Logger.logDebug()) {
								Logger.debug("Finished [preOutput] hook processing.");
							}
						}
						
						// OUTPUT HOOKS
						if (!getOutputHooks().isEmpty()) {
							if (Logger.logDebug()) {
								Logger.debug("Starting [output] hook(s): "
								        + JavaUtils.collectionToString(getOutputHooks()));
							}
							
							for (OutputHook<K, V> hook : getOutputHooks()) {
								if (Logger.logDebug()) {
									Logger.debug("Executing hook: " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook: " + hook.getHandle());
								}
							}
							
							if (Logger.logDebug()) {
								Logger.debug("Finished [output] hook processing.");
							}
						}
						
						// POSTOUTPUT HOOKS
						if (!getPostOutputHooks().isEmpty()) {
							if (Logger.logDebug()) {
								Logger.debug("Starting [postOutput] hook(s): "
								        + JavaUtils.collectionToString(getPostOutputHooks()));
							}
							
							for (PostOutputHook<K, V> hook : getPostOutputHooks()) {
								if (Logger.logDebug()) {
									Logger.debug("Executing hook: " + hook.getHandle());
								}
								
								hook.execute();
								
								if (Logger.logDebug()) {
									Logger.debug("Done with hook: " + hook.getHandle());
								}
							}
							
							if (Logger.logDebug()) {
								Logger.debug("Finished [postProcess] hook processing.");
							}
						}
						
						setOutputData(null);
					}
				} else {
					setSkipData(false);
				}
			} while (!processingCompleted() || (getInputData() != null));
			// repeat until we got no more input data and are not processing
			// further data
			
			// POSTEXECUTION HOOKS
			if (!getPostExecutionHooks().isEmpty()) {
				if (Logger.logDebug()) {
					Logger.debug("Starting [postExecution] hook(s): "
					        + JavaUtils.collectionToString(getPostExecutionHooks()));
				}
				
				for (PostExecutionHook<K, V> hook : getPostExecutionHooks()) {
					if (Logger.logDebug()) {
						Logger.debug("Executing hook: " + hook.getHandle());
					}
					
					hook.execute();
					
					if (Logger.logDebug()) {
						Logger.debug("Done with hook: " + hook.getHandle());
					}
				}
				
				if (Logger.logDebug()) {
					Logger.debug("Finished [postExecution] hook processing.");
				}
			}
			
			finish();
		} catch (Exception e) {
			
			if (Logger.logError()) {
				Logger.error("Caught exception: " + e.getClass().getSimpleName());
				Logger.error(e.getMessage(), e);
				Logger.error("Shutting down.");
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
	
	/**
	 * @param skipData
	 *            the skipData to set
	 */
	protected final void setSkipData(final boolean skipData) {
		this.skipData = skipData;
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
			
			if (Logger.logInfo()) {
				Logger.info("[" + this.getClass().getSimpleName() + "] Received shutdown request. Terminating.");
			}
			
			setShutdown(true);
			
			AndamaThread<V, ?> outputThread = null;
			
			while ((outputThread = (AndamaThread<V, ?>) this.outputThreads.poll()) != null) {
				
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
				}
				outputThread.disconnectInput(this);
			}
			
			AndamaThread<?, K> inputThread = null;
			
			while ((inputThread = (AndamaThread<?, K>) this.inputThreads.poll()) != null) {
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
				}
				inputThread.disconnectOutput(this);
			}
			
			AndamaThread<?, ?> thread = null;
			
			while ((thread = (AndamaThread<?, ?>) this.knownThreads.poll()) != null) {
				if (!thread.isShutdown()) {
					thread.shutdown();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#skipData()
	 */
	@Override
	public boolean skipData() {
		return this.skipData = true;
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
	final CountDownLatch write(final V data) throws InterruptedException {
		Condition.notNull(data, "[write] `data` should not be null.");
		Condition.notNull(this.outputStorage, "[write] `outputStorage` should not be null.");
		Condition.check(hasOutputConnector(), "[write] `hasOutputConnector()` should be true, but is: %s",
		                hasOutputConnector());
		
		if (Logger.logTrace()) {
			Logger.trace("writing data: " + data);
		}
		
		return this.outputStorage.write(data);
	}
	
	/**
	 * @param data
	 * @throws InterruptedException
	 */
	final void writeOutputData(final V data) throws InterruptedException {
		this.outputLatch = write(data);
		
		if (this.isWaitForLatch()) {
			if (Logger.logDebug()) {
				Logger.debug("Waiting for latch to be resolved.");
			}
			
			this.outputLatch.await();
		}
	}
	
}
