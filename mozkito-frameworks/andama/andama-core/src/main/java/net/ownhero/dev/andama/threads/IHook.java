/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package net.ownhero.dev.andama.threads;

import net.ownhero.dev.andama.messages.EventBus;

/**
 * The Interface IHook.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface IHook<K, V> {
	
	/**
	 * Completed.
	 * 
	 * @return true, if successful
	 */
	public boolean completed();
	
	/**
	 * Execute.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void execute() throws InterruptedException;
	
	/**
	 * Gets the event bus.
	 * 
	 * @return the event bus
	 */
	EventBus getEventBus();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public String getHandle();
	
	/**
	 * should be implemented as 'final'.
	 * 
	 * @return the thread
	 */
	public Node<K, V> getThread();
}
