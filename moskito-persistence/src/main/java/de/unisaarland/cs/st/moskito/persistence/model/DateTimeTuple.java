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
package de.unisaarland.cs.st.moskito.persistence.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.persistence.Annotated;

@Embeddable
public class DateTimeTuple implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8037500943691455328L;
	
	private DateTime          oldValue;
	private DateTime          newValue;
	
	/**
	 * 
	 */
	protected DateTimeTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public DateTimeTuple(final Date oldValue, final Date newValue) {
		setOldValue(new DateTime(oldValue));
		setNewValue(new DateTime(newValue));
	}
	
	/**
	 * @param oldValue
	 * @param newValue
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
		DateTimeTuple other = (DateTimeTuple) obj;
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
	@Column (name = "newValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaNewValue() {
		return (getNewValue() != null
		                             ? getNewValue().toDate()
		                             : null);
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	@Column (name = "oldValue")
	@Temporal (TemporalType.TIMESTAMP)
	protected Date getJavaOldValue() {
		return getOldValue() != null
		                            ? getOldValue().toDate()
		                            : null;
	}
	
	/**
	 * @return the newValue
	 */
	@Transient
	public DateTime getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
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
		result = prime * result + ((getNewValue() == null)
		                                                  ? 0
		                                                  : getNewValue().hashCode());
		result = prime * result + ((getOldValue() == null)
		                                                  ? 0
		                                                  : getOldValue().hashCode());
		return result;
	}
	
	/**
	 * @param date
	 */
	protected void setJavaNewValue(final Date date) {
		setNewValue(date != null
		                        ? new DateTime(date)
		                        : null);
	}
	
	/**
	 * @param date
	 */
	protected void setJavaOldValue(final Date date) {
		setOldValue(date != null
		                        ? new DateTime(date)
		                        : null);
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final DateTime newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final DateTime oldValue) {
		this.oldValue = oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DateTimeTuple [old=");
		builder.append(getOldValue());
		builder.append(", new=");
		builder.append(getNewValue());
		builder.append("]");
		return builder.toString();
	}
	
}
