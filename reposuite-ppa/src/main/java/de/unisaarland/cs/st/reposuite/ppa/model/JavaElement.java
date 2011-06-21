/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

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

import org.jdom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	
	private long              generatedId;
	
	private String            fullQualifiedName;
	
	private String            elementType;
	
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
		this.setFullQualifiedName(fullQualifiedName);
		this.setElementType(this.getClass().getCanonicalName());
		this.setElementType(elementType);
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
		JavaElement other = (JavaElement) obj;
		if (this.getElementType() == null) {
			if (other.getElementType() != null) {
				return false;
			}
		} else if (!this.getElementType().equals(other.getElementType())) {
			return false;
		}
		if (this.getFullQualifiedName() == null) {
			if (other.getFullQualifiedName() != null) {
				return false;
			}
		} else if (!this.getFullQualifiedName().equals(other.getFullQualifiedName())) {
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
	// @Column (name = "fullqualifiedname", nullable = false)
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
		int index = this.getFullQualifiedName().lastIndexOf(".");
		if (index > 0) {
			return this.getFullQualifiedName().substring(index);
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
		String[] nameParts = this.getFullQualifiedName().split("\\.");
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
		result = prime * result + ((this.getElementType() == null)
		                                                          ? 0
		                                                          : this.getElementType().hashCode());
		result = prime * result + ((this.getFullQualifiedName() == null)
		                                                                ? 0
		                                                                : this.getFullQualifiedName().hashCode());
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
		return "JavaElement [fullQualifiedName=" + this.getFullQualifiedName() + "]";
	}
}
