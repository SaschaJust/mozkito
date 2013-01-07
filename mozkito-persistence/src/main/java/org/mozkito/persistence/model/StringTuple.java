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

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.persistence.PersistentTuple;

/**
 * The Class StringTuple.
 */
@Embeddable
public class StringTuple implements PersistentTuple<String> {
	
	/** The old value. */
	private String            oldValue;
	
	/** The new value. */
	private String            newValue;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -268313983915960230L;
	
	/**
	 * Instantiates a new string tuple.
	 */
	protected StringTuple() {
		
	}
	
	/**
	 * Instantiates a new string tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
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
		final StringTuple other = (StringTuple) obj;
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
	@Transient
	public final String getHandle() {
		return JavaUtils.getHandle(StringTuple.class);
	}
	
	/**
	 * Gets the new value.
	 * 
	 * @return the newValue
	 */
	@Basic
	@Lob
	public String getNewValue() {
		return this.newValue;
	}
	
	/**
	 * Gets the old value.
	 * 
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
	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * Sets the old value.
	 * 
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
		final StringBuilder builder = new StringBuilder();
		builder.append("StringTuple [old="); //$NON-NLS-1$
		builder.append(getOldValue());
		builder.append(", new="); //$NON-NLS-1$
		builder.append(getNewValue());
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
