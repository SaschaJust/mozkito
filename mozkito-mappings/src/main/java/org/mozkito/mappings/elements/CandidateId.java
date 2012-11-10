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
package org.mozkito.mappings.elements;

/**
 * The Class CandidateId.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidateId {
	
	/** The from. */
	long from;
	
	/** The to. */
	long to;
	
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
		final CandidateId other = (CandidateId) obj;
		if (this.from != other.from) {
			return false;
		}
		if (this.to != other.to) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public long getFrom() {
		
		return this.from;
		
	}
	
	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public long getTo() {
		
		return this.to;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (this.from ^ (this.from >>> 32));
		result = (prime * result) + (int) (this.to ^ (this.to >>> 32));
		return result;
	}
	
	/**
	 * Sets the from.
	 * 
	 * @param from
	 *            the new from
	 */
	public void setFrom(final long from) {
		
		this.from = from;
		
	}
	
	/**
	 * Sets the to.
	 * 
	 * @param to
	 *            the new to
	 */
	public void setTo(final long to) {
		
		this.to = to;
	}
}
