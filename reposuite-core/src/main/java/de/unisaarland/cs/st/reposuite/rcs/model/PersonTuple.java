package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;

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
	public PersonTuple(final Person oldValue, final Person newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setFirst("oldValue", oldValue);
		setNewValue("newValue", newValue);
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	public PersonTuple(final PersonContainer oldValue, final PersonContainer newValue) {
		Condition.notNull(oldValue);
		Condition.notNull(newValue);
		
		setFirst(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * @return the newValue
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getOldValue() {
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
	public void setFirst(final PersonContainer oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * @param key
	 * @param oldValue
	 */
	@Transient
	public void setFirst(final String key, final Person oldValue) {
		this.oldValue = new PersonContainer();
		getOldValue().add(key, oldValue);
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
	public void setNewValue(final String key, final Person newValue) {
		this.newValue = new PersonContainer();
		getNewValue().add(key, newValue);
	}
	
}
