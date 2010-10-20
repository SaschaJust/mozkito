package de.unisaarland.cs.st.reposuite.utils;

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
	
}
