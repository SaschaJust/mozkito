/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class JavaElement.
 * 
 * @author kim
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID  = -8960043672858454394L;
	
	/** The short name. */
	private String            shortName         = "<unknown>";
	
	/** The primary key. */
	private JavaElementPrimaryKey primaryKey;
	
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
		String[] nameParts = fullQualifiedName.split("\\.");
		this.shortName = nameParts[nameParts.length - 1];
		this.setPrimaryKey(new JavaElementPrimaryKey(fullQualifiedName, this.getClass()));
	}
	
	/* (non-Javadoc)
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
		JavaElement other = (JavaElement) obj;
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
	 * Gets the full qualified name.
	 * 
	 * @return the fullQualifiedName
	 */
	@Transient
	public String getFullQualifiedName() {
		return this.primaryKey.getFullQualifiedName();
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
	 * Gets the primary key.
	 * 
	 * @return the primary key
	 */
	@EmbeddedId
	public JavaElementPrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}
	
	/**
	 * Gets the short name.
	 * 
	 * @return the shortName
	 */
	public String getShortName() {
		return this.shortName;
	}
	
	/**
	 * Gets the xML representation.
	 * 
	 * @param document
	 *            the document
	 * @return the xML representation
	 */
	public abstract Element getXMLRepresentation(Document document);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getFullQualifiedName() == null) ? 0 : this.getFullQualifiedName().hashCode());
		return result;
	}
	
	/**
	 * Sets the full qualified name.
	 * 
	 * @param name
	 *            the new full qualified name
	 * @return the fullQualifiedName
	 */
	@Transient
	protected void setFullQualifiedName(final String name) {
		this.primaryKey.setFullQualifiedName(name);
	}
	
	/**
	 * Sets the primary key.
	 * 
	 * @param primaryKey
	 *            the new primary key
	 */
	private void setPrimaryKey(final JavaElementPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * Sets the short name.
	 * 
	 * @param shortName
	 *            the new short name
	 */
	@SuppressWarnings("unused")
	private void setShortName(final String shortName) {
		this.shortName = shortName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + this.getFullQualifiedName() + ", shortName=" + this.shortName + "]";
	}
}
