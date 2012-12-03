/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.persistence.model;

import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;

import org.mozkito.persistence.Annotated;

/**
 * The Class PersonTuple.
 */
@Embeddable
public class PersonTuple implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8692461784697718949L;
	
	/** The old value. */
	private PersonContainer   oldValue;
	
	/** The new value. */
	private PersonContainer   newValue;
	
	/**
	 * Instantiates a new person tuple.
	 */
	protected PersonTuple() {
		
	}
	
	/**
	 * Instantiates a new person tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	public PersonTuple(final Person oldValue, final Person newValue) {
		CollectionCondition.notAllNull(Arrays.asList(new Person[] { oldValue, newValue }),
		                               "A PersonTuple cannot contain NULL as new and old value. Got newValue=%s, oldValue=%s.",
		                               newValue, oldValue);
		setOldValue("oldValue", oldValue);
		setNewValue("newValue", newValue);
	}
	
	/**
	 * Instantiates a new person tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	@NoneNull
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
		final PersonTuple other = (PersonTuple) obj;
		if (getNewValue() == null) {
			if (other.getNewValue() != null) {
				return false;
			}
		} else if (!getNewValue().equals(other.getNewValue())) {
			return false;
		}
		if (getOldValue() == null) {
			if (other.getOldValue() != null) {
				return false;
			}
		} else if (!getOldValue().equals(other.getOldValue())) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	public final String getHandle() {
		return JavaUtils.getHandle(PersonTuple.class);
	}
	
	/**
	 * Gets the new value.
	 * 
	 * @return the newValue
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getNewValue() {
		return this.newValue;
	}
	
	/**
	 * Gets the old value.
	 * 
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
		result = (prime * result) + ((getNewValue() == null)
		                                                    ? 0
		                                                    : getNewValue().hashCode());
		result = (prime * result) + ((getOldValue() == null)
		                                                    ? 0
		                                                    : getOldValue().hashCode());
		return result;
	}
	
	/**
	 * Sets the new value.
	 * 
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final PersonContainer newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * Sets the new value.
	 * 
	 * @param key
	 *            the key
	 * @param newValue
	 *            the new value
	 */
	@Transient
	public void setNewValue(final String key,
	                        final Person newValue) {
		this.newValue = new PersonContainer();
		getNewValue().add(key, newValue);
	}
	
	/**
	 * Sets the old value.
	 * 
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final PersonContainer oldValue) {
		this.oldValue = oldValue;
	}
	
	/**
	 * Sets the old value.
	 * 
	 * @param key
	 *            the key
	 * @param oldValue
	 *            the old value
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
		final StringBuilder builder = new StringBuilder();
		builder.append("PersonTuple [old=");
		builder.append(getOldValue());
		builder.append(", new=");
		builder.append(getNewValue());
		builder.append("]");
		return builder.toString();
	}
	
}
