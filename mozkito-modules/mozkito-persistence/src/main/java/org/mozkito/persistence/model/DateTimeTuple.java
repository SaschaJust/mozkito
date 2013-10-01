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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.joda.time.DateTime;

import org.mozkito.persistence.PersistentTuple;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class DateTimeTuple.
 */
@Embeddable
public class DateTimeTuple implements PersistentTuple<DateTime> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8037500943691455328L;
	
	/** The old value. */
	private DateTime          oldValue;
	
	/** The new value. */
	private DateTime          newValue;
	
	/**
	 * Instantiates a new date time tuple.
	 */
	protected DateTimeTuple() {
		
	}
	
	/**
	 * Instantiates a new date time tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	@NoneNull
	public DateTimeTuple(final Date oldValue, final Date newValue) {
		setOldValue(new DateTime(oldValue));
		setNewValue(new DateTime(newValue));
	}
	
	/**
	 * Instantiates a new date time tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	@NoneNull
	public DateTimeTuple(final DateTime oldValue, final DateTime newValue) {
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
		if (!(obj instanceof DateTimeTuple)) {
			return false;
		}
		final DateTimeTuple other = (DateTimeTuple) obj;
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
	@Override
	public String getClassName() {
		return JavaUtils.getHandle(DateTimeTuple.class);
	}
	
	/**
	 * Gets the java new value.
	 * 
	 * @return the newValue
	 */
	@Column (name = "newValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaNewValue() {
		return (getNewValue() != null
		                             ? getNewValue().toDate()
		                             : null);
	}
	
	/**
	 * Gets the java old value.
	 * 
	 * @return the oldValue
	 */
	@Column (name = "oldValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaOldValue() {
		return getOldValue() != null
		                            ? getOldValue().toDate()
		                            : null;
	}
	
	/**
	 * Gets the new value.
	 * 
	 * @return the newValue
	 */
	@Override
	@Transient
	public DateTime getNewValue() {
		return this.newValue;
	}
	
	/**
	 * Gets the old value.
	 * 
	 * @return the oldValue
	 */
	@Override
	@Transient
	public DateTime getOldValue() {
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
	 * Sets the java new value.
	 * 
	 * @param date
	 *            the new java new value
	 */
	protected void setJavaNewValue(final Date date) {
		setNewValue(date != null
		                        ? new DateTime(date)
		                        : null);
	}
	
	/**
	 * Sets the java old value.
	 * 
	 * @param date
	 *            the new java old value
	 */
	protected void setJavaOldValue(final Date date) {
		setOldValue(date != null
		                        ? new DateTime(date)
		                        : null);
	}
	
	/**
	 * Sets the new value.
	 * 
	 * @param newValue
	 *            the newValue to set
	 */
	@Override
	public void setNewValue(final DateTime newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * Sets the old value.
	 * 
	 * @param oldValue
	 *            the oldValue to set
	 */
	@Override
	public void setOldValue(final DateTime oldValue) {
		this.oldValue = oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("DateTimeTuple [old="); //$NON-NLS-1$
		builder.append(getOldValue());
		builder.append(", new="); //$NON-NLS-1$
		builder.append(getNewValue());
		builder.append("]"); //$NON-NLS-1$
		
		return builder.toString();
	}
	
}
