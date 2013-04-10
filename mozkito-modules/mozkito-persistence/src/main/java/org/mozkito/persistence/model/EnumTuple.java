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
import javax.persistence.Transient;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.persistence.PersistentTuple;

/**
 * The Class EnumTuple.
 */
@Embeddable
public class EnumTuple implements PersistentTuple<Enum<?>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7376689941623465731L;
	
	/** The old value. */
	private Enum<?>           oldValue;
	
	/** The old string value. */
	private String            oldStringValue;
	
	/** The new value. */
	private Enum<?>           newValue;
	
	/** The new string value. */
	private String            newStringValue;
	
	/** The enum class. */
	private Class<?>          enumClass;
	
	/** The enum class name. */
	private String            enumClassName;
	
	/**
	 * Instantiates a new enum tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	public EnumTuple(final Enum<?> oldValue, final Enum<?> newValue) {
		PRECONDITIONS: {
			if ((oldValue == null) && (newValue == null)) {
				throw new NullPointerException("Old and new value must not be null at the same time.");
			} else if ((oldValue != null) && (newValue != null) && (oldValue.getClass() != newValue.getClass())) {
				throw new IllegalArgumentException("Old and new value have to be of the same type.");
			}
		}
		
		try {
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.enumClass = (oldValue != null
			                                  ? oldValue
			                                  : newValue).getClass();
			
			SANITY: {
				assert this.enumClass != null;
			}
			
			this.enumClassName = this.enumClass.getCanonicalName();
			if (newValue != null) {
				this.newStringValue = newValue.name();
			}
			
			if (oldValue != null) {
				this.oldStringValue = oldValue.name();
			}
			
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.enumClass, "Field '%s' in '%s'.", "enumClass", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Convert enum.
	 * 
	 * @param enumClass
	 *            the enum class
	 * @param stringValue
	 *            the string value
	 * @return the enum
	 */
	private Enum<?> convertEnum(@NotNull final Class<?> enumClass,
	                            @NotNull final String stringValue) {
		final Object[] enumConstants = enumClass.getEnumConstants();
		final Enum<?>[] array = (Enum<?>[]) enumConstants;
		
		if (array != null) {
			for (final Enum<?> e : array) {
				if (e.name().toUpperCase().equals(stringValue.toUpperCase())) {
					return e;
				}
			}
		} else {
			throw UnrecoverableError.format("Data is in an inconsistent state. Trying to convert non-enum class '%s'.",
			                                enumClass.getCanonicalName());
		}
		return null;
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
		final EnumTuple other = (EnumTuple) obj;
		if (this.enumClassName == null) {
			if (other.enumClassName != null) {
				return false;
			}
		} else if (!this.enumClassName.equals(other.enumClassName)) {
			return false;
		}
		if (this.newStringValue == null) {
			if (other.newStringValue != null) {
				return false;
			}
		} else if (!this.newStringValue.equals(other.newStringValue)) {
			return false;
		}
		if (this.oldStringValue == null) {
			if (other.oldStringValue != null) {
				return false;
			}
		} else if (!this.oldStringValue.equals(other.oldStringValue)) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	@Transient
	public final String getClassName() {
		return JavaUtils.getHandle(EnumTuple.class);
	}
	
	/**
	 * Gets the enum class.
	 * 
	 * @return the enumClass
	 */
	@Transient
	public Class<?> getEnumClass() {
		if (this.enumClass == null) {
			if (getOldValue() != null) {
				this.enumClass = getOldValue().getClass();
			} else if (getNewValue() != null) {
				this.enumClass = getNewValue().getClass();
			} else if (getEnumClassName() != null) {
				try {
					this.enumClass = Class.forName(getEnumClassName());
				} catch (final ClassNotFoundException e) {
					throw new ClassLoadingError(e, getEnumClassName());
				}
			}
		}
		return this.enumClass;
	}
	
	/**
	 * Gets the enum class name.
	 * 
	 * @return the enumClassName
	 */
	@Basic
	protected String getEnumClassName() {
		return this.enumClassName;
	}
	
	/**
	 * Gets the new string value.
	 * 
	 * @return the newStringValue
	 */
	@Basic
	protected String getNewStringValue() {
		return this.newStringValue;
	}
	
	/**
	 * Gets the new value.
	 * 
	 * @return the newValue
	 */
	@Override
	@Transient
	public Enum<?> getNewValue() {
		if ((this.newValue == null) && (getNewStringValue() != null)) {
			this.newValue = convertEnum(getEnumClass(), getNewStringValue());
		}
		
		return this.newValue;
	}
	
	/**
	 * Gets the old string value.
	 * 
	 * @return the oldStringValue
	 */
	@Basic
	protected String getOldStringValue() {
		return this.oldStringValue;
	}
	
	/**
	 * Gets the old value.
	 * 
	 * @return the oldValue
	 */
	@Override
	@Transient
	public Enum<?> getOldValue() {
		if ((this.oldValue == null) && (getOldStringValue() != null)) {
			this.oldValue = convertEnum(getEnumClass(), getOldStringValue());
		}
		return this.oldValue;
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
		result = (prime * result) + ((this.enumClassName == null)
		                                                         ? 0
		                                                         : this.enumClassName.hashCode());
		result = (prime * result) + ((this.newStringValue == null)
		                                                          ? 0
		                                                          : this.newStringValue.hashCode());
		result = (prime * result) + ((this.oldStringValue == null)
		                                                          ? 0
		                                                          : this.oldStringValue.hashCode());
		return result;
	}
	
	/**
	 * Sets the enum class name.
	 * 
	 * @param enumClassName
	 *            the enumClassName to set
	 */
	protected void setEnumClassName(final String enumClassName) {
		this.enumClassName = enumClassName;
	}
	
	/**
	 * Sets the new string value.
	 * 
	 * @param newStringValue
	 *            the newStringValue to set
	 */
	protected void setNewStringValue(final String newStringValue) {
		this.newStringValue = newStringValue;
	}
	
	/**
	 * Sets the new value.
	 * 
	 * @param newValue
	 *            the newValue to set
	 */
	@Override
	public void setNewValue(final Enum<?> newValue) {
		if ((getOldValue() == null) && (newValue == null)) {
			throw new IllegalArgumentException("Old and new value must not be null at the same time.");
		}
		
		this.newValue = newValue;
		setNewStringValue(newValue != null
		                                  ? newValue.name()
		                                  : null);
	}
	
	/**
	 * Sets the old string value.
	 * 
	 * @param oldStringValue
	 *            the oldStringValue to set
	 */
	protected void setOldStringValue(final String oldStringValue) {
		this.oldStringValue = oldStringValue;
	}
	
	/**
	 * Sets the old value.
	 * 
	 * @param oldValue
	 *            the oldValue to set
	 */
	@Override
	public void setOldValue(final Enum<?> oldValue) {
		if ((getNewValue() == null) && (oldValue == null)) {
			throw new IllegalArgumentException("Old and new value must not be null at the same time.");
		}
		
		this.oldValue = oldValue;
		setOldStringValue(oldValue != null
		                                  ? oldValue.name()
		                                  : null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EnumTuple [class="); //$NON-NLS-1$
		builder.append(getEnumClassName());
		builder.append(", old="); //$NON-NLS-1$
		builder.append(getOldStringValue());
		builder.append(", new="); //$NON-NLS-1$
		builder.append(getNewStringValue());
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
