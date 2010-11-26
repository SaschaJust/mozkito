package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

@Embeddable
public class StringTuple implements Annotated {
	
	private String            oldValue;
	private String            newValue;
	
	/**
     * 
     */
	private static final long serialVersionUID = -268313983915960230L;
	
	/**
	 * 
	 */
	protected StringTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public StringTuple(final String oldValue, final String newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	@Lob
	public String getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	@Lob
	public String getOldValue() {
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
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}
	
}
