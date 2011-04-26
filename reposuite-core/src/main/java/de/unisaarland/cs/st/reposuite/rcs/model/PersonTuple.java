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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PersonTuple)) {
			return false;
		}
		PersonTuple other = (PersonTuple) obj;
		if (this.newValue == null) {
			if (other.newValue != null) {
				return false;
			}
		} else if (!this.newValue.equals(other.newValue)) {
			return false;
		}
		if (this.oldValue == null) {
			if (other.oldValue != null) {
				return false;
			}
		} else if (!this.oldValue.equals(other.oldValue)) {
			return false;
		}
		return true;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.newValue == null)
		                                                  ? 0
		                                                  : this.newValue.hashCode());
		result = prime * result + ((this.oldValue == null)
		                                                  ? 0
		                                                  : this.oldValue.hashCode());
		return result;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PersonTuple [old=");
		builder.append(this.oldValue);
		builder.append(", new=");
		builder.append(this.newValue);
		builder.append("]");
		return builder.toString();
	}
	
}
