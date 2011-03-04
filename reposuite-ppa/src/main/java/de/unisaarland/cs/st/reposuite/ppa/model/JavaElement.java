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
 * 
 * @author kim
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class JavaElement implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID  = -8960043672858454394L;
	private String            shortName         = "<unknown>";
	private JavaElementPrimaryKey primaryKey;
	
	@NoneNull
	public JavaElement(final String fullQualifiedName) {
		String[] nameParts = fullQualifiedName.split("\\.");
		this.shortName = nameParts[nameParts.length - 1];
		this.setPrimaryKey(new JavaElementPrimaryKey(fullQualifiedName, this.getClass()));
	}
	
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
	 * @return the fullQualifiedName
	 */
	@Transient
	public String getFullQualifiedName() {
		return this.primaryKey.getFullQualifiedName();
	}
	
	@Transient
	public String getPackageName() {
		int index = this.getFullQualifiedName().lastIndexOf(".");
		if (index > 0) {
			return this.getFullQualifiedName().substring(index);
		}
		return "";
	}
	
	@EmbeddedId
	public JavaElementPrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return this.shortName;
	}
	
	public abstract Element getXMLRepresentation(Document document);
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getFullQualifiedName() == null) ? 0 : this.getFullQualifiedName().hashCode());
		return result;
	}
	
	/**
	 * @return the fullQualifiedName
	 */
	@Transient
	protected void setFullQualifiedName(final String name) {
		this.primaryKey.setFullQualifiedName(name);
	}
	
	private void setPrimaryKey(final JavaElementPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	@SuppressWarnings("unused")
	private void setShortName(final String shortName) {
		this.shortName = shortName;
	}
	
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + this.getFullQualifiedName() + ", shortName=" + this.shortName + "]";
	}
}
