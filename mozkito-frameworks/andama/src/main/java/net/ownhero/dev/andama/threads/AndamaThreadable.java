/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.threads;

import java.util.Collection;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.storages.AndamaDataStorage;

/**
 * Specification of a thread in the {@link AndamaChain}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
public interface AndamaThreadable<K, V> {
	
	public void afterExecution();
	
	public void afterProcess();
	
	public void beforeExecution();
	
	public void beforeProcess();
	
	/**
	 * @return true if there are no glitches found in the connector setup.
	 */
	public boolean checkConnections();
	
	/**
	 * @return true if the thread hasn't been shutdown.
	 */
	public boolean checkNotShutdown();
	
	/**
	 * @param thread
	 *            may not be null
	 * @return true on success
	 */
	public boolean connectInput(AndamaThreadable<?, K> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 * @return true on success
	 */
	public boolean connectOutput(AndamaThreadable<V, ?> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 */
	public void disconnectInput(AndamaThreadable<?, K> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 */
	public void disconnectOutput(AndamaThreadable<V, ?> thread);
	
	/**
	 * this method does the same as shutdown, but does not shutdown the other
	 * threads in the chain.
	 */
	public void finish();
	
	/**
	 * @return the simple class name of the actual instance
	 */
	public String getHandle();
	
	/**
	 * @return the input storage of the instance. Can be null.
	 */
	public AndamaDataStorage<K> getInputStorage();
	
	/**
	 * @return
	 */
	public Collection<AndamaThreadable<?, K>> getInputThreads();
	
	/**
	 * @return the output storage of the instance. Can be null.
	 */
	public AndamaDataStorage<V> getOutputStorage();
	
	/**
	 * @return
	 */
	public Collection<AndamaThreadable<V, ?>> getOutputThreads();
	
	/**
	 * @return true if the object has an input connector-false otherwise
	 */
	public boolean hasInputConnector();
	
	/**
	 * @return true if the object has an output connector-false otherwise
	 */
	public boolean hasOutputConnector();
	
	/**
	 * @return true if there are any writer threads connected to the input
	 *         source of this object. Will also be true if the object doesn't
	 *         have any input connectors.
	 */
	public boolean isInputConnected();
	
	/**
	 * @param thread
	 *            may not be null
	 * @return true if the object is connected to the given thread. This will
	 *         explicitly return false if there are no input connectors on this
	 *         object.
	 */
	public boolean isInputConnected(AndamaThreadable<?, K> thread);
	
	/**
	 * @return true if there are any reader threads connected to the output sink
	 *         of this object. Will also be true if the object doens't have any
	 *         output connectors.
	 */
	public boolean isOutputConnected();
	
	/**
	 * @param thread
	 *            may not be null
	 * @return true, if the object is connected to the given thread-false
	 *         otherwise. This will explicitly return false if there are no
	 *         output connectors in this object.
	 */
	public boolean isOutputConnected(AndamaThreadable<V, ?> thread);
	
	/**
	 * @return true if {@link AndamaThreadable#shutdown()} has already
	 *         been called on this object; false otherwise. The shutdown method
	 *         can also be called internally, after an error occured.
	 */
	public boolean isShutdown();
	
	public V process(K data);
	
	/**
	 * Sets the input storage of the object. In case
	 * {@link AndamaThreadable#hasInputConnector()} returns false, this
	 * method won't do anything.
	 * 
	 * @param storage
	 *            may not be null
	 */
	public void setInputStorage(final AndamaDataStorage<K> storage);
	
	/**
	 * Sets the output storage of the object. In case
	 * {@link AndamaThreadable#hasOutputConnector()} returns false, this
	 * method won't do anything.
	 * 
	 * @param storage
	 *            may not be null
	 */
	public void setOutputStorage(final AndamaDataStorage<V> storage);
	
	/**
	 * shuts down the current thread
	 */
	public void shutdown();
}
