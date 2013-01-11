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

package net.ownhero.dev.ioda;

/**
 * The Class Tuple.
 *
 * @param <K> the key type
 * @param <M> the generic type
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Tuple<K, M> {
	
	/** The first. */
	private K first;
	
	/** The second. */
	private M second;
	
	/**
	 * Instantiates a new tuple.
	 *
	 * @param f the f
	 * @param s the s
	 */
	public Tuple(K f, M s) {
		this.first = f;
		this.second = s;
	}
	
	/**
	 * Gets the first.
	 *
	 * @return the first
	 */
	public K getFirst() {
		return this.first;
	}
	
	/**
	 * Gets the class name.
	 *
	 * @return the simple class name
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Gets the second.
	 *
	 * @return the second
	 */
	public M getSecond() {
		return this.second;
	}
	
	/**
	 * Sets the first.
	 *
	 * @param first the first to set
	 */
	public void setFirst(K first) {
		this.first = first;
	}
	
	/**
	 * Sets the second.
	 *
	 * @param second the second to set
	 */
	public void setSecond(M second) {
		this.second = second;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tuple [first=" + this.first + ", second=" + this.second + "]";
	}
}
