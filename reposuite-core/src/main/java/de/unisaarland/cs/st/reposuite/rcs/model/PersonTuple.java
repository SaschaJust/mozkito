package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class PersonTuple implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8692461784697718949L;
	private PersonContainer   oldValue;
	private PersonContainer   newValue;
	
	/**
	 * 
	 */
	protected PersonTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public PersonTuple(final Person oldValue, final Person newValue) {
		setOldValue("oldValue", oldValue);
		setNewValue("newValue", newValue);
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public PersonTuple(final PersonContainer oldValue, final PersonContainer newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getOldValue() {
		return this.oldValue;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final PersonContainer newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param key
	 * @param newValue
	 */
	@Transient
	public void setNewValue(final String key,
	                        final Person newValue) {
		this.newValue = new PersonContainer();
		getNewValue().add(key, newValue);
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final PersonContainer oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * @param key
	 * @param oldValue
	 */
	@Transient
	public void setOldValue(final String key,
	                        final Person oldValue) {
		this.oldValue = new PersonContainer();
		getOldValue().add(key, oldValue);
	}
	
}
