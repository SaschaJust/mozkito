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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <K>
 * @param <M>
 */
public class Tuple<K, M> {
	
	private K first;
	private M second;
	
	public Tuple(K f, M s) {
		this.first = f;
		this.second = s;
	}
	
	/**
	 * @return the first
	 */
	public K getFirst() {
		return this.first;
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return the second
	 */
	public M getSecond() {
		return this.second;
	}
	
	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(K first) {
		this.first = first;
	}
	
	/**
	 * @param second
	 *            the second to set
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
