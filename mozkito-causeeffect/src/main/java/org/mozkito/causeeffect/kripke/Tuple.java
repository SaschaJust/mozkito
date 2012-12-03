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

package org.mozkito.causeeffect.kripke;

/**
 * The Class Tuple.
 *
 * @param <A> the generic type
 * @param <B> the generic type
 */
public class Tuple<A, B> {
	
	/** The first. */
	public A first;
	
	/** The second. */
	public B second;
	
	/**
	 * Instantiates a new tuple.
	 *
	 * @param first the first
	 * @param second the second
	 */
	public Tuple(final A first, final B second) {
		this.first = first;
		this.second = second;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Tuple other = (Tuple) obj;
		if (this.first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!this.first.equals(other.first)) {
			return false;
		}
		if (this.second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!this.second.equals(other.second)) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.first == null)
		                                                 ? 0
		                                                 : this.first.hashCode());
		result = (prime * result) + ((this.second == null)
		                                                  ? 0
		                                                  : this.second.hashCode());
		return result;
	}
	
}
