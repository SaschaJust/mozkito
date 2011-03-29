/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
public abstract class JavaElement implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long                           serialVersionUID = -8960043672858454394L;
	
	/** The primary key. */
	private JavaElementPrimaryKey                       primaryKey;
	
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
		this.setPrimaryKey(new JavaElementPrimaryKey(fullQualifiedName, this.getClass()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		JavaElement other = (JavaElement) obj;
		if (primaryKey == null) {
			if (other.primaryKey != null) return false;
		} else if (!primaryKey.equals(other.primaryKey)) return false;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((primaryKey == null)
				? 0
				: primaryKey.hashCode());
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + this.getFullQualifiedName() + "]";
	}
}
