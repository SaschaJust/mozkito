package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

@Embeddable
public class LongTuple implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = -8692461784697718949L;
	private Long              oldValue;
	private Long              newValue;
	
	/**
	 * 
	 */
	protected LongTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public LongTuple(final Long oldValue, final Long newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setFirst(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	public Long getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	public Long getOldValue() {
		return this.oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setFirst(final Long oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Long newValue) {
		this.newValue = newValue;
	}
	
}
