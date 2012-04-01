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

import org.joda.time.DateTime;

/**
 * The Interface IEvent.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public interface IEvent {
	
	/**
	 * Accept.
	 */
	void done();
	
	/**
	 * Adds the callback.
	 * 
	 * @param callback
	 *            the callback
	 */
	void addCallback(Callback callback);
	
	/**
	 * Gets the fired.
	 * 
	 * @return the fired
	 */
	DateTime getFired();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	String getHandle();
	
	/**
	 * Gets the issued.
	 * 
	 * @return the issued
	 */
	DateTime getIssued();
	
	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	AccessLevel getLevel();
	
	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	String getLocation();
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	String getMessage();
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	Class<?> getOrigin();
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	EventType getType();
	
	/**
	 * Sets the fired.
	 */
	void fired();
}
