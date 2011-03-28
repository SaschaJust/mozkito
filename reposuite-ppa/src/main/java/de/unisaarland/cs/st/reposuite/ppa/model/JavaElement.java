/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

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
	
	/** The parents. */
	private Map<JavaElement, JavaElementRelation> parentRelations  = new HashMap<JavaElement, JavaElementRelation>();
	
	/** The child relations. */
	private Map<JavaElement, JavaElementRelation>       childRelations   = new HashMap<JavaElement, JavaElementRelation>();
	
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
	
	/**
	 * Adds the child relation. The method assumes that the passed relation can
	 * be added without further merging!
	 * 
	 * @param rel
	 *            the rel
	 */
	protected void addChildRelation(JavaElementRelation childRelation){
		if (!childRelations.containsKey(childRelation.getChild())) {
			childRelations.put(childRelation.getChild(), childRelation);
		}
	}
	
	/**
	 * Adds a parent relation containing this element as child and the given
	 * other element as parent.
	 * 
	 * @param child
	 *            the child
	 * @return the java element relation
	 */
	@NoneNull
	@Transient
	public JavaElementRelation addParent(JavaElement parent) {
		if (getParentRelations().containsKey(parent)) {
			return this.getParentRelations().get(parent);
		} else {
			JavaElementRelation rel = new JavaElementRelation(parent, this);
			getParentRelations().put(parent, rel);
			parent.addChildRelation(rel);
			return rel;
		}
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
	 * Gets the child relations.
	 * 
	 * @return the child relations
	 */
	@OneToMany (cascade = {}, fetch = FetchType.LAZY)
	@MapKey (name = "child")
	public Map<JavaElement, JavaElementRelation> getChildRelations() {
		return childRelations;
	}
	
	/**
	 * Gets the child relations.
	 * 
	 * @param when
	 *            the when
	 * @return the child relations
	 */
	@Transient
	public Set<JavaElement> getChildRelations(RCSTransaction when) {
		Set<JavaElement> result = new HashSet<JavaElement>();
		for (JavaElementRelation child : this.getChildRelations().values()) {
			if (child.isValid(when)) {
				result.add(child.getChild());
			}
		}
		return result;
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
	
	@OneToMany (cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@MapKey (name = "parent")
	public Map<JavaElement, JavaElementRelation> getParentRelations() {
		return parentRelations;
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
	 * Sets the child relations.
	 * 
	 * @param relations
	 *            the new child relations
	 */
	@SuppressWarnings ("unused")
	private void setChildRelations(Map<JavaElement, JavaElementRelation> relations) {
		this.childRelations = relations;
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
	
	@SuppressWarnings ("unused")
	private void setParentRelations(Map<JavaElement, JavaElementRelation> parentRelations) {
		this.parentRelations = parentRelations;
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
