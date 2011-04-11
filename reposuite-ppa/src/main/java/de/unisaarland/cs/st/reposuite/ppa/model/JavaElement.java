/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

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
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
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
		if (this.elementType == null) {
			if (other.elementType != null) {
				return false;
			}
		} else if (!this.elementType.equals(other.elementType)) {
			return false;
		}
		if (this.fullQualifiedName == null) {
			if (other.fullQualifiedName != null) {
				return false;
			}
		} else if (!this.fullQualifiedName.equals(other.fullQualifiedName)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the elementType
	 */
	@Id
	public String getElementType() {
		return this.elementType;
	}
	
	/**
	 * Gets the full qualified name.
	 * 
	 * @return the fullQualifiedName
	 */
	@Id
	public String getFullQualifiedName() {
		return this.fullQualifiedName;
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
		result = prime * result + ((this.elementType == null)
		                                                     ? 0
		                                                     : this.elementType.hashCode());
		result = prime * result + ((this.fullQualifiedName == null)
		                                                           ? 0
		                                                           : this.fullQualifiedName.hashCode());
		return result;
	}
	
	/**
	 * @param elementType the elementType to set
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + this.getFullQualifiedName() + "]";
	}
}
