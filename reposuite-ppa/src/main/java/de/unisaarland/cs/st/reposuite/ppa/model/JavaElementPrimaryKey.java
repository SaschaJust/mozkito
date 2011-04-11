package de.unisaarland.cs.st.reposuite.ppa.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * The Class JavaElementPrimaryKey.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JavaElementPrimaryKey implements Annotated, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3377913237574041596L;
	
	/** The full qualified name. */
	private String            fullQualifiedName;
	
	/** The element type. */
	private String            elementType;
	
	public JavaElementPrimaryKey() {
		
	}
	
	/**
	 * Instantiates a new java element primary key.
	 * 
	 * @param fullQualifiedName
	 *            the full qualified name
	 * @param clazz
	 *            the clazz
	 */
	public JavaElementPrimaryKey(final String fullQualifiedName, final Class<? extends JavaElement> clazz) {
		this.setFullQualifiedName(fullQualifiedName);
		this.setElementType(clazz.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if ((o != null) && (o instanceof JavaElementPrimaryKey) && (o.hashCode() == this.hashCode())) {
			JavaElementPrimaryKey anotherKey = (JavaElementPrimaryKey) o;
			return anotherKey.fullQualifiedName.equals(this.fullQualifiedName)
			        && anotherKey.elementType.equals(this.elementType);
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the element type.
	 * 
	 * @return the element type
	 */
	public String getElementType() {
		return this.elementType;
	}
	
	/**
	 * Gets the full qualified name.
	 * 
	 * @return the full qualified name
	 */
	public String getFullQualifiedName() {
		return this.fullQualifiedName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getFullQualifiedName().hashCode() + 13 * this.getElementType().hashCode();
	}
	
	/**
	 * Sets the element type.
	 * 
	 * @param type
	 *            the new element type
	 */
	private void setElementType(final String type) {
		this.elementType = type;
	}
	
	/**
	 * Sets the full qualified name.
	 * 
	 * @param name
	 *            the new full qualified name
	 */
	protected void setFullQualifiedName(final String name) {
		this.fullQualifiedName = name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("fullQualifiedName",
		                                                                        this.getFullQualifiedName())
		                                                                .append("elementType", this.getElementType())
		                                                                .toString();
	}
}
