package net.ownhero.dev.andama.utils;

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
