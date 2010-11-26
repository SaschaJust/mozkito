package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

@Embeddable
public class IntegerTuple implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = -7376689941623465731L;
	private Integer           oldValue;
	private Integer           newValue;
	
	/**
	 * 
	 */
	protected IntegerTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public IntegerTuple(final Integer oldValue, final Integer newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setFirst(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	public Integer getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	public Integer getOldValue() {
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
	public void setFirst(final Integer oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Integer newValue) {
		this.newValue = newValue;
	}
	
}
