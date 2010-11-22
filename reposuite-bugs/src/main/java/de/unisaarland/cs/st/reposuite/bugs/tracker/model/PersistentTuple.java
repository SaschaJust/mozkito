/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;

import javax.persistence.Embeddable;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Embeddable
public class PersistentTuple<K, V> implements Annotated {
	
	private K first;
	private V second;
	
	protected PersistentTuple() {
	}
	
	/**
	 * @param first
	 * @param second
	 */
	public PersistentTuple(final K first, final V second) {
		setFirst(first);
		setSecond(second);
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
	public V getSecond() {
		return this.second;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param first
	 *            the first to set
	 */
	public void setFirst(final K first) {
		this.first = first;
	}
	
	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(final V second) {
		this.second = second;
	}
}
