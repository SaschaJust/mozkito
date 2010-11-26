package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

@Embeddable
public class EnumTuple implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = -7376689941623465731L;
	private Enum<?>           oldValue;
	private Enum<?>           newValue;
	
	/**
	 * 
	 */
	protected EnumTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public EnumTuple(final Enum<?> oldValue, final Enum<?> newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@Enumerated (EnumType.ORDINAL)
	public Enum<?> getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Enumerated (EnumType.ORDINAL)
	public Enum<?> getOldValue() {
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
	public void setNewValue(final Enum<?> newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final Enum<?> oldValue) {
		this.oldValue = oldValue;
	}
	
}
