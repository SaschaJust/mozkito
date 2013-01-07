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
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.persistence.Annotated;

/**
 * The Class EnumTuple.
 */
@Embeddable
public class EnumTuple implements Annotated {
	
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
	 */
	protected EnumTuple() {
		
	}
	
	/**
	 * Instantiates a new enum tuple.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	@NoneNull
	public EnumTuple(final Enum<?> oldValue, final Enum<?> newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
		setNewStringValue(newValue.name());
		setOldStringValue(oldValue.name());
		setEnumClass(oldValue.getClass());
		setEnumClassName(this.oldValue.getClass().getCanonicalName());
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
		if (!(obj instanceof EnumTuple)) {
			return false;
		}
		final EnumTuple other = (EnumTuple) obj;
		if (this.enumClass == null) {
			if (other.enumClass != null) {
				return false;
			}
		} else if (!this.enumClass.equals(other.enumClass)) {
			return false;
		}
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
	 * Gets the enum class.
	 * 
	 * @return the enumClass
	 */
	@Transient
	public Class<?> getEnumClass() {
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Transient
	public final String getHandle() {
		return JavaUtils.getHandle(EnumTuple.class);
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
	@Transient
	public Enum<?> getNewValue() {
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
	@Transient
	public Enum<?> getOldValue() {
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
		result = (prime * result) + ((this.enumClass == null)
		                                                     ? 0
		                                                     : this.enumClass.hashCode());
		result = (prime * result) + ((this.newValue == null)
		                                                    ? 0
		                                                    : this.newValue.hashCode());
		result = (prime * result) + ((this.oldValue == null)
		                                                    ? 0
		                                                    : this.oldValue.hashCode());
		return result;
	}
	
	/**
	 * Sets the enum class.
	 * 
	 * @param enumClass
	 *            the enumClass to set
	 */
	@Transient
	public void setEnumClass(final Class<?> enumClass) {
		this.enumClass = enumClass;
	}
	
	/**
	 * Sets the enum class.
	 * 
	 * @param className
	 *            the new enum class
	 */
	public void setEnumClass(final String className) {
		try {
			setEnumClass(Class.forName(className));
			
			Enum<?> _enum = null;
			if (this.oldStringValue != null) {
				_enum = convertEnum(getEnumClass(), getOldStringValue());
				
				if (_enum != null) {
					setOldValue(_enum);
				}
			}
			
			if (this.newStringValue != null) {
				_enum = convertEnum(getEnumClass(), getNewStringValue());
				
				if (_enum != null) {
					setNewValue(_enum);
				}
			}
		} catch (final ClassNotFoundException e) {
			throw new ClassLoadingError(e, className);
		}
	}
	
	/**
	 * Sets the enum class name.
	 * 
	 * @param enumClassName
	 *            the enumClassName to set
	 */
	private void setEnumClassName(final String enumClassName) {
		this.enumClassName = enumClassName;
		setEnumClass(enumClassName);
	}
	
	/**
	 * Sets the new string value.
	 * 
	 * @param newStringValue
	 *            the newStringValue to set
	 */
	private void setNewStringValue(final String newStringValue) {
		this.newStringValue = newStringValue;
		if (getEnumClass() != null) {
			final Enum<?> _enum = convertEnum(getEnumClass(), newStringValue);
			if (_enum != null) {
				setNewValue(_enum);
			}
		}
	}
	
	/**
	 * Sets the new value.
	 * 
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Enum<?> newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * Sets the old string value.
	 * 
	 * @param oldStringValue
	 *            the oldStringValue to set
	 */
	private void setOldStringValue(final String oldStringValue) {
		this.oldStringValue = oldStringValue;
		if (getEnumClass() != null) {
			final Enum<?> _enum = convertEnum(getEnumClass(), oldStringValue);
			if (_enum != null) {
				setOldValue(_enum);
			}
		}
	}
	
	/**
	 * Sets the old value.
	 * 
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final Enum<?> oldValue) {
		this.oldValue = oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EnumTuple [class=");
		builder.append(getEnumClassName());
		builder.append(", old=");
		builder.append(getOldStringValue());
		builder.append(", new=");
		builder.append(getNewStringValue());
		builder.append("]");
		return builder.toString();
	}
	
}
