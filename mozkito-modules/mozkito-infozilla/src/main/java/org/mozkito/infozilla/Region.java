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

package org.mozkito.infozilla;

/**
 * The Class Region.
 */
public class Region implements Comparable<Region> {
	
	/** The from. */
	private final int from;
	
	/** The to. */
	private final int to;
	
	/**
	 * Instantiates a new region.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public Region(final int from, final int to) {
		PRECONDITIONS: {
			
			if (from < 0) {
				throw new IllegalArgumentException();
			}
			if (to <= 0) {
				throw new IllegalArgumentException();
			}
		}
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Region o) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (equals(o)) {
				return 0;
			} else {
				if (this.from == o.from) {
					return this.to - o.to;
				} else {
					return this.from - o.from;
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		final Region other = (Region) obj;
		if (this.from != other.from) {
			return false;
		}
		if (this.to != other.to) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the from
	 */
	public int getFrom() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.from;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the to
	 */
	public int getTo() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.to;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		result = (prime * result) + this.from;
		result = (prime * result) + this.to;
		return result;
	}
	
	/**
	 * Merge.
	 * 
	 * @param other
	 *            the other
	 * @return the region
	 */
	public Region merge(final Region other) {
		PRECONDITIONS: {
			if (!overlaps(other)) {
				throw new IllegalArgumentException();
			}
		}
		
		try {
			if (equals(other)) {
				return this;
			} else {
				if (compareTo(other) < 0) {
					return new Region(this.from, Math.max(this.to, other.to));
				} else {
					return new Region(other.from, Math.max(this.to, other.to));
				}
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Overlaps.
	 * 
	 * @param other
	 *            the other
	 * @return true, if successful
	 */
	public boolean overlaps(final Region other) {
		if (compareTo(other) < 0) {
			return this.to >= other.from;
		} else {
			return this.from <= other.to;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Region [from=");
		builder.append(this.from);
		builder.append(", to=");
		builder.append(this.to);
		builder.append("]");
		return builder.toString();
	}
	
}
