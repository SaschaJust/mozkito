package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	
}
