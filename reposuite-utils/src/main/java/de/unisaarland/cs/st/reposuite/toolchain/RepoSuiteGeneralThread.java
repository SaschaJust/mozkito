/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.toolchain;

/**
 * Specification of a thread in the {@link RepoSuiteToolchain}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <V>
 */
public interface RepoSuiteGeneralThread<K, V> {
	
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
	public boolean connectInput(RepoSuiteGeneralThread<?, K> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 * @return true on success
	 */
	public boolean connectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 */
	public void disconnectInput(RepoSuiteGeneralThread<?, K> thread);
	
	/**
	 * @param thread
	 *            may not be null
	 */
	public void disconnectOutput(RepoSuiteGeneralThread<V, ?> thread);
	
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
	public RepoSuiteDataStorage<K> getInputStorage();
	
	/**
	 * @return the output storage of the instance. Can be null.
	 */
	public RepoSuiteDataStorage<V> getOutputStorage();
	
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
	public boolean isInputConnected(RepoSuiteGeneralThread<?, K> thread);
	
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
	public boolean isOutputConnected(RepoSuiteGeneralThread<V, ?> thread);
	
	/**
	 * @return true if {@link RepoSuiteGeneralThread#shutdown()} has already
	 *         been called on this object; false otherwise. The shutdown method
	 *         can also be called internally, after an error occured.
	 */
	public boolean isShutdown();
	
	/**
	 * Sets the input storage of the object. In case
	 * {@link RepoSuiteGeneralThread#hasInputConnector()} returns false, this
	 * method won't do anything.
	 * 
	 * @param storage
	 *            may not be null
	 */
	public void setInputStorage(final RepoSuiteDataStorage<K> storage);
	
	/**
	 * Sets the output storage of the object. In case
	 * {@link RepoSuiteGeneralThread#hasOutputConnector()} returns false, this
	 * method won't do anything.
	 * 
	 * @param storage
	 *            may not be null
	 */
	public void setOutputStorage(final RepoSuiteDataStorage<V> storage);
	
	/**
	 * shuts down the current thread
	 */
	public void shutdown();
}
