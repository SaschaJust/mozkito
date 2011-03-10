package de.unisaarland.cs.st.reposuite.clustering;

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
	 * @see de.unisaarland.cs.st.reposuite.clustering.Cluster#getAllElements()
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
