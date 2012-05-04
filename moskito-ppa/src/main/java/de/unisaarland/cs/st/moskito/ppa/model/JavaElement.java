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
/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package de.unisaarland.cs.st.moskito.ppa.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.jdom2.Element;

import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * The Class JavaElement.
 * 
 * @author Kim Herzig<kim@cs.uni-saarland.de>
 */
@Entity
@Inheritance (strategy = InheritanceType.JOINED)
@DiscriminatorColumn (name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue ("JAVAELEMENT")
@Table (uniqueConstraints = { @UniqueConstraint (columnNames = { "elementtype", "fullqualifiedname" }) })
public abstract class JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8960043672858454394L;
	
	public static String extractMethodName(final String fullQualifiedName) {
		Condition.check(fullQualifiedName.contains("."), "Full qualified method name must contain a '.' character");
		Condition.check(fullQualifiedName.contains("("), "Full qualified method name must contain a '(' character");
		Condition.check(fullQualifiedName.indexOf(".") < fullQualifiedName.indexOf("("),
		                "Full qualified method name must contain a '.' before '(' character");
		String result = fullQualifiedName.substring(0, fullQualifiedName.indexOf("("));
		result = result.substring(result.lastIndexOf("."));
		return result;
	}
	
	private long   generatedId;
	
	private String fullQualifiedName;
	
	private String elementType;
	
	/**
	 * Instantiates a new java element.
	 */
	protected JavaElement() {
		
	}
	
	/**
	 * Instantiates a new java element.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 */
	@NoneNull
	public JavaElement(final String fullQualifiedName, final String elementType) {
		setFullQualifiedName(fullQualifiedName);
		setElementType(this.getClass().getCanonicalName());
		setElementType(elementType);
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
		if (!(obj instanceof JavaElement)) {
			return false;
		}
		final JavaElement other = (JavaElement) obj;
		if (getElementType() == null) {
			if (other.getElementType() != null) {
				return false;
			}
		} else if (!getElementType().equals(other.getElementType())) {
			return false;
		}
		if (getFullQualifiedName() == null) {
			if (other.getFullQualifiedName() != null) {
				return false;
			}
		} else if (!getFullQualifiedName().equals(other.getFullQualifiedName())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the elementType
	 */
	// @Column (name = "elementtype", nullable = false)
	public String getElementType() {
		return this.elementType;
	}
	
	/**
	 * Gets the full qualified name.
	 * 
	 * @return the fullQualifiedName
	 */
	@Column (columnDefinition = "TEXT")
	public String getFullQualifiedName() {
		return this.fullQualifiedName;
	}
	
	@Id
	@GeneratedValue
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the package name.
	 * 
	 * @return the package name
	 */
	@Transient
	public String getPackageName() {
		final int index = getFullQualifiedName().lastIndexOf(".");
		if (index > 0) {
			return getFullQualifiedName().substring(index);
		}
		return "";
	}
	
	/**
	 * Gets the short name.
	 * 
	 * @return the shortName
	 */
	@Transient
	public String getShortName() {
		final String[] nameParts = getFullQualifiedName().split("\\.");
		return nameParts[nameParts.length - 1];
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @return the xML representation
	 */
	public abstract Element getXMLRepresentation();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getElementType() == null)
		                                                       ? 0
		                                                       : getElementType().hashCode());
		result = (prime * result) + ((getFullQualifiedName() == null)
		                                                             ? 0
		                                                             : getFullQualifiedName().hashCode());
		return result;
	}
	
	/**
	 * @param elementType
	 *            the elementType to set
	 */
	public void setElementType(final String elementType) {
		this.elementType = elementType;
	}
	
	/**
	 * Sets the full qualified name.
	 * 
	 * @param name
	 *            the new full qualified name
	 * @return the fullQualifiedName
	 */
	public void setFullQualifiedName(final String name) {
		this.fullQualifiedName = name;
	}
	
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + getFullQualifiedName() + "]";
	}
}
