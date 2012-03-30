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

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import net.ownhero.dev.andama.messages.EventBus;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.storages.AndamaDataStorage;

/**
 * Specification of a thread in the {@link Chain}.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface INode<K, V> extends Runnable {
	
	/**
	 * Adds a new {@link InputHook} to the {@link INode} object. Default hooks should be replaced, if present.
	 * 
	 * @param hook
	 *            the {@link InputHook} to be added.
	 */
	public void addInputHook(InputHook<K, V> hook);
	
	/**
	 * Adds a new {@link OutputHook} to the {@link INode} object. Default hooks should be replaced, if present.
	 * 
	 * @param hook
	 *            the {@link OutputHook} to be added.
	 */
	public void addOutputHook(OutputHook<K, V> hook);
	
	/**
	 * Adds a new {@link PostExecutionHook} to the {@link INode} object. Default hooks should be replaced, if present.
	 * 
	 * @param hook
	 *            the {@link PostExecutionHook} to be added.
	 */
	public void addPostExecutionHook(PostExecutionHook<K, V> hook);
	
	/**
	 * Adds the post input hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPostInputHook(PostInputHook<K, V> hook);
	
	/**
	 * Adds the post output hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPostOutputHook(PostOutputHook<K, V> hook);
	
	/**
	 * Adds the post process hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPostProcessHook(PostProcessHook<K, V> hook);
	
	/**
	 * Adds the pre execution hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPreExecutionHook(PreExecutionHook<K, V> hook);
	
	/**
	 * Adds the pre input hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPreInputHook(PreInputHook<K, V> hook);
	
	/**
	 * Adds the pre output hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPreOutputHook(PreOutputHook<K, V> hook);
	
	/**
	 * Adds the pre process hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addPreProcessHook(PreProcessHook<K, V> hook);
	
	/**
	 * Adds the process hook.
	 * 
	 * @param hook
	 *            the hook
	 */
	public void addProcessHook(ProcessHook<K, V> hook);
	
	/**
	 * Adds a latch to notify threads that depend on this one to be finished.
	 * 
	 * @param latch
	 *            the {@link CountDownLatch}
	 */
	public void addWaitForLatch(final CountDownLatch latch);
	
	/**
	 * Check connections.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean checkConnections();
	
	/**
	 * Connect input.
	 * 
	 * @param thread
	 *            may not be null
	 * @return true on success
	 */
	public boolean connectInput(INode<?, K> thread);
	
	/**
	 * Connect output.
	 * 
	 * @param thread
	 *            may not be null
	 * @return true on success
	 */
	public boolean connectOutput(INode<V, ?> thread);
	
	/**
	 * Disconnect input.
	 * 
	 * @param thread
	 *            may not be null
	 */
	public void disconnectInput(INode<?, K> thread);
	
	/**
	 * Disconnect output.
	 * 
	 * @param thread
	 *            may not be null
	 */
	public void disconnectOutput(INode<V, ?> thread);
	
	/**
	 * this method does the same as shutdown, but does not shutdown the other threads in the chain.
	 */
	public void finish();
	
	/**
	 * Gets the base type.
	 * 
	 * @return the base type
	 */
	@SuppressWarnings ("rawtypes")
	public Class<? extends Node> getBaseType();
	
	/**
	 * Gets the event bus.
	 * 
	 * @return the event bus
	 */
	EventBus getEventBus();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name of the actual instance
	 */
	public String getHandle();
	
	/**
	 * Gets the input storage.
	 * 
	 * @return the input storage of the instance. Can be null.
	 */
	public AndamaDataStorage<K> getInputStorage();
	
	/**
	 * Gets the input threads.
	 * 
	 * @return the input threads
	 */
	public Collection<INode<?, K>> getInputThreads();
	
	/**
	 * Gets the input type.
	 * 
	 * @return the input type
	 */
	public Class<?> getInputType();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the output threads.
	 * 
	 * @return the output threads
	 */
	public Collection<INode<V, ?>> getOutputThreads();
	
	/**
	 * Gets the output type.
	 * 
	 * @return the output type
	 */
	public Class<?> getOutputType();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#getStackTrace()
	 */
	/**
	 * Gets the stack trace.
	 * 
	 * @return the stack trace
	 */
	public abstract StackTraceElement[] getStackTrace();
	
	/**
	 * Gets the thread id.
	 * 
	 * @return the internal thread id (within the {@link Group}).
	 */
	Integer getThreadID();
	
	/**
	 * Checks for input connector.
	 * 
	 * @return true if the object has an input connector-false otherwise
	 */
	public boolean hasInputConnector();
	
	/**
	 * Checks for output connector.
	 * 
	 * @return true if the object has an output connector-false otherwise
	 */
	public boolean hasOutputConnector();
	
	/**
	 * Checks if is input connected.
	 * 
	 * @return true if there are any writer threads connected to the input source of this object. Will also be true if
	 *         the object doesn't have any input connectors.
	 */
	public boolean isInputConnected();
	
	/**
	 * Checks if is input connected.
	 * 
	 * @param thread
	 *            may not be null
	 * @return true if the object is connected to the given thread. This will explicitly return false if there are no
	 *         input connectors on this object.
	 */
	public boolean isInputConnected(INode<?, K> thread);
	
	/**
	 * Checks if is output connected.
	 * 
	 * @return true if there are any reader threads connected to the output sink of this object. Will also be true if
	 *         the object doens't have any output connectors.
	 */
	public boolean isOutputConnected();
	
	/**
	 * Checks if is output connected.
	 * 
	 * @param thread
	 *            may not be null
	 * @return true, if the object is connected to the given thread-false otherwise. This will explicitly return false
	 *         if there are no output connectors in this object.
	 */
	public boolean isOutputConnected(INode<V, ?> thread);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sets the input storage of the object. In case {@link INode#hasInputConnector()} returns false, this method won't
	 * do anything.
	 * 
	 * @param storage
	 *            may not be null
	 */
	public void setInputStorage(final AndamaDataStorage<K> storage);
	
	/**
	 * shuts down the current thread.
	 */
	public void shutdown();
	
	/**
	 * Skip data.
	 * 
	 * @return true, if successful
	 */
	public boolean skipData();
	
	/**
	 * Requires the given thread to be finished before this one is executed.
	 * 
	 * @param thread
	 *            the dependency
	 */
	public void waitFor(Node<?, ?> thread);
}
