/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

/**
 * The Class JavaElementDefinition.
 * 
 * @author kim
 */
@Entity
@ForeignKey(name = "JAVA_ELEM_DEF")
public abstract class JavaElementDefinition extends JavaElement implements Annotated {
	
	/**
	 * 
	 */
	private static final long                    serialVersionUID = 1535115107166147270L;
	
	/** The parent. */
	private JavaElementDefinition                parent           = null;
	
	/** The children. */
	protected Map<String, JavaElementDefinition> children         = new HashMap<String, JavaElementDefinition>();
	
	
	
	/**
	 * Instantiates a new java element definition.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param filePath
	 *            the file path
	 * @param timestamp
	 *            the timestamp
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param parent
	 *            the parent
	 */
	public JavaElementDefinition(@NotNull final String fullQualifiedName, final JavaElementDefinition parent) {
		super(fullQualifiedName);
		this.parent = parent;
	}
	
	/**
	 * Adds the child.
	 * 
	 * @param child
	 *            the child
	 * @return true, if successful
	 */
	@Transient
	public boolean addChild(final JavaElementDefinition child) {
		if (this.children.containsKey(child.getFullQualifiedName())) {
			return false;
		}
		this.children.put(child.getFullQualifiedName(), child);
		return true;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#equals(java.lang
	 * .Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JavaElementDefinition other = (JavaElementDefinition) obj;
		if (this.parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!this.parent.equals(other.parent)) {
			return false;
		}
		return true;
	}
	
	

	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	@ElementCollection
	@JoinTable(name = "children", joinColumns = { @JoinColumn(name = "child_id", nullable = false) })
	public Collection<JavaElementDefinition> getChildren() {
		return this.children.values();
	}
	
	
	
	/**
	 * Gets the package name.
	 * 
	 * @return the package name
	 */
	@Override
	@Transient
	public String getPackageName() {
		if (!super.getFullQualifiedName().contains(".")) {
			return "";
		} else {
			int index = super.getFullQualifiedName().lastIndexOf(".");
			return super.getFullQualifiedName().substring(0, index);
		}
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	protected JavaElementDefinition getParent() {
		return this.parent;
	}
	
	@Transient
	public abstract JavaElementDefinition getTypedParent();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.ppa.model.JavaElement#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.parent == null) ? 0 : this.parent.hashCode());
		return result;
	}
	
	
	
	/**
	 * Sets the children.
	 * 
	 * @param children
	 *            the children
	 */
	@SuppressWarnings("unused")
	private void setChildren(final Map<String, JavaElementDefinition> children) {
		this.children = children;
	}
	
	
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the parent
	 * @return true, if successful
	 */
	@Transient
	public boolean setParent(final JavaClassDefinition parent) {
		if (this.parent == null) {
			this.parent = parent;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
	@SuppressWarnings("unused")
	private void setParent(final JavaElementDefinition parent) {
		this.parent = parent;
	}
	
}
