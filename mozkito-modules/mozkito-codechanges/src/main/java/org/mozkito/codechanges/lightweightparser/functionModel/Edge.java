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

/**
 * The Class Edge.
 */
public class Edge {
	
	/** The edge label: an event. */
	protected Event event;
	
	/** The node that the edge comes from. */
	protected Node  from;
	
	/** The node that the edge goes to. */
	protected Node  to;
	
	/**
	 * Instantiates a new edge.
	 * 
	 * @param event
	 *            the event
	 */
	public Edge(final Event event) {
		super();
		this.event = event;
		this.from = null;
		this.to = null;
		
	}
	
	/**
	 * Instantiates a new edge.
	 * 
	 * @param event
	 *            the event
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public Edge(final Event event, final Node from, final Node to) {
		this.event = event;
		this.from = from;
		this.to = to;
		from.addOutEdge(this);
		to.addInEdge(this);
		
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
		final Edge other = (Edge) obj;
		if (this.event == null) {
			if (other.event != null) {
				return false;
			}
		} else if (!this.event.equals(other.event)) {
			return false;
		}
		if (this.from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!this.from.equals(other.from)) {
			return false;
		}
		if (this.to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!this.to.equals(other.to)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the event which this edge is labeled with.
	 * 
	 * @return the event
	 */
	public Event getEvent() {
		return this.event;
	}
	
	/**
	 * Gets the node which this edge comes from.
	 * 
	 * @return the from
	 */
	public Node getFrom() {
		return this.from;
	}
	
	/**
	 * Gets the node which this edge points to.
	 * 
	 * @return the to
	 */
	public Node getTo() {
		return this.to;
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
		result = (prime * result) + ((this.event == null)
		                                                 ? 0
		                                                 : this.event.hashCode());
		result = (prime * result) + ((this.from == null)
		                                                ? 0
		                                                : this.from.hashCode());
		result = (prime * result) + ((this.to == null)
		                                              ? 0
		                                              : this.to.hashCode());
		return result;
	}
	
	/**
	 * Sets the event label for this edge.
	 * 
	 * @param event
	 *            the new event
	 */
	public void setEvent(final Event event) {
		this.event = event;
	}
	
	/**
	 * Sets the from node.
	 * 
	 * @param from
	 *            the new from
	 */
	public void setFrom(final Node from) {
		this.from = from;
	}
	
	/**
	 * Sets the to node.
	 * 
	 * @param to
	 *            the new to
	 */
	public void setTo(final Node to) {
		this.to = to;
	}
	
	/**
	 * Returns a dot representation of this edge.
	 * 
	 * @return the string containing the dot representation
	 */
	public String toDot() {
		return this.from.toString() + " -> " + this.to.toString() + " [label=\"" + this.event.toString() + "\"];";
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
		return this.from.toString() + " -> " + this.event.toString() + " -> " + this.to.toString() + "\n";
	}
	
}
