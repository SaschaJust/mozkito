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
package org.mozkito.persons.model;

import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;

import org.mozkito.persistence.PersistentTuple;

/**
 * The Class PersonTuple.
 */
@Entity
public class PersonTuple implements PersistentTuple<Person> {
	
	private long              generatedId;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8692461784697718949L;
	
	/** The old value. */
	private PersonContainer   container        = new PersonContainer();
	
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
		                               "A PersonTuple cannot contain NULL as new and old value. Got newValue=%s, oldValue=%s.", //$NON-NLS-1$
		                               newValue, oldValue);
		setOldValue(oldValue);
		setNewValue(newValue);
	}
	
	/**
	 * {@inheritDoc}
	 * 
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PersonTuple other = (PersonTuple) obj;
		if (this.container == null) {
			if (other.container != null) {
				return false;
			}
		} else if (!this.container.equals(other.container)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.PersistentTuple#getClassName()
	 */
	@Override
	@Transient
	public final String getClassName() {
		return JavaUtils.getHandle(PersonTuple.class);
	}
	
	/**
	 * Gets the container.
	 * 
	 * @return the container
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	protected PersonContainer getContainer() {
		return this.container;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public final long getGeneratedId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.generatedId;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.PersistentTuple#getNewValue()
	 */
	@Override
	@Transient
	public Person getNewValue() {
		return this.container.get("new"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.PersistentTuple#getOldValue()
	 */
	@Override
	@Transient
	public Person getOldValue() {
		return this.container.get("old"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.container == null)
		                                                     ? 0
		                                                     : this.container.hashCode());
		return result;
	}
	
	/**
	 * Sets the container.
	 * 
	 * @param container
	 *            the container to set
	 */
	protected final void setContainer(final PersonContainer container) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.container = container;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	public final void setGeneratedId(final long generatedId) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.generatedId = generatedId;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.PersistentTuple#setNewValue(java.lang.Object)
	 */
	@Override
	public void setNewValue(final Person newValue) {
		this.container.add("new", newValue); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.PersistentTuple#setOldValue(java.lang.Object)
	 */
	@Override
	public void setOldValue(final Person oldValue) {
		this.container.add("old", oldValue); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PersonTuple [old="); //$NON-NLS-1$
		builder.append(getOldValue());
		builder.append(", new="); //$NON-NLS-1$
		builder.append(getNewValue());
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
