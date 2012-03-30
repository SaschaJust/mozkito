/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package net.ownhero.dev.andama.messages;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;

/**
 * The Class EventBus.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class EventBus {
	
	/** The listeners. */
	List<IEventListener> listeners = new LinkedList<IEventListener>();
	
	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(final IEventListener listener) {
		if (Logger.logTrace()) {
			Logger.trace(String.format("Adding new EventListener '%s'.", listener));
		}
		this.listeners.add(listener);
	}
	
	/**
	 * Fire event.
	 * 
	 * @param event
	 *            the event
	 */
	public void fireEvent(final IEvent event) {
		event.fired();
		
		for (final IEventListener listener : this.listeners) {
			listener.handle(event);
		}
		
		event.done();
	}
}
