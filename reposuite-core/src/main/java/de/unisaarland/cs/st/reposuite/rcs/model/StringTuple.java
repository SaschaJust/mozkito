package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	@NoneNull
	public StringTuple(final String oldValue, final String newValue) {
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
		if (!(obj instanceof StringTuple)) {
			return false;
		}
		StringTuple other = (StringTuple) obj;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StringTuple [old=");
		builder.append(this.oldValue);
		builder.append(", new=");
		builder.append(this.newValue);
		builder.append("]");
		return builder.toString();
	}
	
}
