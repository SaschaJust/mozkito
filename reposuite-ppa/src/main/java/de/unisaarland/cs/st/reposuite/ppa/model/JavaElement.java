/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
@IdClass (JavaElementPrimaryKey.class)
public abstract class JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8960043672858454394L;
	
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
	public JavaElement(final String fullQualifiedName) {
		setFullQualifiedName(fullQualifiedName);
		setElementType(this.getClass().getCanonicalName());
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
	@Id
	public String getElementType() {
		return elementType;
	}
	
	/**
	 * Gets the full qualified name.
	 * 
	 * @return the fullQualifiedName
	 */
	@Id
	public String getFullQualifiedName() {
		return fullQualifiedName;
	}
	
	/**
	 * Gets the package name.
	 * 
	 * @return the package name
	 */
	@Transient
	public String getPackageName() {
		int index = getFullQualifiedName().lastIndexOf(".");
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
		String[] nameParts = getFullQualifiedName().split("\\.");
		return nameParts[nameParts.length - 1];
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @param document
	 *            the document
	 * @return the xML representation
	 */
	public abstract Element getXMLRepresentation(Document document);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getElementType() == null)
		                                                     ? 0
		                                                     : getElementType().hashCode());
		result = prime * result + ((getFullQualifiedName() == null)
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
		fullQualifiedName = name;
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
