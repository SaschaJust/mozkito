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

package org.mozkito.infozilla.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * The Class Region.
 */
@Embeddable
public class Region implements Comparable<Region> {
	
	/** The from. */
	private Integer from;
	
	/** The to. */
	private Integer to;
	
	/**
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public Region() {
		// stub
	}
	
	/**
	 * Instantiates a new region.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public Region(final Integer from, final Integer to) {
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
				if (getFrom().equals(o.getFrom())) {
					return getTo() - o.getTo();
				} else {
					return getFrom() - o.getFrom();
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
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		final Region other = (Region) obj;
		if (!getFrom().equals(other.getFrom())) {
			return false;
		}
		if (!getTo().equals(other.getTo())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	@Basic
	public Integer getFrom() {
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
	 * Gets the to.
	 * 
	 * @return the to
	 */
	@Basic
	public Integer getTo() {
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
		result = (prime * result) + getFrom();
		result = (prime * result) + getTo();
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
					return new Region(getFrom(), Math.max(getTo(), other.getTo()));
				} else {
					return new Region(other.getFrom(), Math.max(getTo(), other.getTo()));
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
			return getTo() >= other.getFrom();
		} else {
			return getFrom() <= other.getTo();
		}
	}
	
	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(final Integer from) {
		this.from = from;
	}
	
	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(final Integer to) {
		this.to = to;
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
		builder.append(getFrom());
		builder.append(", to=");
		builder.append(getTo());
		builder.append("]");
		
		return builder.toString();
	}
	
}
