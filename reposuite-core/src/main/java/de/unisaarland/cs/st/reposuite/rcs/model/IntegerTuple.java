package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	@NoneNull
	public IntegerTuple(final Integer oldValue, final Integer newValue) {
		setOldValue(oldValue);
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
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Integer newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final Integer oldValue) {
		this.oldValue = oldValue;
	}
	
}
