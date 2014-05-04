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
package org.mozkito.codechanges.lightweightparser.constraints;

/**
 * The Class Constraint.
 */
public class Constraint implements Comparable<Constraint> {
	
	/** The first. */
	String first;
	
	/** The second. */
	String second;
	
	/** The both. */
	String both;
	
	/** The id. */
	long   id;
	
	/**
	 * Instantiates a new constraint.
	 * 
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 */
	public Constraint(final String first, final String second) {
		super();
		this.first = first;
		this.second = second;
		this.both = first + second;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Constraint o) {
		if (o.id == this.id) {
			return 0;
		}
		if (this.id < o.id) {
			return -1;
		}
		return 1;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Constraint)) {
			return false;
		}
		
		final Constraint c = (Constraint) o;
		
		return this.both.equals(c.both);
	}
	
	/**
	 * Gets the gra string.
	 * 
	 * @return the gra string
	 */
	public String getGraString() {
		return "p" + this.id + ": " + this.first + "->" + this.second + "\n";
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getID() {
		return "p" + this.id;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.both.hashCode();
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setID(final long id) {
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.first + " < " + this.second;
	}
	
}
