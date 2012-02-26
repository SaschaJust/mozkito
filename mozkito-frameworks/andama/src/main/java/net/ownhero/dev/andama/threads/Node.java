/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.threads;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.storages.AndamaDataStorage;
import net.ownhero.dev.andama.threads.comparator.AndamaThreadComparator;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.checks.Check;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * {@link Node}s are the edges of a {@link Chain} graph, connecting the {@link AndamaDataStorage} nodes.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
abstract class Node<K, V> extends Thread implements INode<K, V>, Comparable<Node<?, ?>> {
	
	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 * 
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	@SuppressWarnings ("rawtypes")
	public static Class<?> getClass(final Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			final Type componentType = ((GenericArrayType) type).getGenericComponentType();
			final Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 * 
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	@SuppressWarnings ("rawtypes")
	private static <T> List<Class<?>> getTypeArguments(final Class<T> baseClass,
	                                                   final Class<? extends T> childClass) {
		final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just
				// keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				final ParameterizedType parameterizedType = (ParameterizedType) type;
				final Class<?> rawType = (Class) parameterizedType.getRawType();
				
				final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}
				
				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}
		
		// finally, for each actual type argument provided to baseClass,
		// determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		final List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}
	
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
	
	private final Set<CountDownLatch>                                  awaitingLatches      = new HashSet<CountDownLatch>();
	private final Map<Node<?, ?>, CountDownLatch>                      dependencies         = new HashMap<Node<?, ?>, CountDownLatch>();
	private final Map<Class<?>, K>                                     inputCache           = new HashMap<Class<?>, K>();
	private Tuple<K, CountDownLatch>                                   inputDataTuple;
	private final Set<InputHook<K, V>>                                 inputHooks           = new HashSet<InputHook<K, V>>();
	private AndamaDataStorage<K>                                       inputStorage;
	private final LinkedBlockingDeque<INode<?, K>>                     inputThreads         = new LinkedBlockingDeque<INode<?, K>>();
	private final LinkedBlockingDeque<INode<?, ?>>                     knownThreads         = new LinkedBlockingDeque<INode<?, ?>>();
	private V                                                          outputData;
	private final Set<OutputHook<K, V>>                                outputHooks          = new HashSet<OutputHook<K, V>>();
	
	private Collection<CountDownLatch>                                 outputLatches        = new LinkedList<CountDownLatch>();
	
	private final ConcurrentHashMap<INode<V, ?>, AndamaDataStorage<V>> outputThreads        = new ConcurrentHashMap<INode<V, ?>, AndamaDataStorage<V>>();
	private boolean                                                    parallelizable       = false;
	private final Set<PostExecutionHook<K, V>>                         postExecutionHooks   = new HashSet<PostExecutionHook<K, V>>();
	
	private final Set<PostInputHook<K, V>>                             postInputHooks       = new HashSet<PostInputHook<K, V>>();
	private final Set<PostOutputHook<K, V>>                            postOutputHooks      = new HashSet<PostOutputHook<K, V>>();
	private final Set<PostProcessHook<K, V>>                           postProcessHooks     = new HashSet<PostProcessHook<K, V>>();
	
	// hooks
	private final Set<PreExecutionHook<K, V>>                          preExecutionHooks    = new HashSet<PreExecutionHook<K, V>>();
	private final Set<PreInputHook<K, V>>                              preInputHooks        = new HashSet<PreInputHook<K, V>>();
	private final Set<PreOutputHook<K, V>>                             preOutputHooks       = new HashSet<PreOutputHook<K, V>>();
	
	private final Set<PreProcessHook<K, V>>                            preProcessHooks      = new HashSet<PreProcessHook<K, V>>();
	private final Set<ProcessHook<K, V>>                               processHooks         = new HashSet<ProcessHook<K, V>>();
	
	private final Set<CountDownLatch>                                  processLatches       = new HashSet<CountDownLatch>();
	private final ISettings                                            settings;
	
	private boolean                                                    shutdown;
	
	private boolean                                                    skipData             = false;
	private final Group                                                threadGroup;
	private Integer                                                    threadID             = -1;
	private final boolean                                              waitForLatch         = false;
	private boolean                                                    warningSameInputdata = false;
	
