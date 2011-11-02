/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.clustering;

import java.util.HashSet;
import java.util.Set;


public class VirtualCluster<T> extends Cluster<T> {
	
	private final T t;
	
	public VirtualCluster(final T t) {
		super(0d);
		this.t = t;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VirtualCluster other = (VirtualCluster) obj;
		if (this.t == null) {
			if (other.t != null) {
				return false;
			}
		} else if (!this.t.equals(other.t)) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.Cluster#getAllElements()
	 */
	@Override
	public Set<T> getAllElements() {
		Set<T> result = new HashSet<T>();
		result.add(this.t);
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.t == null) ? 0 : this.t.hashCode());
		return result;
	}
	
}
