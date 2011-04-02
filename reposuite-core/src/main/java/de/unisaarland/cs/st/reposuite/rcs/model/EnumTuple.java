package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	@NoneNull
	public EnumTuple(final Enum<?> oldValue, final Enum<?> newValue) {
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