	/**
	 * The constructor of the {@link Node}. This should be called from all extending classes.
	 * 
	 * @param threadGroup
	 *            the {@link Group}. See {@link Group} for details.
	 * @param name
	 *            the name of the {@link Group}. See {@link Group} for details.
	 * @param settings
	 *            An instance of RepoSuiteSettings
	 */
	public Node(@NotNull final Group threadGroup, @NotNull final ISettings settings, final boolean parallelizable) {
		super(threadGroup, "default");
		setName(this.getClass().getSimpleName());
		this.parallelizable = parallelizable;
		setThreadID(threadGroup.addThread(this));
		this.threadGroup = threadGroup;
		this.settings = settings;
		
		if (hasInputConnector()) {
			this.inputStorage = new AndamaDataStorage<K>();
			this.inputStorage.registerOutput(this);
		}
		
		setShutdown(false);
		
		CompareCondition.equals(hasInputConnector(),
		                        this.inputStorage != null,
		                        "Either this class has no input connector, then inputStorage must be null, or it has one and inputStorage must not be null. [hasInputConnector(): %s] [inputStorage!=null: %s]",
		                        hasInputConnector(), this.inputStorage != null);
		
		Condition.check(!this.shutdown, "`shutdown` must not be set after constructor.");
		Condition.notNull(this.settings, "`settings` must not be null.");
		Condition.notNull(this.threadGroup, "`threadGroup` must not be null.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addInputHook(net.ownhero .dev.andama.threads.InputHook)
	 */
	@Override
	public final void addInputHook(final InputHook<K, V> hook) {
		getInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addOutputHook(net.ownhero .dev.andama.threads.OutputHook)
	 */
	@Override
	public final void addOutputHook(final OutputHook<K, V> hook) {
		getOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPostExecutionHook(
	 * net.ownhero.dev.andama.threads.PostExecutionHook)
	 */
	@Override
	public final void addPostExecutionHook(final PostExecutionHook<K, V> hook) {
		getPostExecutionHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPostInputHook(net.
	 * ownhero.dev.andama.threads.PostInputHook)
	 */
	@Override
	public final void addPostInputHook(final PostInputHook<K, V> hook) {
		getPostInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPostOutputHook(net
	 * .ownhero.dev.andama.threads.PostOutputHook)
	 */
	@Override
	public final void addPostOutputHook(final PostOutputHook<K, V> hook) {
		getPostOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPostProcessHook(net
	 * .ownhero.dev.andama.threads.PostProcessHook)
	 */
	@Override
	public final void addPostProcessHook(final PostProcessHook<K, V> hook) {
		getPostProcessHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPreExecutionHook(net
	 * .ownhero.dev.andama.threads.PreExecutionHook)
	 */
	@Override
	public final void addPreExecutionHook(final PreExecutionHook<K, V> hook) {
		getPreExecutionHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPreInputHook(net.ownhero
	 * .dev.andama.threads.PreInputHook)
	 */
	@Override
	public final void addPreInputHook(final PreInputHook<K, V> hook) {
		getPreInputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPreOutputHook(net.
	 * ownhero.dev.andama.threads.PreOutputHook)
	 */
	@Override
	public final void addPreOutputHook(final PreOutputHook<K, V> hook) {
		getPreOutputHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addPreProcessHook(net
	 * .ownhero.dev.andama.threads.PreProcessHook)
	 */
	@Override
	public final void addPreProcessHook(final PreProcessHook<K, V> hook) {
		getPreProcessHooks().add(hook);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#addProcessHook(net.ownhero .dev.andama.threads.ProcessHook)
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
	
	@Override
	public void addWaitForLatch(final CountDownLatch latch) {
		this.awaitingLatches.add(latch);
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
	public final int compareTo(final Node<?, ?> o) {
		final AndamaThreadComparator comparator = new AndamaThreadComparator();
		return comparator.compare(this, o);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#connectInput(de
	 * .unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean connectInput(@NotNull final INode<?, K> thread) {
		
		if (hasInputConnector()) {
			this.inputThreads.add(thread);
			this.knownThreads.add(thread);
			
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#connectOutput(de
	 * .unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean connectOutput(@NotNull final INode<V, ?> thread) {
		if (hasOutputConnector() && !this.outputThreads.containsKey(thread)) {
			this.knownThreads.add(thread);
			
			final AndamaDataStorage<V> storage = thread.getInputStorage();
			storage.registerInput(this);
			this.outputThreads.put(thread, storage);
			
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#disconnectInput
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final void disconnectInput(@NotNull final INode<?, K> thread) {
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#disconnectOutput
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final void disconnectOutput(@NotNull final INode<V, ?> thread) {
		if (hasOutputConnector()) {
			if (this.outputThreads.containsKey(thread)) {
				this.outputThreads.get(thread).unregisterInput(this);
				this.outputThreads.remove(thread);
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
		
		final ArrayList<INode<V, ?>> outputThreadables = new ArrayList<INode<V, ?>>(this.outputThreads.size());
		outputThreadables.addAll(this.outputThreads.keySet());
		
		for (final INode<V, ?> outputThread : outputThreadables) {
			if (Logger.logDebug()) {
				Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
			}
			outputThread.disconnectInput(this);
			this.outputThreads.remove(outputThread);
		}
		INode<?, K> inputThread = null;
		
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
		final ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[0];
	}
	
	/**
	 * @return the inputData
	 */
	public final K getInputData() {
		final K localCache = this.inputDataTuple != null
		                                                ? this.inputDataTuple.getFirst()
		                                                : null;
		Class<?> caller;
		try {
			caller = JavaUtils.getCallingClass();
			
			if (caller != Node.class) {
				if (!this.warningSameInputdata && this.inputCache.containsKey(caller) && (localCache != null)) {
					if (this.inputCache.get(caller) == localCache) {
						if (Logger.logWarn()) {
							if (Logger.logDebug()) {
								final Throwable throwable = new Throwable();
								throwable.fillInStackTrace();
								
								Logger.debug("Caller: " + caller.getSimpleName(), throwable);
							}
							Logger.warn("Multiple request of input data element (" + localCache.toString()
							        + ") within " + this.toString());
							Logger.warn("This might not be an issue if you call " + JavaUtils.getThisMethodName()
							        + " multiple times within one hook.");
							Logger.warn("Though, make sure your hooks set the completed flag after having processed your input element.");
							Logger.warn("Otherwise you will read the same input data in the next around again.");
							Logger.warn("The completed flag can be set by either setting it explicitly with the according setter or");
							Logger.warn("Calling the provide output data method instead of the provide partial output data.");
							Logger.warn("This warning will be suppressed for the remaining execution.");
						}
						this.warningSameInputdata = true;
					}
				}
				
				this.inputCache.put(caller, localCache);
			}
		} catch (final ClassNotFoundException e) {
			if (Logger.logError()) {
				Logger.error("Determining calling class failed.", e);
			}
		}
		
		return localCache;
	}
	
	/**
	 * @return the inputHooks
	 */
	protected final Set<InputHook<K, V>> getInputHooks() {
		return this.inputHooks;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#getInputStorage()
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
	public final Collection<INode<?, K>> getInputThreads() {
		final LinkedList<INode<?, K>> list = new LinkedList<INode<?, K>>();
		
		if (isInputConnected()) {
			list.addAll(this.inputThreads);
		}
		
		return list;
	}
	
	/**
	 * @return
	 */
	@Override
	public final Class<?> getInputType() {
		if (hasInputConnector()) {
			return getTypeArguments(Node.class, getClass()).get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @param thread
	 * @return the type of the output chunks of the given thread
	 */
	public final Type getOutputClassType() {
		final ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
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
	 * @see net.ownhero.dev.andama.model.AndamaThreadable#getOutputThreads()
	 */
	@Override
	public final Collection<INode<V, ?>> getOutputThreads() {
		final LinkedList<INode<V, ?>> list = new LinkedList<INode<V, ?>>();
		
		if (isOutputConnected()) {
			list.addAll(this.outputThreads.keySet());
		}
		
		return list;
	}
	
	/**
	 * @return
	 */
	@Override
	public final Class<?> getOutputType() {
		if (hasOutputConnector()) {
			final List<Class<?>> list = getTypeArguments(Node.class, getClass());
			return list.get(list.size() - 1);
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
	protected final ISettings getSettings() {
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
	
	@Override
	public Integer getThreadID() {
		return this.threadID;
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
	 * Requests the current size of the input storage. Make sure there is a valid input storage, i.e. you are using an
	 * implementation where {@link Node#hasInputConnector()} is true.
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isInputConnected()
	 */
	@Override
	public final boolean isInputConnected() {
		return !hasInputConnector() || (this.inputThreads.size() > 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isInputConnected
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean isInputConnected(final INode<?, K> thread) {
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isOutputConnected()
	 */
	@Override
	public final boolean isOutputConnected() {
		return !hasOutputConnector() || (this.outputThreads.size() > 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#isOutputConnected
	 * (de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread)
	 */
	@Override
	public final boolean isOutputConnected(final INode<V, ?> thread) {
		return (this.outputThreads.contains(thread));
	}
	
	/**
	 * @return the parallelizable
	 */
	public final boolean isParallelizable() {
		return this.parallelizable;
	}
	
	/**
	 * @return true if {@link INode#shutdown()} has already been called on this object; false otherwise. The shutdown
	 *         method can also be called internally, after an error occurred.
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
	 * @return
	 */
	private final boolean processingCompleted() {
		return Hook.allCompleted(getProcessHooks());
	}
	
	/**
	 * @return the next chunk from the inputStorage. Will be null if there isn't any input left and no writers are
	 *         attached to the storage anymore.
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
			
			// add dependency latches
			if (!this.dependencies.isEmpty()) {
				for (final Node<?, ?> key : this.dependencies.keySet()) {
					if (this.dependencies.get(key) == null) {
						final CountDownLatch value = new CountDownLatch(1);
						this.dependencies.put(key, value);
						key.addWaitForLatch(value);
					}
				}
			}
			
			// wait for dependency threads
			if (!this.dependencies.isEmpty()) {
				for (final Node<?, ?> key : this.dependencies.keySet()) {
					if (!key.isShutdown()) {
						this.dependencies.get(key).await();
					}
				}
			}
			
			// PREEXECUTION HOOKS
			if (!getPreExecutionHooks().isEmpty()) {
				if (Logger.logDebug()) {
					Logger.debug("Starting [preExecution] hook(s): "
					        + JavaUtils.collectionToString(getPreExecutionHooks()));
				}
				
				for (final PreExecutionHook<K, V> hook : getPreExecutionHooks()) {
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
				if (hasInputConnector() && processingCompleted()) {
					if (!getPreInputHooks().isEmpty()) {
						if (Logger.logDebug()) {
							Logger.debug("Starting [preInput] hook(s): "
							        + JavaUtils.collectionToString(getPreInputHooks()));
						}
						for (final PreInputHook<K, V> hook : getPreInputHooks()) {
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
						
						for (final InputHook<K, V> hook : getInputHooks()) {
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
						
						for (final PostInputHook<K, V> hook : getPostInputHooks()) {
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
						break;
					}
				}
				
				// PREPROCESS HOOKS
				if (!getPreProcessHooks().isEmpty()) {
					if (Logger.logDebug()) {
						Logger.debug("Starting [preProcess] hook(s): "
						        + JavaUtils.collectionToString(getPreProcessHooks()));
					}
					
					for (final PreProcessHook<K, V> hook : getPreProcessHooks()) {
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
					
					for (final ProcessHook<K, V> hook : getProcessHooks()) {
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
					
					for (final PostProcessHook<K, V> hook : getPostProcessHooks()) {
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
				
				if (!isSkipData()) {
					if (hasOutputConnector() && isOutputConnected()) {
						if (getOutputData() != null) {
							// PREOUTPUT HOOKS
							if (!getPreOutputHooks().isEmpty()) {
								if (Logger.logDebug()) {
									Logger.debug("Starting [preOutput] hook(s): "
									        + JavaUtils.collectionToString(getPreOutputHooks()));
								}
								
								for (final PreOutputHook<K, V> hook : getPreOutputHooks()) {
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
								
								for (final OutputHook<K, V> hook : getOutputHooks()) {
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
								
								for (final PostOutputHook<K, V> hook : getPostOutputHooks()) {
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
						} else {
							
							if (Logger.logDebug()) {
								Logger.debug("Output data is null.");
							}
						}
					}
				} else {
					
					if (Logger.logDebug()) {
						Logger.debug("Skipping data on request.");
					}
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
				
				for (final PostExecutionHook<K, V> hook : getPostExecutionHooks()) {
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
			
			for (final CountDownLatch latch : this.awaitingLatches) {
				if (latch.getCount() > 0) {
					latch.countDown();
				}
			}
		} catch (final Exception e) {
			
			if (Logger.logError()) {
				Logger.error("Caught exception: " + e.getClass().getSimpleName());
				Logger.error(e.getMessage(), e);
				Logger.error("Shutting down.");
			}
		} finally {
			finish();
			for (final CountDownLatch latch : this.awaitingLatches) {
				if (latch.getCount() > 0) {
					latch.countDown();
				}
			}
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
	 * @see de.unisaarland.cs.st.reposuite.RepoSuiteGeneralThread#setInputStorage
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
	 * Set the current shutdown status. This method only sets the variable. Call {@link Node#shutdown()} to initiate a
	 * proper shutdown.
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
	
	private void setThreadID(final Integer threadID) {
		this.threadID = threadID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#setUncaughtExceptionHandler(java.lang.Thread. UncaughtExceptionHandler)
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
			
			final ArrayList<INode<V, ?>> outputThreadables = new ArrayList<INode<V, ?>>(this.outputThreads.size());
			outputThreadables.addAll(this.outputThreads.keySet());
			
			for (final INode<V, ?> outputThread : outputThreadables) {
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from output thread: " + outputThread.getHandle());
				}
				outputThread.disconnectInput(this);
				this.outputThreads.remove(outputThread);
			}
			
			Node<?, K> inputThread = null;
			
			while ((inputThread = (Node<?, K>) this.inputThreads.poll()) != null) {
				if (Logger.logDebug()) {
					Logger.debug("Disconnecting from input thread: " + inputThread.getHandle());
				}
				inputThread.disconnectOutput(this);
			}
			
			Node<?, ?> thread = null;
			
			while ((thread = (Node<?, ?>) this.knownThreads.poll()) != null) {
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
		final StringBuilder builder = new StringBuilder();
		
		builder.append('[').append(getThreadGroup().getName()).append("] ");
		
		builder.append(getHandle());
		
		if ((this.getClass().getSuperclass() != null) && Node.class.isAssignableFrom(this.getClass().getSuperclass())) {
			builder.append(' ').append(this.getClass().getSuperclass().getSimpleName());
		}
		
		builder.append(' ');
		
		final StringBuilder typeBuilder = new StringBuilder();
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#waitFor(net.ownhero.dev .andama.threads.AndamaThread)
	 */
	@Override
	public void waitFor(final Node<?, ?> thread) {
		this.dependencies.put(thread, null);
	}
	
	/**
	 * Writes a chunk to the output storage. Make sure to call this only if {@link Node#hasOutputConnector()} is true.
	 * 
	 * @param data
	 *            a chunk of data, not null
	 * @return
	 * @throws InterruptedException
	 */
	final Collection<CountDownLatch> write(final V data) throws InterruptedException {
		Condition.notNull(data, "[write] `data` should not be null.");
		Condition.check(hasOutputConnector(), "[write] `hasOutputConnector()` should be true, but is: %s",
		                hasOutputConnector());
		
		if (Logger.logTrace()) {
			Logger.trace("writing data: " + data);
		}
		
		final LinkedList<CountDownLatch> latches = new LinkedList<CountDownLatch>();
		
		for (final INode<V, ?> thread : this.outputThreads.keySet()) {
			latches.add(this.outputThreads.get(thread).write(data));
		}
		return latches;
	}
	
	/**
	 * @param data
	 * @throws InterruptedException
	 */
	final void writeOutputData(final V data) throws InterruptedException {
		this.outputLatches = write(data);
		
		if (this.isWaitForLatch()) {
			if (Logger.logDebug()) {
				Logger.debug("Waiting for latch to be resolved.");
			}
			
			for (final CountDownLatch latch : this.outputLatches) {
				latch.await();
			}
		}
	}
	
}
