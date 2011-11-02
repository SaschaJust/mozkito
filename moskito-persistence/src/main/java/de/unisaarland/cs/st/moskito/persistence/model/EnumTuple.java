/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

@Embeddable
public class EnumTuple implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7376689941623465731L;
	private Enum<?>           oldValue;
	private String            oldStringValue;
	private Enum<?>           newValue;
	private String            newStringValue;
	private Class<?>          enumClass;
	private String            enumClassName;
	
	/**
	 * 
	 */
	protected EnumTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public EnumTuple(final Enum<?> oldValue, final Enum<?> newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
		setNewStringValue(newValue.name());
		setOldStringValue(oldValue.name());
		setEnumClass(oldValue.getClass());
		setEnumClassName(this.oldStringValue.getClass().getCanonicalName());
	}
	
	/**
	 * @param enumClass
	 * @param stringValue
	 * @return
	 */
	private Enum<?> convertEnum(final Class<?> enumClass,
	                            final String stringValue) {
		for (Enum<?> e : (Enum<?>[]) enumClass.getEnumConstants()) {
			if (e.name().equals(stringValue)) {
				return e;
			}
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
		EnumTuple other = (EnumTuple) obj;
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
	 * @return the enumClass
	 */
	@Transient
	public Class<?> getEnumClass() {
		return this.enumClass;
	}
	
	/**
	 * @return the enumClassName
	 */
	@Basic
	protected String getEnumClassName() {
		return this.enumClassName;
	}
	
	/**
	 * @return the newStringValue
	 */
	@Basic
	protected String getNewStringValue() {
		return this.newStringValue;
	}
	
	/**
	 * @return the newValue
	 */
	@Transient
	public Enum<?> getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldStringValue
	 */
	@Basic
	protected String getOldStringValue() {
		return this.oldStringValue;
	}
	
	/**
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
		result = prime * result + ((this.enumClass == null)
		                                                   ? 0
		                                                   : this.enumClass.hashCode());
		result = prime * result + ((this.newValue == null)
		                                                  ? 0
		                                                  : this.newValue.hashCode());
		result = prime * result + ((this.oldValue == null)
		                                                  ? 0
		                                                  : this.oldValue.hashCode());
		return result;
	}
	
	/**
	 * @param enumClass the enumClass to set
	 */
	@Transient
	public void setEnumClass(final Class<?> enumClass) {
		this.enumClass = enumClass;
	}
	
	/**
	 * @param className
	 */
	public void setEnumClass(final String className) {
		try {
			setEnumClass(Class.forName(className));
			Enum<?> _enum = convertEnum(getEnumClass(), getOldStringValue());
			
			if (_enum != null) {
				setOldValue(_enum);
			}
			
			_enum = convertEnum(getEnumClass(), getNewStringValue());
			
			if (_enum != null) {
				setNewValue(_enum);
			}
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param enumClassName the enumClassName to set
	 */
	private void setEnumClassName(final String enumClassName) {
		this.enumClassName = enumClassName;
	}
	
	/**
	 * @param newStringValue the newStringValue to set
	 */
	private void setNewStringValue(final String newStringValue) {
		this.newStringValue = newStringValue;
		if (getEnumClass() != null) {
			Enum<?> _enum = convertEnum(getEnumClass(), newStringValue);
			if (_enum != null) {
				setNewValue(_enum);
			}
		}
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final Enum<?> newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldStringValue the oldStringValue to set
	 */
	private void setOldStringValue(final String oldStringValue) {
		this.oldStringValue = oldStringValue;
		if (getEnumClass() != null) {
			Enum<?> _enum = convertEnum(getEnumClass(), oldStringValue);
			if (_enum != null) {
				setOldValue(_enum);
			}
		}
	}
	
	/**
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
		StringBuilder builder = new StringBuilder();
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
