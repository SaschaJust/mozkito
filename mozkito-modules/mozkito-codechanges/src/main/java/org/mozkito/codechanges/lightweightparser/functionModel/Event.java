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
package org.mozkito.codechanges.lightweightparser.functionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Event. An Event is used to label the edges of function models and represents a function call. Each event
 * stores the name of function that is called and a list of objects associated with the function call.
 */
public class Event {
	
	/** The name of the function that is called. */
	private final String    name;
	
	/** A list of all objects associated with this function call including the type of association. */
	private final List<Obj> objects;
	
	/** The epsilon event. (required for epsilon edges) */
	private static Event    epsilon;
	
	/**
	 * Gets the singleton instance of an epsilon event The epsilon event does not represent a function call and is used
	 * to label epsilon edges.
	 * 
	 * @return the epsilon event
	 */
	public static Event getEpsilon() {
		if (Event.epsilon == null) {
			Event.epsilon = new Event("");
		}
		return Event.epsilon;
	}
	
	/**
	 * Instantiates a new event. The list of associated objects is set to an empty list.
	 * 
	 * @param name
	 *            the name of the function that is called
	 */
	public Event(final String name) {
		super();
		this.name = name;
		this.objects = new ArrayList<Obj>();
	}
	
	/**
	 * Instantiates a new event.
	 * 
	 * @param name
	 *            the name of the function that is called
	 * @param objects
	 *            the objects associated with the function call
	 */
	public Event(final String name, final List<Obj> objects) {
		super();
		this.name = name.trim();
		this.objects = objects;
	}
	
	/**
	 * Adds an object to the list of associated objects.
	 * 
	 * @param obj
	 *            the object to be added
	 */
	public void addObject(final Obj obj) {
		this.objects.add(obj);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Event other = (Event) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.objects == null) {
			if (other.objects != null) {
				return false;
			}
		} else if (!this.objects.equals(other.objects)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the name of the function that is called.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the list of associated objects.
	 * 
	 * @return the objects
	 */
	public List<Obj> getObjects() {
		return this.objects;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null)
		                                                ? 0
		                                                : this.name.hashCode());
		result = (prime * result) + ((this.objects == null)
		                                                   ? 0
		                                                   : this.objects.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this == Event.epsilon) {
			return "EPSILON";
		}
		return this.name + " :" + this.objects.toString();
	}
}
