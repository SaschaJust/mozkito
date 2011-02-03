/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

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
	
	protected String          fullQualifiedName = "<unknown>";
	
	private String            shortName         = "<unknown>";
	
	@NoneNull
	public JavaElement(final String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
		String[] nameParts = fullQualifiedName.split("\\.");
		this.shortName = nameParts[nameParts.length - 1];
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
	 * @return the fullQualifiedName
	 */
	@Id
	public String getFullQualifiedName() {
		return this.fullQualifiedName;
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
		result = prime * result + ((this.fullQualifiedName == null) ? 0 : this.fullQualifiedName.hashCode());
		return result;
	}
	
	@SuppressWarnings("unused")
	private void setFullQualifiedName(final String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
	}
	
	@SuppressWarnings("unused")
	private void setShortName(final String shortName) {
		this.shortName = shortName;
	}
	
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + this.fullQualifiedName + ", shortName=" + this.shortName + "]";
	}
}
